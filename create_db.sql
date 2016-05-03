create database midburn DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
use midburn;

CREATE TABLE tickets
(
  ticket_id INT PRIMARY KEY NOT NULL,
  order_number INT NOT NULL,
  mail VARCHAR(128),
  name VARCHAR(128) COLLATE utf8_general_ci NOT NULL,
  barcode VARCHAR(40) NOT NULL,
  ticket_type VARCHAR(128) NOT NULL,
  buyer_mail VARCHAR(128) NOT NULL,
  document_id VARCHAR(50) NULL,
  cancelled TINYINT DEFAULT 0 NOT NULL,
  entrance_date DATETIME,
  shift_id INT,
  early_arrival TINYINT DEFAULT 0,
  disabled_parking TINYINT DEFAULT 0
) CHARACTER SET utf8 COLLATE utf8_general_ci;

insert into tickets(ticket_id, order_number, barcode, Name, ticket_type, early_arrival)
  values (-100, -999999, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '*FAKE TICKET*', 'Adult Ticket', 1);
insert into tickets(ticket_id, order_number, barcode, Name, ticket_type, Entrance_Date, early_arrival, disabled_parking)
  values (-200, -999999, 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', '*FAKE TICKET*', 'Adult Ticket', '1999-01-01 12:34:56', 1, 1);
insert into tickets(ticket_id, order_number, barcode, Name, ticket_type, disabled_parking)
  values (-300, -999999, 'cccccccccccccccccccccccccccccccc', '*FAKE TICKET*', 'Adult Ticket', 1);

CREATE TABLE shifts
(
  shift_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  start_date DATETIME,
  end_date DATETIME,
  ip VARCHAR(16),
  gater VARCHAR(32)
);

CREATE TABLE tickets_log
(
  change_time DATETIME NOT NULL,
  ticket_id INT NOT NULL,
  order_number INT NOT NULL,
  mail VARCHAR(128),
  name VARCHAR(128) COLLATE utf8_general_ci NOT NULL,
  barcode VARCHAR(40) NOT NULL,
  ticket_type VARCHAR(128) NOT NULL,
  buyer_mail VARCHAR(128) NOT NULL,
  document_id VARCHAR(50) NULL,
  cancelled TINYINT DEFAULT 0 NOT NULL,
  entrance_date DATETIME,
  shift_id INT,
  early_arrival TINYINT DEFAULT 0,
  disabled_parking TINYINT DEFAULT 0
) CHARACTER SET utf8 COLLATE utf8_general_ci;

--CREATE USER midburn IDENTIFIED BY 'midburn';
--grant usage on *.* to midburn@localhost identified by 'midburn';
--grant all privileges on midburn.* to midburn@localhost;
