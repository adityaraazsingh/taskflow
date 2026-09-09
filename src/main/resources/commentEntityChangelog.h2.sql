-- liquibase formatted sql

-- changeset adity:20260909-1 splitStatements:false
ALTER TABLE ${schemaName}.COMMENT_ENTITY ADD COLUMN REPLY_COMMENT_ID BIGINT;

-- changeset adity:20260909-2 splitStatements:false
ALTER TABLE ${schemaName}.COMMENT_ENTITY ADD COLUMN REPLY_COMMENT_CONTENT VARCHAR(255);

-- changeset adity:20260909-3 splitStatements:false
ALTER TABLE ${schemaName}.COMMENT_ENTITY
  ADD CONSTRAINT FK_COMMENT_REPLY
  FOREIGN KEY (REPLY_COMMENT_ID)
  REFERENCES ${schemaName}.COMMENT_ENTITY(ID);
