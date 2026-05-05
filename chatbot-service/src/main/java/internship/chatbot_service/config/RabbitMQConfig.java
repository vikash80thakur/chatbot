    package internship.chatbot_service.config;


    import jakarta.annotation.PostConstruct;
    import org.springframework.amqp.core.*;
    import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
    import org.springframework.amqp.rabbit.connection.ConnectionFactory;
    import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
    import org.springframework.amqp.support.converter.MessageConverter;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class RabbitMQConfig {

        // 🔹 Queue name
        public static final String QUEUE = "chat.queue";

        // 🔹 Exchange name
        public static final String EXCHANGE = "chat.exchange";

        // 🔹 Routing key
        public static final String ROUTING_KEY = "chat.routingKey";

        // ✅ Create Queue
        @Bean
        public Queue queue() {
            System.out.println("🔥 Creating Queue: " + QUEUE);
            return new Queue(QUEUE, true);
        }

        // ✅ Create Exchange
        @Bean
        public DirectExchange exchange() {
            System.out.println("🔥 Creating Exchange: " + EXCHANGE);
            return new DirectExchange(EXCHANGE);
        }

        // ✅ Bind Queue + Exchange
        @Bean
        public Binding binding(Queue queue, DirectExchange exchange) {
            System.out.println("🔥 Binding Queue to Exchange");
            return BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with(ROUTING_KEY);
        }

//        @Bean
//        public JacksonJsonMessageConverter converter() {
//            return new JacksonJsonMessageConverter();
//        }

        @Bean
        public MessageConverter jsonMessageConverter() {
            return new JacksonJsonMessageConverter();
        }


        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
                ConnectionFactory connectionFactory,
                MessageConverter messageConverter) {

            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setMessageConverter(messageConverter);

            return factory;
        }
    }