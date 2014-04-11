use weibocontentlib;

create table category2_type6_user_collecting (
	id int not null auto_increment,
	user_id varchar(20) not null,
	page_size int not null,
	page_no int not null,
	primary key (id)
);

insert into category2_type6_user_collecting (user_id) values 
('1757507145'),
('2006490207'),
('1646285904'),
('1995147852'),
('1888451480'),
('1697491943');

create table category2_type6_user_applying (
	id int not null auto_increment,
  	cookies text not null,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table category2_type6_status_collected (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type6_status_filtered (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type6_status_verified (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type6_status_transfered (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);