CREATE TABLE notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_notices_author
        FOREIGN KEY (author_id) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_notices_pinned_created_at
    ON notices(pinned, created_at);

CREATE INDEX idx_notices_author_id
    ON notices(author_id);
