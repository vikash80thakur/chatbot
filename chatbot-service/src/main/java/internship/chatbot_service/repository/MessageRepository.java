package internship.chatbot_service.repository;

import internship.chatbot_service.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final JdbcTemplate template;

    // 🔹 Save message
    public void save(Message message) {

        String sql = "INSERT INTO messages (conversation_id, role, content, timestamp) VALUES (?, ?, ?, ?)";

        template.update(sql,
                message.getConversationId(),
                message.getRole(),
                message.getContent(),
                Timestamp.valueOf(LocalDateTime.now())
        );
    }

    // 🔹 Get messages by conversationId
    public List<Message> findByConversationId(Long conversationId) {

        String sql = "SELECT * FROM messages WHERE conversation_id=?";

        return template.query(sql, (rs, rowNum) -> new Message(
                rs.getLong("id"),
                rs.getLong("conversation_id"),
                rs.getString("role"),
                rs.getString("content"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        ), conversationId);
    }
}