CREATE TABLE IF NOT EXISTS `users`
(
    `id`                        bigint unsigned     NOT NULL AUTO_INCREMENT,
    `user_name`                 VARCHAR(255) UNIQUE NOT NULL,
    `password`                  VARCHAR(255)        NOT NULL,
    `role`                      ENUM('COMMON', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'COMMON',
    `account_non_expired`       bit(1) NOT NULL DEFAULT b'1',
    `account_non_locked`        bit(1) NOT NULL DEFAULT b'1',
    `credentials_non_expired`   bit(1) NOT NULL DEFAULT b'1',
    `enabled`                   bit(1) NOT NULL DEFAULT b'1',
    `system_id_user_insert`     bigint(20) unsigned NOT NULL,
    `system_date_insert`        datetime(3) NOT NULL,
    `system_id_user_update`     bigint(20) unsigned DEFAULT NULL,
    `system_date_update`        datetime(3) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;