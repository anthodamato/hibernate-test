create table city (id integer primary key, name varchar(100), population int, region_id int);

create sequence APP.HIBERNATE_SEQUENCE;

create table region (id integer primary key, name varchar(100), population int);
