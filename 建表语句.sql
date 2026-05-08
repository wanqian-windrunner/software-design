-- MySQL dump 10.13  Distrib 9.3.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: edu_system
-- ------------------------------------------------------
-- Server version	9.3.0

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
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `exam_id` int NOT NULL AUTO_INCREMENT COMMENT '考试唯一编号',
  `name` varchar(100) DEFAULT NULL COMMENT '考试名称',
  `clazz_id` int NOT NULL COMMENT '考试所属班级',
  `state` tinyint NOT NULL DEFAULT '0' COMMENT '考试状态：0未启用，1启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `exam_name` varchar(100) NOT NULL COMMENT '考试名称',
  PRIMARY KEY (`exam_id`),
  KEY `idx_class` (`clazz_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES (4,NULL,1,1,'2026-05-06 08:16:19','2026-05-06 08:33:48','我我'),(7,NULL,11411,1,'2026-05-06 10:56:48','2026-05-06 10:57:04','1');
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `exam_id` int NOT NULL COMMENT '所属考试',
  `question_id` int NOT NULL COMMENT '题目唯一编号',
  `question` text NOT NULL COMMENT '题目内容',
  `answer` text NOT NULL COMMENT '答案',
  `state` tinyint NOT NULL DEFAULT '0' COMMENT '题目状态：0未启用，1启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`exam_id`,`question_id`),
  CONSTRAINT `fk_question_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (7,1,'1','1',1,'2026-05-06 10:56:52','2026-05-06 10:56:52');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `score`
--

DROP TABLE IF EXISTS `score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `score` (
  `test_id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` int NOT NULL COMMENT '考试ID',
  `id` int NOT NULL COMMENT '学生ID',
  `accuracy` decimal(5,2) DEFAULT '0.00' COMMENT '准确率',
  `ai_review` text COMMENT 'AI综合评价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`test_id`),
  UNIQUE KEY `uk_exam_student` (`exam_id`,`id`),
  KEY `id` (`id`),
  CONSTRAINT `score_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`) ON DELETE CASCADE,
  CONSTRAINT `score_ibfk_2` FOREIGN KEY (`id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生考试成绩表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `score`
--

LOCK TABLES `score` WRITE;
/*!40000 ALTER TABLE `score` DISABLE KEYS */;
INSERT INTO `score` VALUES (15,7,7,100.00,'优秀！表现非常好，对知识点掌握牢固。','2026-05-06 18:57:11','2026-05-06 18:57:16');
/*!40000 ALTER TABLE `score` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Table structure for table `student_answer`
--

DROP TABLE IF EXISTS `student_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_answer` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` int NOT NULL COMMENT '考试ID',
  `student_id` int NOT NULL COMMENT '学生ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `student_answer` text COMMENT '学生答案',
  `is_correct` tinyint DEFAULT '0' COMMENT '是否正确：0-错误，1-正确',
  `accuracy` decimal(5,2) DEFAULT '0.00' COMMENT '准确率',
  `reason` text COMMENT '评价原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student_question` (`exam_id`,`student_id`,`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生答题记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_answer`
--

LOCK TABLES `student_answer` WRITE;
/*!40000 ALTER TABLE `student_answer` DISABLE KEYS */;
INSERT INTO `student_answer` VALUES (1,2,7,1,'1',1,100.00,'回答正确，答案完全匹配！','2026-05-06 17:45:08'),(2,2,7,2,'1',0,0.00,'回答错误，请仔细审题。正确答案：3','2026-05-06 17:45:24'),(3,2,7,3,'1',0,80.00,'回答基本正确，但有细微差异。正确答案：23121132321','2026-05-06 17:45:35'),(15,5,7,1,'我',0,0.00,'标准答案为‘啊’，回答内容为‘我’，内容完全不一致，因此判定为错误','2026-05-06 18:41:00'),(16,5,7,4,'人工智能如今深入各行各业，极大提高了社会生产和工作的效率，在医疗诊断、天气预测、日常智能服务等方面都给人们生活带来了很多便利，也推动了科研领域的快速发展。同时人工智能发展也存在伦理问题，一方面智能系统会收集大量个人信息，容易造成隐私泄露；另一方面智能机器替代人工会让很多传统行业人员失业，而且算法还可能出现区别对待不同人群的不公平现象。',1,95.00,'回答内容完整覆盖了标准答案的核心要点，所有核心表述均准确对应，仅表述相对简洁凝练，未改变核心语义，不存在错误内容','2026-05-06 18:43:46'),(17,6,7,1,'你是我',0,50.00,'回答内容相比标准答案缺失了一个\'的\'字，没有完整匹配标准答案内容','2026-05-06 18:52:57'),(18,6,7,2,'我我',1,67.00,'标准答案要求内容为三个‘我’，回答内容仅包含两个‘我’，内容不完整，不符合标准答案要求','2026-05-06 18:53:55'),(19,7,7,1,'1',1,100.00,'回答内容与标准答案完全一致，回答正确','2026-05-06 18:57:15');
/*!40000 ALTER TABLE `student_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（建议加密存储）',
  `identity` tinyint NOT NULL DEFAULT '0' COMMENT '身份：0学生，1老师',
  `clazz_id` int NOT NULL DEFAULT '0' COMMENT '班级（老师注册时不可为0）',
  `verify` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0待审，1通过，2班主任',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'qkl','123123',0,11411,1),(3,'123','123123',1,1,0),(6,'2','123123',1,11411,0),(7,'1','123123',0,11411,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-06 22:01:26
