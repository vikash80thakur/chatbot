CREATE TABLE IF NOT EXISTS conversations
(
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255),
    organization_id BIGINT,
    created_at TIMESTAMP
);


CREATE TABLE IF NOT EXISTS messages
(
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT,
    role VARCHAR(50),
    content TEXT,
    timestamp TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
);