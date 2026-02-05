-- V3_2__create_personal_access_tokens_table.sql

CREATE TABLE `personal_access_tokens` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `token` CHAR(36) NOT NULL,
  `user_id` INT(11) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `personal_access_tokens`
  ADD CONSTRAINT `fk_pat_user`
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
  ON DELETE CASCADE;
