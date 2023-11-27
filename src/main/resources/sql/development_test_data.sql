--Test user: do not insert them into production, just for development
--Password: test
INSERT INTO users VALUES(
    'test', 
    '$2a$12$3W4FUDZUuj5739QKvTEPO.CqSn7RekFsMsJikp8bZAn4MZ5Gk6/F.', 
    'test@test.com',
    'test_first_name',
    'test_last_name'
);