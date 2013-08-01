drop table if exists audit_log;
drop table if exists CAMEL_MESSAGEPROCESSED;

-- required by Camel JdbcMessageIdRepository
create table CAMEL_MESSAGEPROCESSED (
    processorName varchar(255),
    messageId varchar(100),
    createdAt timestamp
);

create table audit_log (
    auditId serial primary key,
	  message varchar(255) not null
);
