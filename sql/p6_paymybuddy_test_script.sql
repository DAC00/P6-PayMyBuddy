/* p6_paymybuddy_test database is for testing */

create database p6_paymybuddy_test;
use p6_paymybuddy_test;

create table users (
id integer primary key auto_increment,
username varchar(55) not null,
email varchar(55) not null unique,
password varchar(255) not null);

create table transactions (
id integer primary key auto_increment,
description varchar(255) not null,
amount double not null,
sender_id integer not null,
receiver_id integer not null,
foreign key (sender_id) references users(id) ON DELETE CASCADE,
foreign key (receiver_id) references users(id) ON DELETE CASCADE);

create table users_connections (
id_first_user integer not null,
id_second_user integer not null,
primary key(id_first_user, id_second_user),
foreign key (id_first_user) references users(id) ON DELETE CASCADE,
foreign key (id_second_user) references users(id) ON DELETE CASCADE);

/* Filling the testing database */

INSERT INTO users (username, email, password) VALUES 
('alice', 'alice@mail.com', '$2a$12$TqHcWP.JzkaYsM/ClaAl5uTuVn8BzzbrndmnN7CZTUFOYOP2kxwNC'),
('bob', 'bob@mail.com', '$2a$12$NHf6kzLo7RcT6fjzIalbcenNYMv/HmFVrHyqMwdaHPtBWGF3iNHxS'),
('carol', 'carol@mail.com', '$2a$12$z2V2ERP4EWase1O4YAthxebbnlm1Q3WxmKncfGC8loaP4vuYo4pXW'),
('dave', 'dave@mail.com', '$2a$12$k5Gl7riFI43TUfXdFqiJQuz0UlCXDaTIIPKogjLaN1eLnKPB7ur5.');

INSERT INTO transactions (description, amount, sender_id, receiver_id) VALUES
('Transaction 1', 100.0, 1, 2),
('Transaction 2', 50.0, 2, 3),
('Transaction 3', 200.0, 3, 1),
('Transaction 4', 75.0, 1, 4);

INSERT INTO users_connections (id_first_user, id_second_user) VALUES
(1, 2), (2, 1),  -- Alice / Bob
(2, 3), (3, 2),  -- Bob / Carol
(1, 3), (3, 1),  -- Alice / Carol
(1, 4), (4, 1);  -- Alice / Dave
