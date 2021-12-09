--create table book (id int primary key, title varchar(100), author varchar(100));

create sequence APP.HIBERNATE_SEQUENCE;

create table booksample (id int generated always as identity primary key, title varchar(100), author varchar(100));
