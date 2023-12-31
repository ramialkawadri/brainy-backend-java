--The username indicates the
CREATE TABLE shared_files(
    file_owner      VARCHAR(63)     NOT NULL        REFERENCES users(username),
    shared_with     VARCHAR(63)     NOT NULL        REFERENCES users(username),
    filename        TEXT            NOT NULL,
    can_edit        boolean         DEFAULT false,
    PRIMARY KEY(file_owner, shared_with, filename)
);
