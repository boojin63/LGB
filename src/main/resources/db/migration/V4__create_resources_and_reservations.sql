CREATE TABLE resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description TEXT NULL,
    location VARCHAR(200) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_resources_active_type
    ON resources(active, type);

CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    start_at DATETIME(6) NOT NULL,
    end_at DATETIME(6) NOT NULL,
    status VARCHAR(30) NOT NULL,
    purpose VARCHAR(500) NOT NULL,
    reject_reason VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    decided_at DATETIME(6) NULL,
    decided_by BIGINT NULL,
    CONSTRAINT fk_reservations_resource
        FOREIGN KEY (resource_id) REFERENCES resources(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_requester
        FOREIGN KEY (requester_id) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_reservations_decided_by
        FOREIGN KEY (decided_by) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_reservations_resource_status_time
    ON reservations(resource_id, status, start_at, end_at);

CREATE INDEX idx_reservations_requester_created_at
    ON reservations(requester_id, created_at);

CREATE INDEX idx_reservations_status_created_at
    ON reservations(status, created_at);
