# User schema

# --- !Ups
create table career (
  id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NOT NULL
);

# --- !Downs
drop table `career`;