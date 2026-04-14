package internship.chatbot_service.Service;


import internship.chatbot_service.Dto.ChatMessage;
import internship.chatbot_service.Dto.ChatResponse;

public interface ChatService {
    ChatResponse getReply(ChatMessage request);
}
