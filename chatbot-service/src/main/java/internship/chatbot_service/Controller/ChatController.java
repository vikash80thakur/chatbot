package internship.chatbot_service.Controller;

import internship.chatbot_service.Dto.ChatMessage;
import internship.chatbot_service.Dto.ChatResponse;
import internship.chatbot_service.Service.ChatServiceImp;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private ChatServiceImp chatService;

    public ChatController(ChatServiceImp chatService) {
        this.chatService = chatService;
    }


    @GetMapping
    public String getMessage(){
        return "Getting message from chatBot";
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatMessage request){
        return chatService.getReply(request);
    }

}
