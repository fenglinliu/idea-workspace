package cn.bookcycle.mq.service;

import cn.bookcycle.mq.pojo.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RabbitMQSender
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/25
 */
@Service
public class RabbitMQSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQSender.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${javainuse.rabbitmq.exchange}")
    private String exchange;

    @Value("${javainuse.rabbitmq.routingkey}")
    private String routingkey;

    public void send(Employee company) {
        LOGGER.info("exchange:{}, routingkey:{}, company:{}", exchange, routingkey, company);
        rabbitTemplate.convertAndSend("test.topic", "cc.123","sd324534645674575467");
        System.out.println("Send msg = " + company);

    }

}
