

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_activity`;
CREATE TABLE `t_activity`  (
  `user_id` bigint(20) NOT NULL,
  `act` smallint(6) NOT NULL COMMENT '添加了问题\r\n关注了问题\r\n关注了话题\r\n回答了问题\r\n赞同了回答',
  `id` bigint(20) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  UNIQUE INDEX `u_id`(`user_id`, `act`, `id`) USING BTREE,
  INDEX `uid`(`user_id`, `gmt_create`) USING BTREE,
  CONSTRAINT `t_activity_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_answer
-- ----------------------------
DROP TABLE IF EXISTS `t_answer`;
CREATE TABLE `t_answer`  (
  `answer_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` bigint(20) NOT NULL,
  `gmt_modify` bigint(20) NULL DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `question_id` bigint(20) NOT NULL,
  `ans` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `anonymous` tinyint(1) NOT NULL,
  PRIMARY KEY (`answer_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_answer_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `t_question` (`question_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_answer_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_approve
-- ----------------------------
DROP TABLE IF EXISTS `t_approve`;
CREATE TABLE `t_approve`  (
  `review_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  INDEX `review_id`(`review_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_approve_ibfk_1` FOREIGN KEY (`review_id`) REFERENCES `t_review` (`review_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_approve_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_attitude
-- ----------------------------
DROP TABLE IF EXISTS `t_attitude`;
CREATE TABLE `t_attitude`  (
  `answer_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `atti` tinyint(1) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  INDEX `answer_id`(`answer_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_attitude_ibfk_1` FOREIGN KEY (`answer_id`) REFERENCES `t_answer` (`answer_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_attitude_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_follow
-- ----------------------------
DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow`  (
  `user_id` bigint(20) NOT NULL,
  `follower_id` bigint(20) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `follower_id`(`follower_id`) USING BTREE,
  CONSTRAINT `t_follow_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_follow_ibfk_2` FOREIGN KEY (`follower_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `src_id` bigint(20) NULL DEFAULT NULL,
  `dst_id` bigint(20) NULL DEFAULT NULL,
  `gmt_create` bigint(20) NULL DEFAULT NULL,
  `msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unread` tinyint(1) NULL DEFAULT NULL,
  INDEX `src_id`(`src_id`) USING BTREE,
  INDEX `dst_id`(`dst_id`) USING BTREE,
  CONSTRAINT `t_message_ibfk_1` FOREIGN KEY (`src_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_message_ibfk_2` FOREIGN KEY (`dst_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_question
-- ----------------------------
DROP TABLE IF EXISTS `t_question`;
CREATE TABLE `t_question`  (
  `question_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` bigint(20) NOT NULL,
  `gmt_modify` bigint(20) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `user_id` bigint(20) NOT NULL,
  `anonymous` tinyint(1) NOT NULL,
  PRIMARY KEY (`question_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_question_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_question_topic
-- ----------------------------
DROP TABLE IF EXISTS `t_question_topic`;
CREATE TABLE `t_question_topic`  (
  `question_id` bigint(20) NOT NULL,
  `topic_id` bigint(20) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  INDEX `question_id`(`question_id`) USING BTREE,
  INDEX `topic_id`(`topic_id`) USING BTREE,
  CONSTRAINT `t_question_topic_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `t_question` (`question_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_question_topic_ibfk_2` FOREIGN KEY (`topic_id`) REFERENCES `t_topic` (`topic_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_review
-- ----------------------------
DROP TABLE IF EXISTS `t_review`;
CREATE TABLE `t_review`  (
  `review_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` bigint(20) NOT NULL,
  `answer_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `revi` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `reply_id` bigint(20) NULL DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`review_id`) USING BTREE,
  INDEX `answer_id`(`answer_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `reply_id`(`reply_id`) USING BTREE,
  CONSTRAINT `t_review_ibfk_1` FOREIGN KEY (`answer_id`) REFERENCES `t_answer` (`answer_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_review_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_review_ibfk_3` FOREIGN KEY (`reply_id`) REFERENCES `t_review` (`review_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_subscribe_question
-- ----------------------------
DROP TABLE IF EXISTS `t_subscribe_question`;
CREATE TABLE `t_subscribe_question`  (
  `question_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  INDEX `question_id`(`question_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_subscribe_question_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `t_question` (`question_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_subscribe_question_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_subscribe_topic
-- ----------------------------
DROP TABLE IF EXISTS `t_subscribe_topic`;
CREATE TABLE `t_subscribe_topic`  (
  `topic_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  INDEX `topic_id`(`topic_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_subscribe_topic_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `t_topic` (`topic_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_subscribe_topic_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_topic
-- ----------------------------
DROP TABLE IF EXISTS `t_topic`;
CREATE TABLE `t_topic`  (
  `topic_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `topic_intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`topic_id`) USING BTREE,
  UNIQUE INDEX `topic_name`(`topic_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gmt_create` bigint(20) NOT NULL,
  `username` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `authority` smallint(2) NOT NULL,
  `nickname` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sex` smallint(2) NULL DEFAULT NULL,
  `intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `avater` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100000 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


SET FOREIGN_KEY_CHECKS = 1;
