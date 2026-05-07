package internship.chatbot_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Main Queue
    public static final String QUEUE = "chat.queue";

    // Main Exchange
    public static final String EXCHANGE = "chat.exchange";

    // Main Routing Key
    public static final String ROUTING_KEY = "chat.routingKey";


    // DLQ Queue
    public static final String DLQ = "chat.dlq";

    // Dead Letter Exchange
    public static final String DLX_EXCHANGE = "chat.dlx";

    // DLQ Routing Key
    public static final String DLQ_ROUTING_KEY = "chat.dlq.routingKey";



    @Bean
    public Queue queue() {

        System.out.println("Creating Main Queue: " + QUEUE);

        return QueueBuilder
                .durable(QUEUE)

                // if message fails → send to DLX
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)

                // routing key for DLQ
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)

                .build();
    }


    @Bean
    public DirectExchange exchange() {

        System.out.println("Creating Exchange: " + EXCHANGE);

        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue,
                           DirectExchange exchange) {

        System.out.println("Binding Main Queue to Exchange");

        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }




    @Bean
    public Queue deadLetterQueue() {

        System.out.println("Creating DLQ: " + DLQ);

        return QueueBuilder
                .durable(DLQ)
                .build();
    }


    @Bean
    public DirectExchange deadLetterExchange() {

        System.out.println("Creating DLX: " + DLX_EXCHANGE);

        return new DirectExchange(DLX_EXCHANGE);
    }



    @Bean
    public Binding deadLetterBinding() {

        System.out.println("Binding DLQ to DLX");

        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(messageConverter);

        // failed messages should NOT requeue infinitely
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
