create table book (id int generated always as identity primary key, title varchar(100), author varchar(100), format varchar(100) not null, pages int not null);

CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout',  '1');
