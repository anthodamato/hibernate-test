create table person (id integer primary key, name varchar(100), fingerprint_id int);

create sequence APP.HIBERNATE_SEQUENCE;

create table fingerprint (id integer primary key, type varchar(100));
