# User schema

# --- !Ups
create table career (
  id SERIAL PRIMARY KEY ,
  name TEXT NOT NULL,
  description TEXT NOT NULL
);

# --- !Downs
drop table `career`;