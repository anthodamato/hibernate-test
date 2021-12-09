create table customer (id bigint not null, name varchar(255), primary key (id));

create table orders (id bigint not null, date_of timestamp, deliveryType integer, status varchar(255), customer_id bigint, primary key (id), foreign key (customer_id) references customer);

create table product (id bigint not null, name varchar(255), primary key (id));

create table orders_product (orders_id bigint not null, products_id bigint not null, foreign key (products_id) references product, foreign key (orders_id) references orders);

create sequence APP.HIBERNATE_SEQUENCE;
