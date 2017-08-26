--liquibase formatted sql

--changeset mlarson:0120-1
ALTER TABLE EventLog ADD resultCalculated BOOLEAN NOT NULL DEFAULT FALSE;
