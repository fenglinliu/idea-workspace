package cn.bookcycle.messagequeue.util;

import cn.bookcycle.messagequeue.constant.Constants;
import com.rabbitmq.client.*;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Queue
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
public class Queue {

    private static final Logger LOGGER = LoggerFactory.getLogger(Queue.class);

    private static final String TOPIC_EXCHANGE_TYPE = "topic";

    private static final int QUEUE_DEFAULT_NUMBER = 5;

    private static final double QUEUE_NUMBER_LIMIT_FACTORY = 0.75;

    private static final double QUEUE_NUMBER_RESIZE_FACTORY = 0.5;

    private static  int ORIGINAL_START = 1;

    private static int ORIGINAL_END = QUEUE_DEFAULT_NUMBER;

    private CachingConnectionFactory  connectionFactory4Template;

    private AmqpTemplate rabbitTemplate;

    private ConnectionFactory connectionFactory4Chanel;

    private Connection connection;

    private void init() {
        connectionFactory4Template = new CachingConnectionFactory(Constants.IP);
        connectionFactory4Template.setUsername("admin");
        connectionFactory4Template.setPassword("1234");
        connectionFactory4Template.setPort(5672);
        rabbitTemplate = new RabbitTemplate(connectionFactory4Template);

        connectionFactory4Chanel = new ConnectionFactory();
        connectionFactory4Chanel.setHost(Constants.IP);
        connectionFactory4Chanel.setUsername("admin");
        connectionFactory4Chanel.setPassword("1234");
        connectionFactory4Chanel.setPort(5672);
    }

    public Queue() {
        init();
        LOGGER.info("Queue - connectionFactory4Chanel:{}", connectionFactory4Chanel);
        try {
//            connection = connectionFactory.newConnection();
            connection = connectionFactory4Chanel.newConnection();
        } catch (Exception e) {
            LOGGER.error("Queue - connectionFactory4Chanel:{}, create queue error", connectionFactory4Chanel);
        }

    }

    public int defaultQueueNumber() {
        return QUEUE_DEFAULT_NUMBER;
    }

    public void logoutQueue() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                LOGGER.error("logoutQueue - {}", e.getStackTrace());
            }

        }
    }


    public void createExchange(String exchange) {
        if (connection != null && exchange != null) {
            Channel channel = null;
            // 创建一个type=direct 持久化的 非自动删除的交换器
            try {
                channel = connection.createChannel();
                channel.exchangeDeclare(exchange, TOPIC_EXCHANGE_TYPE, true, false, null);
                channel.close();
            } catch (Exception e) {
                LOGGER.error("createExchange - {}", e.getStackTrace());
            }

        } else {
            LOGGER.error("createExchange - connection is {}, exchange is {}", connection, exchange);
        }
    }

    public boolean firstCreateQueue(String exchange) {
        try {
            for (int i = 0; i < QUEUE_DEFAULT_NUMBER + 1; ++i) {
                LOGGER.info("firstCreateQueue - start to create {} queue", i);
                String queueName = IDAndNameProducer.newQueueName(exchange, i);
                if (i == 0) {
                    // 0号队列用于记录，所以它的绑定键可以接收任何消息（包括普通消息和用于扩容的消息）
                    createQueue(queueName, exchange, exchange + ".#");
                } else {
                    // 其他队列只接收普通消息
                    createQueue(queueName, exchange, exchange + ".oridinary.*");
                }
            }
        } catch (Exception e) {
            LOGGER.error("firstCreateQueue - {}", e.getStackTrace());
            return  false;
        }
        return true;
    }

    public void createQueueBelongToBrotherExchange(String exchange, int brotherExchangeIndex) {
        int queueNumber = computeBrotherExchangeQueueSize(brotherExchangeIndex);
        String brotherExchange = exchange + "_" + brotherExchangeIndex;
        // 循环创建队列并绑定到brotherExchange
        int startQueueIndex = computeQueueStart(brotherExchangeIndex);
        for (int i = 0, index = startQueueIndex; i < queueNumber; ++i, ++index) {
            String queueName = IDAndNameProducer.newQueueName(exchange, index);
            createQueue(queueName, brotherExchange, brotherExchange + ".oridinary.*");
        }

    }

    public int  resizeQueueNumber(String exchage, int queueNumber, int usedQueueNumber) {
        if (usedQueueNumber  > queueNumber * QUEUE_NUMBER_LIMIT_FACTORY) {
            // 空闲队列数量小于阈值，扩容
            int increasedQueueNumber = (int) (queueNumber * QUEUE_NUMBER_RESIZE_FACTORY);
            // 获取exchange中index=0的队列名字
            String queueName = IDAndNameProducer.newQueueName(exchage, 0);
            // 取index=0这个记录队列中的所有消息
            List<String> msgRecord = new ArrayList<String>();
            String msg = null;
            do {
                msg = pullMessage(queueName);
                if (msg != null)
                    msgRecord.add(msg);
            } while (msg != null);
            // 创建新的exchange，并给这个exchange分配n个队列（注意这个新exchange的命名规则：exchange_1）
            int nextBrotherExchangeIndex = computeNextBrotherExchangeIndex(queueNumber);
            String brotherExchange = exchage + "_" + nextBrotherExchangeIndex;
            createExchange(brotherExchange);
            createQueueBelongToBrotherExchange(exchage, nextBrotherExchangeIndex);
            // 把记录队列中的所有消息发送到index=0队列和新的exchange中
            for (int i = 0; i < msgRecord.size(); ++i) {
                // 发送给index=0队列
                putMessage(exchage, exchage + "." + new Random().nextInt(), msgRecord.get(i));
                // 发送给新的exchange
                putMessage(brotherExchange, brotherExchange + ".oridinary." + new Random().nextInt(), msgRecord.get(i));
            }
            return increasedQueueNumber;
        } else {
            // 空闲队列数量大于阈值，不扩容
            return 0;
        }
    }

    public void createQueue(String queueName, String exchange, String bindingKey) {
        if (connection != null) {
            Channel channel = null;
            try {
                channel = connection.createChannel();
                channel.queueDeclare(queueName, false, false, false, null);
                channel.queueBind(queueName, exchange, bindingKey);
            } catch (Exception e) {
                LOGGER.error("createQueue - {}", e.getStackTrace());
            }
        } else {
            LOGGER.error("createQueue - connection is null");
        }
    }

    public void putMessage(String exchange, String routeKey, String msg) {
        if (rabbitTemplate != null) {
            rabbitTemplate.convertAndSend(exchange, routeKey, msg);
        } else {
            LOGGER.error("putMessage - {}", "rabbitTemplate is null");
        }

    }

    public String pullMessage(String queueName) {
        Object msg = null;
        if (rabbitTemplate != null) {
            msg = rabbitTemplate.receiveAndConvert(queueName);
        } else {
            LOGGER.error("pullMessage - {}", "rabbitTemplate is null");
        }
        return (msg == null) ? null : msg.toString();
    }

    public int computeQueueStart(int brotherExchangeIndex) {
        int start = 1;

        for (int i = 0, oldSize = QUEUE_DEFAULT_NUMBER, newSize = 0; i < brotherExchangeIndex; ++i) {
            newSize = (int) (oldSize * (1 + QUEUE_NUMBER_RESIZE_FACTORY));
            start = oldSize +1;
            oldSize = newSize;
        }

        return start;
    }

    public int computeQueueEnd(int brotherExchangeIndex) {
        int end = 5;

        for (int i = 0, oldSize = QUEUE_DEFAULT_NUMBER, newSize = 0; i < brotherExchangeIndex; ++i) {
            newSize = (int) (oldSize * (1 + QUEUE_NUMBER_RESIZE_FACTORY));
            end = newSize;
            oldSize = newSize;
        }

        return end;
    }

    public int computeBrotherExchangeQueueSize(int brotherExchangeIndex) {
        return  computeQueueEnd(brotherExchangeIndex) - computeQueueStart(brotherExchangeIndex) + 1;
    }

    public int computeNextBrotherExchangeIndex(int queueNumber) {
        int index = 0;
        int computeQueueNumber = QUEUE_DEFAULT_NUMBER;
        while (computeQueueNumber < queueNumber) {
            index++;
            computeQueueNumber = (int) (computeQueueNumber * ( 1 + QUEUE_NUMBER_RESIZE_FACTORY));
        }
        return index + 1;
    }

}
