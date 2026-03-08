-- MySQL dump 10.13  Distrib 8.4.6, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: chat
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `content_like`
--

DROP TABLE IF EXISTS `content_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `content_like` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content_id` varchar(50) NOT NULL COMMENT '内容id',
  `user_id` varchar(50) NOT NULL COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `content_id` (`content_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `content_like`
--

LOCK TABLES `content_like` WRITE;
/*!40000 ALTER TABLE `content_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `content_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emoji`
--

DROP TABLE IF EXISTS `emoji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emoji` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '表情id',
  `url` varchar(500) NOT NULL COMMENT '表情图片地址',
  `content` varchar(200) NOT NULL COMMENT '表情内容',
  `category_id` bigint NOT NULL COMMENT '表情分类id',
  `sort` int DEFAULT '0' COMMENT '排序值,越大越靠前',
  `creator_id` varchar(50) NOT NULL COMMENT '创建者id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(1) DEFAULT '2' COMMENT ' 0 已删除 1上架 2未上架',
  `width` int DEFAULT '12' COMMENT '图片宽度',
  `height` int DEFAULT '12' COMMENT '图片高度',
  PRIMARY KEY (`id`),
  KEY `emoji_content_status_index` (`content`,`status`),
  KEY `emoji_category_id_status_index` (`category_id`,`status`),
  KEY `emoji_create_time_index` (`create_time`),
  KEY `emoji_sort_status_index` (`sort`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='表情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emoji`
--

LOCK TABLES `emoji` WRITE;
/*!40000 ALTER TABLE `emoji` DISABLE KEYS */;
INSERT INTO `emoji` VALUES (3,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/b125255f-8a26-49d1-9783-1688b1507c77.jpg','1',1,1,'2021443320361967616','2026-02-22 21:31:07','2026-02-24 11:08:06',0,126,126),(4,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/ecbca5d4-44e4-425d-97b2-c29057607d94.jpg','豆包',1,2,'2021443320361967616','2026-02-22 21:44:23','2026-02-24 11:08:06',1,126,126),(5,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/9ee5df69-ce51-4871-a401-9de1086a9f84.png','萌死了',2,1,'2021443320361967616','2026-02-23 21:39:04','2026-02-24 11:08:06',1,126,126),(6,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/9bb044be-a7ae-49aa-ad8e-887a52eccf25.png','weiwei',2,2,'2024317657469415424','2026-02-23 21:47:40','2026-02-24 11:08:06',1,126,126),(7,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/4a82907e-c760-49d1-8962-1cfad4a64820.gif','gif',1,2,'2024317657469415424','2026-02-24 11:00:15','2026-02-24 11:05:07',1,256,256),(8,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/5a3ffa89-083a-4437-8e39-dfc21eca2d47.gif','[憨憨]',3,3,'2024317657469415424','2026-02-24 11:35:00','2026-02-24 11:55:04',1,200,200),(9,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/06c1df04-2d5f-4231-9077-6a329db46923.jpg','[?]',3,3,'2024317657469415424','2026-02-24 11:36:29','2026-02-24 11:54:44',1,200,200),(10,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/d505b7c8-3fee-4823-ab6d-845288211b1f.gif','[笑]',3,3,'2024317657469415424','2026-02-24 11:45:08','2026-02-24 11:55:03',1,400,400),(11,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/a0d4c80b-d5d7-485d-8a9b-aac2aaf902b2.gif','上车喽',3,2,'2024317657469415424','2026-02-24 15:02:50','2026-02-24 15:04:39',1,282,282),(12,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/e9fd94a1-b838-473f-b170-b46ba63732ed.gif','黑如子',3,3,'2024317657469415424','2026-02-24 15:03:14','2026-02-24 15:04:38',1,240,235),(13,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/4c2969fd-1454-4117-be78-3e5d4b203ebd.gif','拳头',3,4,'2024317657469415424','2026-02-24 15:03:41','2026-02-24 15:04:37',1,282,282),(14,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/e2a89d4e-a429-421c-93e0-f8d0b998eab4.gif','Love you',3,4,'2024317657469415424','2026-02-24 15:03:58','2026-02-24 15:04:36',1,248,214),(15,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/c59438d2-8550-48c9-86f7-6dd8113d07fc.gif','click',3,5,'2024317657469415424','2026-02-24 15:04:13','2026-02-24 15:04:35',1,282,252),(16,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/d8debc80-fbad-4556-9db7-b9f625e989cb.gif','[hh]',3,1,'2024317657469415424','2026-02-24 15:31:31','2026-02-24 15:32:35',1,282,282),(17,'https://summit-chat.oss-cn-beijing.aliyuncs.com/emoji/26c00a45-8739-4645-8b65-313a6944c0a4.png','真爱粉必备',1,3,'2024317657469415424','2026-02-26 17:11:24','2026-02-26 17:11:28',1,1000,986);
/*!40000 ALTER TABLE `emoji` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emoji_category`
--

DROP TABLE IF EXISTS `emoji_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emoji_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '表情分类id',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '分类排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(1) DEFAULT '0' COMMENT ' 0 禁用 1启用',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `emoji_category_name_status_index` (`name`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='表情分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emoji_category`
--

LOCK TABLES `emoji_category` WRITE;
/*!40000 ALTER TABLE `emoji_category` DISABLE KEYS */;
INSERT INTO `emoji_category` VALUES (1,'斗图',0,'2026-02-22 11:23:26',1,'2026-02-24 10:50:44'),(2,'萌宠',0,'2026-02-22 23:37:43',1,'2026-02-23 22:33:47'),(3,'GIF',2,'2026-02-24 11:34:25',1,'2026-02-24 11:34:24');
/*!40000 ALTER TABLE `emoji_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_apply`
--

DROP TABLE IF EXISTS `friend_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend_apply` (
  `id` bigint NOT NULL COMMENT 'id',
  `applicant_id` bigint NOT NULL COMMENT '申请人id',
  `recipient_id` bigint NOT NULL COMMENT '接受人id',
  `apply_reason` varchar(50) DEFAULT NULL COMMENT '申请理由',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 待接收 1已接收 2 拒绝',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `handle_time` datetime DEFAULT NULL COMMENT '接受时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_apply`
--

LOCK TABLES `friend_apply` WRITE;
/*!40000 ALTER TABLE `friend_apply` DISABLE KEYS */;
INSERT INTO `friend_apply` VALUES (2025124771657920512,2021443320361967616,2012441049674457089,'1',1,'2026-02-21 16:25:45','2026-02-21 16:25:49'),(2025127942199349248,2021443320361967616,2012441049674457089,'2',0,'2026-02-21 16:38:21',NULL),(2025128077859913728,2021443320361967616,2012441049674457089,'2',1,'2026-02-21 16:38:53','2026-02-21 16:39:14'),(2025132912889987072,2021443320361967616,2012441049674457089,'',1,'2026-02-21 16:58:06','2026-02-21 16:59:55'),(2025188804683620352,2021443320361967616,2012441049674457089,'',2,'2026-02-21 20:40:11','2026-02-21 20:40:19'),(2025195972077957120,2012441049674457089,2021443320361967616,'你好帅',1,'2026-02-21 21:08:40','2026-02-21 21:08:46'),(2025952194725027840,2024317657469415424,2021443320361967616,'',1,'2026-02-23 23:13:38','2026-02-23 23:13:49'),(2025954930296324096,2024317657469415424,2021443320361967616,'',1,'2026-02-23 23:24:30','2026-02-23 23:24:48'),(2025966316489412608,2024317657469415424,2021443320361967616,'',1,'2026-02-24 00:09:45','2026-02-24 00:09:50'),(2025970204785496064,2024317657469415424,2021443320361967616,'',1,'2026-02-24 00:25:12','2026-02-24 00:25:20'),(2027371219724255232,2027340725276000256,2021443320361967616,'',1,'2026-02-27 21:12:20','2026-02-27 21:12:40');
/*!40000 ALTER TABLE `friend_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_applications`
--

DROP TABLE IF EXISTS `group_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_applications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` bigint DEFAULT NULL COMMENT '群聊id',
  `applicant_id` bigint DEFAULT NULL COMMENT '申请人id',
  `application_reason` text COMMENT '申请理由',
  `status` enum('pending','approved','rejected') DEFAULT 'pending' COMMENT '申请状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `processed_by` bigint DEFAULT NULL COMMENT '处理人id',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '处理时间',
  `rejection_reason` text COMMENT '拒绝原因',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_applicant_id` (`applicant_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_group_applicant_status` (`group_id`,`applicant_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_applications`
--

LOCK TABLES `group_applications` WRITE;
/*!40000 ALTER TABLE `group_applications` DISABLE KEYS */;
INSERT INTO `group_applications` VALUES (6,11,2012441049674457089,'','approved','2026-02-11 11:44:34','2026-02-11 11:45:07',2021443320361967616,'2026-02-11 11:45:07',NULL),(7,11,2012441049674458089,'','approved','2026-02-14 06:57:05','2026-02-14 06:57:18',2021443320361967616,'2026-02-14 06:57:18',NULL),(9,10,2012441049674457089,'','rejected','2026-02-17 14:09:35','2026-02-21 08:02:49',2012441049674457088,'2026-02-21 08:02:49',NULL),(10,10,2021443320361967616,'我是你爹\n','rejected','2026-02-21 07:59:58','2026-02-21 08:02:50',2012441049674457088,'2026-02-21 08:02:50',NULL),(11,11,2024317657469415424,'','approved','2026-02-24 06:42:03','2026-02-24 06:42:11',2021443320361967616,'2026-02-24 06:42:11',NULL),(12,11,2024317657469415424,'','approved','2026-02-24 10:31:06','2026-02-24 10:31:14',2021443320361967616,'2026-02-24 10:31:14',NULL);
/*!40000 ALTER TABLE `group_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_chat`
--

DROP TABLE IF EXISTS `group_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) NOT NULL COMMENT '群聊名称',
  `group_description` text COMMENT '群聊描述',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint DEFAULT '1' COMMENT '群聊状态：1-活跃，0-禁用',
  `number` int NOT NULL DEFAULT '1' COMMENT '群人数',
  `icon` varchar(255) DEFAULT 'https://summit-oss.oss-cn-beijing.aliyuncs.com/062b2654-aa4d-4546-b94b-d6e956a10c37.jpg' COMMENT '群头像',
  PRIMARY KEY (`id`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_created_at` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_chat`
--

LOCK TABLES `group_chat` WRITE;
/*!40000 ALTER TABLE `group_chat` DISABLE KEYS */;
INSERT INTO `group_chat` VALUES (10,'大家荣耀','',2012441049674457088,'2026-02-10 12:13:40','2026-02-23 03:06:43',1,1,'https://summit-chat.oss-cn-beijing.aliyuncs.com/975c0431-4ffc-4f3b-a508-e40703f8967f.jpg'),(11,'test','我是你的,你干嘛哎呦',2021443320361967616,'2026-02-11 06:18:31','2026-02-24 10:31:14',1,6,'https://summit-chat.oss-cn-beijing.aliyuncs.com/08de6032-a9e9-4841-bcc7-a62b8ec5094b.jpg'),(12,'TEM','',2024317657469415424,'2026-02-21 05:13:30','2026-02-23 03:06:44',1,1,'https://summit-chat.oss-cn-beijing.aliyuncs.com/0c0abbdc-5a01-4b9d-b66d-7e1c8f071b27.ico');
/*!40000 ALTER TABLE `group_chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_members` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL COMMENT '群聊ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `join_time` timestamp NULL DEFAULT NULL COMMENT '加入时间',
  `role` enum('admin','member','owner') DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1-正常 2-禁言 ',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_group_user` (`group_id`,`user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_group_role` (`group_id`,`role`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_members`
--

LOCK TABLES `group_members` WRITE;
/*!40000 ALTER TABLE `group_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_messages`
--

DROP TABLE IF EXISTS `group_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_messages` (
  `id` varchar(64) NOT NULL,
  `group_id` bigint DEFAULT NULL COMMENT '群聊id',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者id',
  `msg` text NOT NULL COMMENT '消息内容',
  `create_time` datetime(3) DEFAULT NULL,
  `type` enum('text','image','file','speech','emoji') DEFAULT 'text' COMMENT '消息类型',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1-删除 0-正常',
  PRIMARY KEY (`id`),
  KEY `idx_group_sent` (`group_id`,`create_time`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_sent_at` (`create_time`),
  KEY `idx_group_sent_detailed` (`group_id`,`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_messages`
--

LOCK TABLES `group_messages` WRITE;
/*!40000 ALTER TABLE `group_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_notice`
--

DROP TABLE IF EXISTS `group_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL COMMENT '群聊id',
  `publisher_id` bigint NOT NULL COMMENT '发布者id',
  `content` varchar(255) NOT NULL COMMENT '内容',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除 0-正常 1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_notice`
--

LOCK TABLES `group_notice` WRITE;
/*!40000 ALTER TABLE `group_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media`
--

DROP TABLE IF EXISTS `media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `emitter_id` bigint NOT NULL COMMENT '发起人',
  `receive_id` bigint NOT NULL COMMENT '接受人',
  `status` enum('accept','reject','wait','cancel') NOT NULL DEFAULT 'wait',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '发起时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`),
  KEY `idx` (`emitter_id`,`receive_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media`
--

LOCK TABLES `media` WRITE;
/*!40000 ALTER TABLE `media` DISABLE KEYS */;
/*!40000 ALTER TABLE `media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `msg` varchar(200) NOT NULL COMMENT '消息内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `version` bigint DEFAULT '0' COMMENT '版本号,乐观锁',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '1' COMMENT '(0为true,1为false)',
  `publisher_id` bigint NOT NULL COMMENT '发布管理员id',
  `like` int DEFAULT '0' COMMENT '点赞数',
  PRIMARY KEY (`id`),
  KEY `sys_notice__index_end_time` (`end_time`),
  KEY `sys_notice_create_time_index` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT 'id',
  `nick_name` varchar(20) DEFAULT NULL COMMENT '昵称',
  `pw` varchar(100) DEFAULT NULL COMMENT '密码',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `third_auth` json DEFAULT NULL COMMENT '第三方信息,冗余',
  `status` tinyint(1) DEFAULT '1' COMMENT '1 正常,0禁用',
  `icon` varchar(200) DEFAULT NULL COMMENT '用户头像',
  `age` tinyint DEFAULT NULL COMMENT '用户年龄',
  `gender` tinyint DEFAULT '2' COMMENT '1 男 2 女',
  `ip` varchar(50) DEFAULT NULL COMMENT '用户定位',
  `hobby` varchar(50) DEFAULT NULL COMMENT '兴趣',
  `birth` date DEFAULT NULL COMMENT '生日',
  `is_delete` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1 正常 2注销 3冻结',
  `role` enum('user','admin','super_admin') DEFAULT 'user' COMMENT '角色',
  `active_mobile` varchar(20) GENERATED ALWAYS AS ((case when (`is_delete` = 1) then `mobile` else NULL end)) STORED,
  `call_state` tinyint DEFAULT NULL COMMENT '通话状态机 1空闲 2响铃 3通话中',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_mobile` (`active_mobile`),
  KEY `uk_nick` (`nick_name`),
  KEY `idx_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `nick_name`, `pw`, `mobile`, `create_time`, `update_time`, `third_auth`, `status`, `icon`, `age`, `gender`, `ip`, `hobby`, `birth`, `is_delete`, `role`, `call_state`) VALUES (8888888888888888888,'智能助手机器人',NULL,'00000000000','2026-02-27 19:02:43',NULL,NULL,1,'\nhttps://summit-chat.oss-cn-beijing.aliyuncs.com/avatar/27f9aba78b54494a89990cd47fc47eba.gif',18,2,NULL,'帮助用户解决问题,陪伴用户聊天,欢迎cue我啊',NULL,1,'user',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_active`
--

DROP TABLE IF EXISTS `user_active`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_active` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) NOT NULL COMMENT '用户id',
  `active` bigint NOT NULL COMMENT '活跃度',
  `icon` varchar(255) NOT NULL COMMENT '头像',
  `nick_name` varchar(50) NOT NULL COMMENT '昵称',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `active` (`active`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户活跃度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_active`
--

LOCK TABLES `user_active` WRITE;
/*!40000 ALTER TABLE `user_active` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_active` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_link`
--

DROP TABLE IF EXISTS `user_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_link` (
  `link_user_id` bigint NOT NULL COMMENT '用户联系人id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `is_frequent` tinyint(1) DEFAULT '0' COMMENT '是否常用 0 否 1是',
  `remark` varchar(20) DEFAULT NULL COMMENT '备注',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除 0否 1是',
  UNIQUE KEY `idx_user_link_unique` (`user_id`,`link_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_link`
--

LOCK TABLES `user_link` WRITE;
/*!40000 ALTER TABLE `user_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_msg`
--

DROP TABLE IF EXISTS `user_msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_msg` (
  `id` varchar(64) NOT NULL,
  `content` text NOT NULL COMMENT 'msg',
  `send_time` datetime(3) DEFAULT NULL,
  `emitter_id` bigint NOT NULL COMMENT '发送人id',
  `receive_id` bigint NOT NULL COMMENT '接收人id',
  `status` tinyint(1) NOT NULL COMMENT '0 已发送 1 已撤回 2 未读',
  `emoji_id` bigint DEFAULT NULL COMMENT '表情id',
  `type` enum('text','emoji','speech','AI') DEFAULT NULL COMMENT '消息类型 text emoji speech',
  `session_seq` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_eid_rid_time_r` (`emitter_id`,`receive_id`,`send_time` DESC),
  KEY `idx_eid_rid_time_l` (`receive_id`,`emitter_id`,`send_time` DESC),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_msg`
--

LOCK TABLES `user_msg` WRITE;
/*!40000 ALTER TABLE `user_msg` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_msg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_space`
--

DROP TABLE IF EXISTS `work_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_space` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `today_new_user` int DEFAULT '0',
  `total_user` int DEFAULT '0',
  `today_new_msg` int DEFAULT '0',
  `total_msg` int DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `work_space_create_time_index` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作空间';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_space`
--

LOCK TABLES `work_space` WRITE;
/*!40000 ALTER TABLE `work_space` DISABLE KEYS */;
INSERT INTO `work_space` VALUES (1,7,7,5,5,'2026-02-22 07:34:07'),(2,0,7,9,14,'2026-02-23 08:39:27'),(3,0,7,6,24,NULL);
/*!40000 ALTER TABLE `work_space` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-08 16:38:27
