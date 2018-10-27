package cn.bookcycle.mq.config;

import cn.bookcycle.mq.pojo.Employee;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * RabbitMQConfig4Receiver
 *
 * @author Flynn
 * @version 1.0
 * @description TODO
 * @email liufenglin@163.com
 * @date 2018/10/26
 */
@Configuration
public class RabbitMQConfig4Receiver {

//    @Value("${javainuse.rabbitmq.queue}")
//    private static  String SIMPLE_MESSAGE_QUEUE ;
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
//        connectionFactory.setUsername("guest");
//        connectionFactory.setPassword("guest");
//        return connectionFactory;
//    }
//
////    @Bean
//    public Queue simpleQueue() {
//        return new Queue(SIMPLE_MESSAGE_QUEUE);
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        template.setRoutingKey(SIMPLE_MESSAGE_QUEUE);
//        template.setMessageConverter(jsonMessageConverter());
//        return template;
//    }
//
////    @Bean
//    public SimpleMessageListenerContainer listenerContainer() {
//        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
//        listenerContainer.setConnectionFactory(connectionFactory());
//        listenerContainer.setQueues(simpleQueue());
//        listenerContainer.setMessageConverter(jsonMessageConverter());
//        listenerContainer.setMessageListener(new Employee());
//        listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
//        return listenerContainer;
//    }

}
