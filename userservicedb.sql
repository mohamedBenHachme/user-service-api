

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";



CREATE TABLE  `confirmationotp` (
  `otp_id` bigint(20) NOT NULL,
  `confirmationotp` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=MYISAM   DEFAULT CHARSET=latin1;


INSERT INTO `confirmationotp` (`otp_id`, `confirmationotp`, `created_date`, `user_id`) VALUES
(61, '4840', '2019-09-10 14:50:39', 59);


CREATE TABLE  `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


INSERT INTO `hibernate_sequence` (`next_val`) VALUES
(63);


CREATE TABLE  `user` (
  `user_id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `id_card` blob DEFAULT NULL,
  `is_email_confirmed` tinyint(1) DEFAULT 0,
  `is_enabled` tinyint(1) DEFAULT 1,
  `isidcecked` tinyint(1) DEFAULT 0,
  `last_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `qr_code` blob DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `user_token` varchar(255) DEFAULT NULL,
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


INSERT INTO `user` (`user_id`, `email`, `id_card`, `is_email_confirmed`, `is_enabled`, `isidcecked`, `last_name`, `phone_number`, `qr_code`, `role`, `surname`, `user_token`) VALUES
(62, NULL, NULL, 0, 0, 0, NULL, '+212630546088', NULL, 'Customer', NULL, NULL),
(59, 'benhachmemohamed@gmail.com', NULL, 1, 1, 0, NULL, '212630546088', NULL, 'Customer', NULL, NULL);


ALTER TABLE `confirmationotp`
  ADD PRIMARY KEY (`otp_id`),
  ADD KEY `FKaj1hlme5l2frb21qq8spw7o3r` (`user_id`);


ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `UK_4bgmpi98dylab6qdvf9xyaxu4` (`phone_number`);
COMMIT;

