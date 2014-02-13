use weibocontentlib;

create table category2_type7_user_collected (
	id int not null auto_increment,
	user_id varchar(20) not null,
	page_size int not null,
	page_no int not null,
	primary key (id)
);

insert into category2_type7_user_collected (user_id) values 
('2564786364'),
('2031912077'),
('1770281962'),
('1653235142'),
('2300206015'),
('2203959564'),
('2219082024'),
('1791292051'),
('2846792965');

create table category2_type7_user_applying (
	id int not null auto_increment,
  	cookies text not null,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table category2_type7_status_collected (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type7_status_filtered (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type7_status_verified (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category2_type7_status_transfered (
	id int not null auto_increment,
	status_text varchar(300) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);