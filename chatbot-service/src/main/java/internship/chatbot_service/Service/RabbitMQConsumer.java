package internship.chatbot_service.Service;

import internship.chatbot_service.config.RabbitMQConfig;
import internship.chatbot_service.dto.QueueMessage;
import internship.chatbot_service.model.Message;
import internship.chatbot_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final OpenAIService openAIService;
    private final MessageRepository messageRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(QueueMessage message) {

        System.out.println("📥 Message received from queue: " + message);

        // Calling OpenAI
        String response = openAIService.askAI(message.getPrompt());

        System.out.println("🤖 AI Response: " + response);
        System.out.println("ConversationId from queue: " + message.getConversationId());

        // here I'm saving the response in database
        Message msg = new Message(
                null,
                message.getConversationId(),
                "assistant",
                response,
                LocalDateTime.now()
        );

        messageRepository.save(msg);
        System.out.println("💾 Saved AI response to DB");
    }
}
