create table store (id integer primary key, name varchar(100));

create sequence APP.HIBERNATE_SEQUENCE;

create table item (id integer primary key, name varchar(100), model varchar(100));

create table store_items (store_id integer, items_id integer);
