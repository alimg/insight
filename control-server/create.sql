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
  FOREIGN KEY (id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `device` (
  `id` int NOT NULL,
  `userid` int NOT NULL,
  FOREIGN KEY (userid) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `events` (
  `id` bigint NOT NULL,
  `deviceid` int NOT NULL,
  `userid` int NOT NULL,
  `date` DATETIME,
  `type` varchar(32),
  `data` varchar(256)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

