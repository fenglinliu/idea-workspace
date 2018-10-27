package cn.bookcycle.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQConfig
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/25
 */

@Configuration
public class RabbitMQConfig4Sender {
//    ConnectionFactory connectionFactory;

    @Value("${javainuse.rabbitmq.queue}")
    String queueName;
//
//    @Value("${javainuse.rabbitmq.exchange}")
//    String exchange;
//
//    @Value("${javainuse.rabbitmq.routingkey}")
//    private String routingkey;
//
//    @Bean
//    Queue queue() {
//        return new Queue(queueName, true);
//    }
//
//    @Bean
//    TopicExchange exchange() {
//        return new TopicExchange("sdf");
////        return new DirectExchange("haha");
//    }
//
//    @Bean
//    Binding binding(Queue queue, DirectExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(routingkey);
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//
//    @Bean
//    public AmqpTemplate rabbitTemplate() {
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate();
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
//        return rabbitTemplate;
//    }
}
