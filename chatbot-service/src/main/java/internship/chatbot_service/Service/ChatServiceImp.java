package internship.chatbot_service.Service;


import internship.chatbot_service.Dto.ChatMessage;
import internship.chatbot_service.Dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImp implements ChatService{

    @Override
    public ChatResponse getReply(ChatMessage request){
        String requestMessage = request.getMessage();
        String reply = "You said: " + requestMessage + " 🤖";
        return new ChatResponse(reply);
    }
}
