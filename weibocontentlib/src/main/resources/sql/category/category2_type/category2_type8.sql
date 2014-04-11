use weibocontentlib;

create table category2_type8_user_collecting (
	id int not null auto_increment,
	user_id varchar(20) not null,
	page_size int not null,
	page_no int not null,
	primary key (id)
);

insert into category2_type8_user_collecting (user_id) values 
('1679701817'),
('2169328633'),
('1974152033'),
('2149964255');

create table category2_type8_user_applying (
	id int not null auto_increment,
  	cookies text not null,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table category2_type8_status_collected (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type8_status_filtered (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type8_status_verified (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type8_status_transfered (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);