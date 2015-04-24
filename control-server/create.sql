DROP DATABASE insight;
CREATE DATABASE insight;
USE insight;


CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `email` text NOT NULL,
  `password` text NOT NULL,
  `push_token` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `userinfo` (
  `id` int NOT NULL,
  `firstname` varchar(32) NOT NULL,
  `lastname` text NOT NULL,
  `location_lt` int,
  `location_ln` int,
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `devices` (
  `id` int NOT NULL,
  `userid` int,
  `name` varchar(128),
  `last_response` DATETIME,
  `address` varchar(32),
  PRIMARY KEY (id),
  FOREIGN KEY (userid) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deviceid` int NOT NULL,
  `userid` int,
  `date` DATETIME,
  `type` varchar(32),
  `filename` varchar(128) NOT NULL,
  `data` varchar(256),
  `encryption` varchar(32),
  `priority` int,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `devices` (`id`, `userid`, `last_response`, `address`) VALUES ('9000', NULL, NULL, NULL);
