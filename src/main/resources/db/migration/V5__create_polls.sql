CREATE TABLE polls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    status VARCHAR(30) NOT NULL,
    anonymous BOOLEAN NOT NULL DEFAULT TRUE,
    result_visible BOOLEAN NOT NULL DEFAULT FALSE,
    starts_at DATETIME(6) NOT NULL,
    ends_at DATETIME(6) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_polls_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_polls_status_ends_at
    ON polls(status, ends_at);

CREATE INDEX idx_polls_created_at
    ON polls(created_at);

CREATE TABLE poll_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poll_id BIGINT NOT NULL,
    text VARCHAR(200) NOT NULL,
    display_order INT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_poll_options_poll
        FOREIGN KEY (poll_id) REFERENCES polls(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_poll_options_poll_order
    ON poll_options(poll_id, display_order);

CREATE TABLE poll_votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poll_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    voter_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_poll_votes_poll
        FOREIGN KEY (poll_id) REFERENCES polls(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_poll_votes_option
        FOREIGN KEY (option_id) REFERENCES poll_options(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_poll_votes_voter
        FOREIGN KEY (voter_id) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT uq_poll_votes_poll_voter
        UNIQUE (poll_id, voter_id)
);

CREATE INDEX idx_poll_votes_poll_id
    ON poll_votes(poll_id);

CREATE INDEX idx_poll_votes_option_id
    ON poll_votes(option_id);

CREATE INDEX idx_poll_votes_voter_poll
    ON poll_votes(voter_id, poll_id);
