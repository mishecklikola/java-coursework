-- users
CREATE TABLE IF NOT EXISTS users (
                                     id          BIGSERIAL PRIMARY KEY,
                                     name        VARCHAR(255)  NOT NULL,
    email       VARCHAR(320)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL
    );
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email ON users (LOWER(email));

-- tasks
CREATE TABLE IF NOT EXISTS tasks (
                                     id           BIGSERIAL PRIMARY KEY,
                                     user_id      BIGINT        NOT NULL,
                                     title        VARCHAR(255)  NOT NULL,
    description  TEXT,
    created_at   TIMESTAMPTZ   NOT NULL,
    target_date  TIMESTAMPTZ   NOT NULL,
    status       VARCHAR(20)   NOT NULL,
    deleted      BOOLEAN       NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_tasks_user ON tasks (user_id, deleted, status);

-- notifications
CREATE TABLE IF NOT EXISTS notifications (
                                             id          BIGSERIAL PRIMARY KEY,
                                             user_id     BIGINT        NOT NULL,
                                             message     VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL,
    status      VARCHAR(20)   NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_notif_user_status ON notifications (user_id, status);
