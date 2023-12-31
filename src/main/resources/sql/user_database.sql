--Creating user table
CREATE TABLE users (
    username                        VARCHAR(63)         PRIMARY KEY,
    password                        VARCHAR(100)        NOT NULL,
    email                           VARCHAR(50)         NOT NULL UNIQUE,
    first_name                      VARCHAR(50)         NOT NULL,
    last_name                       VARCHAR(50)         NOT NULL,
    password_change_date            TIMESTAMP           NOT NULL DEFAULT NOW(),
    logout_date                     TIMESTAMP           NOT NULL DEFAULT NOW()
);
