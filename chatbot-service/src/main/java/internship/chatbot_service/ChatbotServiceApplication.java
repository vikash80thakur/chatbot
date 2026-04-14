package internship.chatbot_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatbotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatbotServiceApplication.class, args);
		System.out.println("Welcome to chatbot-service");
	}

}
