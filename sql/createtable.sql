create schema moviedb;
use moviedb;
create table moviedb.movies(
   id varchar(10) not null,
   title VARCHAR(100) not null,
   year int NOT NULL,
   director varchar(100) not null,
   primary key ( id )
);

create table moviedb.stars(
   id varchar(10) not null,
   name VARCHAR(100) not null,
   birthYear int,
   primary key ( id )
);

create table moviedb.stars_in_movies(
   starId varchar(10) not null,
   movieId varchar(10) not null,
   foreign key (starId) references moviedb.stars(id),
   foreign key (movieId) references moviedb.movies(id)
);

create table moviedb.genres(
   id int not null auto_increment,
   name VARCHAR(32) not null,
   primary key ( id )
);

create table moviedb.genres_in_movies(
   genreId int not null,
   movieId varchar(10) not null,
   foreign key (genreId) references moviedb.genres(id),
   foreign key (movieId) references moviedb.movies(id)
);

create table moviedb.creditcards(
   id varchar(20) not null,
   firstName varchar(50) not null,
   lastName varchar(50) not null,
   expiration date not null,
   primary key ( id )
);

create table moviedb.customers(
   id int not null auto_increment,
   firstName VARCHAR(50) not null,
   lastName varchar(50) not null,
   ccId varchar(20) not null,
   foreign key (ccId) references moviedb.creditcards(id),
   address varchar(200) not null,
   email varchar(50) not null,
   password varchar(20) not null,
   primary key ( id )
);

create table moviedb.sales(
   id int not null auto_increment,
   customerId int not null,
   foreign key (customerId) references moviedb.customers(id),
   movieId varchar(10) not null,
   foreign key (movieId) references moviedb.movies(id),
   saleDate date not null,
   primary key ( id )
);

create table moviedb.ratings(
   movieId varchar(10) not null,
   foreign key (movieId) references moviedb.movies(id),
   rating float not null,
   numVotes int not null
);