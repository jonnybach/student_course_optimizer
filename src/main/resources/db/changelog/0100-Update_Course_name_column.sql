--liquibase formatted sql

--changeset mlarson:0100-1
ALTER TABLE Course CHANGE name name VARCHAR(100) NOT NULL;


