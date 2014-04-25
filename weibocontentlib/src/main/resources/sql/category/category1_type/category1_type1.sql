use weibocontentlib;

create table category1_type1_user_collecting (
	id int not null auto_increment,
	user_id varchar(20) not null,
	page_size int not null,
	page_no int not null,
	primary key (id)
);

insert into category1_type1_user_collecting (user_id) values 
('2133041565'),
('2732055990'),
('2011665122');

create table category1_type1_user_applying (
	id int not null auto_increment,
  	cookies text not null,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table category1_type1_user_transfering (
	id int not null auto_increment,
  	cookies text not null,
  	transfering_index int not null default 0,
  	created_timestamp timestamp not null,
  	primary key (id)
);

create table category1_type1_status_collected (
	id int not null auto_increment,
	status_text varchar(288) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category1_type1_status_filtered (
	id int not null auto_increment,
	status_text varchar(288) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);

create table category1_type1_status_verified (
	id int not null auto_increment,
	status_text varchar(288) not null,
	status_picture_file varchar(200),
	created_timestamp timestamp not null default current_timestamp,
	primary key (id)
);
