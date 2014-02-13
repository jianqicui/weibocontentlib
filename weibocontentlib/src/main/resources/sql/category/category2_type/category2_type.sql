use weibocontentlib;

create table category2_type (
	id int not null auto_increment,
	type_id int not null,
	type_name varchar(10) not null,
	primary key (id)
);

insert into category2_type (type_id, type_name) values 
(1, '语录'),
(2, '生活'),
(3, '搞笑'),
(4, '情感'),
(5, '健康'),
(6, '星座'),
(7, '女人'),
(8, '旅游'),
(9, '职场'),
(10, '汽车');