package cn.bookcycle.messagequeue.util;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Queue
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/27
 */
@Component
public class Queue {

    private static final Logger LOGGER = LoggerFactory.getLogger(Queue.class);

    private static final String TOPIC_EXCHANGE_TYPE = "topic";

    @Autowired
    private ConnectionFactory connectionFactory;

    private Connection connection;

    public Queue() {
        connection = connectionFactory.createConnection();
    }

    public void logoutQueue() {
        if (connection != null) {
            connection.close();
        }
    }

    public void createExchange(String exchange) {
        if (connection != null) {
            Channel channel = connection.createChannel(true);
            // 创建一个type=direct 持久化的 非自动删除的交换器
            try {
                channel.exchangeDeclare(exchange, TOPIC_EXCHANGE_TYPE, true, false, null);
                channel.close();
            } catch (Exception e) {
                LOGGER.error("createExchange - {}", e.getStackTrace());
            }

        } else {
            LOGGER.error("createExchange - connection is null");
        }
    }

    public void createQueue(String queueName, String exchange, String bindingKey) {

    }

    public void putMessage(String msg, String routeKey) {

    }

    public String pullMessage(String queueName) {

        return null;
    }


}
