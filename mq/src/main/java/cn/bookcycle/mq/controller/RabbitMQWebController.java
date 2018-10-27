package cn.bookcycle.mq.controller;

import cn.bookcycle.mq.pojo.Employee;
import cn.bookcycle.mq.service.RabbitMQReceiver;
import cn.bookcycle.mq.service.RabbitMQSender;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RabbitMQWebController
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/25
 */
@RestController
@RequestMapping(value = "/javainuse-rabbitmq/")
public class RabbitMQWebController {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    RabbitMQSender rabbitMQSender;

    @Autowired
    RabbitMQReceiver rabbitMQReceiver;

    @GetMapping(value = "/producer")
    public String producer(@RequestParam("empName") String empName, @RequestParam("empId") String empId) {

        Employee emp=new Employee();
        emp.setEmpId(empId);
        emp.setEmpName(empName);
        rabbitMQSender.send(emp);

        return "Message sent to the RabbitMQ JavaInUse Successfully";
    }

    @GetMapping(value = "/consumer")
    public String consumer() {
        return rabbitMQReceiver.receiveMsg();
    }

    @GetMapping(value = "/createqueue")
    public String createQueue() {
        Connection connection = connectionFactory.createConnection();
        Channel channel = connection.createChannel(true);
        try {
            channel.queueDeclare("queue11111111", false, false, false, null);
            channel.queueBind("queue11111111", "test.topic", "cc.#");
        } catch (Exception e) {

        }


//        String routingKey = getRouting(argv);
//        String message = getMessage(argv);

//        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
//        System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        connection.close();
//        AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory);
//        Queue queue = new Queue("sdfsdf", true);
//        amqpAdmin.declareQueue(queue);
//        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(new TopicExchange("test.topic")).with("cc.#"));
        return "success";
    }

}
