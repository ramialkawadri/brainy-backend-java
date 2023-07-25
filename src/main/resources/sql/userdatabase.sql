--Creating table
CREATE TABLE users (
    username            VARCHAR(50)         PRIMARY KEY,
    password            VARCHAR(100)        NOT NULL,
    email               VARCHAR(50)         NOT NULL UNIQUE,
    first_name          VARCHAR(50)         NOT NULL,
    last_name           VARCHAR(50)         NOT NULL
);

--Test user: do not insert them into production, just for development
--Password: test
INSERT INTO users VALUES(
    'test', 
    '$2a$12$3W4FUDZUuj5739QKvTEPO.CqSn7RekFsMsJikp8bZAn4MZ5Gk6/F.', 
    'test@test.com',
    'test_first_name',
    'test_last_name'
);