ALTER TABLE lecturers
    ADD COLUMN user_id BIGINT NULL;

ALTER TABLE lecturers
    ADD CONSTRAINT uq_lecturers_user_id UNIQUE (user_id);

ALTER TABLE lecturers
    ADD CONSTRAINT fk_lecturers_user
        FOREIGN KEY (user_id) REFERENCES users(id);
