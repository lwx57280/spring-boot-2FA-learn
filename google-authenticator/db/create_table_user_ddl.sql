
CREATE TABLE `user`
(
    `id`             bigint      NOT NULL AUTO_INCREMENT,
    `username`       varchar(50) NOT NULL COMMENT '用户名',
    `password`       varchar(100) NOT NULL COMMENT 'BCrypt加密密码',
    `google_secret`  varchar(100) DEFAULT NULL COMMENT '谷歌密钥',
    `google_enable`  tinyint(1) DEFAULT '0' COMMENT '0关闭 1开启',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;