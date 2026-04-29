package internship.chatbot_service.repository;


import internship.chatbot_service.model.Conversation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ConversationRepository     {

    private final JdbcTemplate template;


    // 🔹 Create Conversation
    public Conversation save(Conversation conversation) {

        String sql = "INSERT INTO conversations (user_name, organization_id, created_at) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, conversation.getUser());
            ps.setLong(2, conversation.getOrganizationId());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        conversation.setId(keyHolder.getKey().longValue());
        return conversation;
    }

}
