package cn.bookcycle.mq.service;

import cn.bookcycle.mq.pojo.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * CustomMessageListener
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/26
 */
@Component
public class CustomMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMessageListener.class);

    @RabbitListener(queues = "queue11111111")
    public void handleForwardEngineeringRequest(Message msg) throws IOException {
//        Employee employee = new ObjectMapper().readValue(jsonString, Employee.class);
        byte [] bytes = msg.getBody();
        LOGGER.info("Receive Message <{}>", new String(bytes, "UTF-8"));
    }
}
