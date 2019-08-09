CREATE TABLE `product` (
  `id` int(11) unsigned NOT NULL,
  `price` int(11) unsigned NOT NULL DEFAULT '0',
  `detail` varchar(1000) NOT NULL DEFAULT '',
  `count` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `orders` (
  `id` varchar(25) NOT NULL DEFAULT '',
  `uid` int(11) unsigned NOT NULL,
  `pid` int(11) unsigned NOT NULL,
  `detail` varchar(1000) NOT NULL DEFAULT '',
  `price` int(11) unsigned NOT NULL,
  `order_status` tinyint(1) unsigned NOT NULL,
  `token` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sessions` (
  `id` int(11) unsigned NOT NULL,
  `session_id` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;