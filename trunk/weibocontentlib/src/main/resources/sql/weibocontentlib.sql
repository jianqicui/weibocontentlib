drop database weibocontentlib;
create database if not exists weibocontentlib default charset utf8 collate utf8_general_ci;

use weibocontentlib;

create table user_collecting (
	id int not null auto_increment,
  	cookies text not null,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table user_transfering (
	id int not null auto_increment,
  	cookies text not null,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table operator (
	id int not null auto_increment,
	name varchar(10) not null,
  	password varchar(100) not null,
  	role varchar(20) not null,
  	primary key (id)
);

insert into operator (name, password, role) values 
('admin', '638091df2786a6e7d287c637f4165d43c5ad7bb6', 'ROLE_OPERATOR');