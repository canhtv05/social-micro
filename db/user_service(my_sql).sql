CREATE DATABASE  IF NOT EXISTS `user_service` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `user_service`;
-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: user_service
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('22222222-2222-2222-2222-222222222222','ADMIN','2025-07-10 15:15:45','2025-07-10 15:15:45'),('33333333-3333-3333-3333-333333333333','USER','2025-07-10 15:15:45','2025-07-10 15:15:45');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` char(36) NOT NULL,
  `role_id` char(36) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES ('09ca2612-3ce9-464e-855f-a89e98c00ef9','22222222-2222-2222-2222-222222222222'),('9b0e66b4-a108-46f2-bf8f-bdc82592e93b','22222222-2222-2222-2222-222222222222'),('09ca2612-3ce9-464e-855f-a89e98c00ef9','33333333-3333-3333-3333-333333333333'),('9b0e66b4-a108-46f2-bf8f-bdc82592e93b','33333333-3333-3333-3333-333333333333');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `user_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `refresh_token` text,
  `user_status` enum('ACTIVE','INACTIVE','BANNED') NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('09ca2612-3ce9-464e-855f-a89e98c00ef9','canhtv@example.com','canhtv@example.com','$2a$10$wv7p62dM.gb8q8BSNZZwAeRGfLfmWZFx5WEV7gBSF2VYphozLELVC','eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJjYW5odHYwNSIsInN1YiI6IjA5Y2EyNjEyLTNjZTktNDY0ZS04NTVmLWE4OWU5OGMwMGVmOSIsImV4cCI6MTc1NTkzMjU5NSwiaWF0IjoxNzU0NzIyOTk1LCJqdGkiOiIwMzg5NWI4NC1kMzIzLTQ1YjgtODk5OS0xMWQ1NmE0Yjk0NDciLCJlbWFpbCI6ImNhbmh0dkBleGFtcGxlLmNvbSJ9.ddP1yPxkJ0OvJBcAdJ_og0ksljKE9K2X3T-UDh80Za6UkkDOnS3byB9ma6zk8Sm2DXL4u9-qOt--Q57_Z627gg','ACTIVE','2025-08-08 21:34:35','2025-08-09 07:03:15','MALE'),('9b0e66b4-a108-46f2-bf8f-bdc82592e93b','hello','hello123@gmail.com','$2a$10$2.xa7SCcKnfgJOlBfVqVXuKp9CRQNX9JXFZIMdE7/d5ubnfYKy.QC','eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJjYW5odHYwNSIsInN1YiI6IjliMGU2NmI0LWExMDgtNDZmMi1iZjhmLWJkYzgyNTkyZTkzYiIsImV4cCI6MTc1NTkzMzE5OSwiaWF0IjoxNzU0NzIzNTk5LCJqdGkiOiI3NzY2NGNjMS0wYThmLTQ5ZDYtYjJkNy1kNzBjZjFmMjA0ODEiLCJlbWFpbCI6ImhlbGxvMTIzQGdtYWlsLmNvbSJ9.oK0lTE5UXnc8f7z57xfyG1Nbe7d1buGniZPtGQPwSAwouB-Ig7lbyeT11w_8hSubTuoCG0uk46Qw5w_EDBzTTw','ACTIVE','2025-08-08 21:35:27','2025-08-09 07:13:19','OTHER');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'user_service'
--

--
-- Dumping routines for database 'user_service'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-06 10:55:41
