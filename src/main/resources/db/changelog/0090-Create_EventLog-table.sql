--liquibase formatted sql

--changeset mlarson:0090-1
CREATE TABLE EventLog
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  semesterId INT NOT NULL,
  eventData TEXT NOT NULL,
  createdDate TIMESTAMP NOT NULL,
  isShadowMode BOOL NOT NULL,
  eventLogTypeId INT NOT NULL
);

--changeset mlarson:0090-2
CREATE INDEX EventLogEventTypeFkIdx ON EventLog(eventLogTypeId);

--changeset mlarson:0090-3
CREATE INDEX EventLogSemesterFkIdx ON EventLog(semesterId);

--changeset mlarson:0090-4
ALTER TABLE EventLog ADD CONSTRAINT EventLogEventTypeFk FOREIGN KEY (eventLogTypeId) REFERENCES EventLogType(id);

--changeset mlarson:0090-5
ALTER TABLE EventLog ADD CONSTRAINT EventLogSemesterFk FOREIGN KEY (semesterId) REFERENCES Semester(id);

--changeset mlarson:0090-6
CREATE INDEX EventLogIsShadowModeIdx ON EventLog(isShadowMode);




