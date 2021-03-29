-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: tiw-project-db
-- ------------------------------------------------------
-- Server version	8.0.23

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
-- Table structure for table `frequentazione`
--

DROP TABLE IF EXISTS `frequentazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `frequentazione` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matricolaStudente` int NOT NULL,
  `idCorso` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_matricolaStudente_idx` (`matricolaStudente`),
  KEY `fk_idCorso_idx` (`idCorso`),
  CONSTRAINT `fk_idCorso` FOREIGN KEY (`idCorso`) REFERENCES `corso` (`id`),
  CONSTRAINT `fk_matricolaStudente` FOREIGN KEY (`matricolaStudente`) REFERENCES `utente` (`matricola`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `frequentazione`
--

LOCK TABLES `frequentazione` WRITE;
/*!40000 ALTER TABLE `frequentazione` DISABLE KEYS */;
INSERT INTO `frequentazione` VALUES (1,800001,1001),(2,800001,2000),(3,800001,2001),(4,800001,3000),(5,800002,1001),(6,800002,2001),(7,800002,3000),(8,800002,4000),(9,800003,1001),(10,800003,2000),(11,800003,3000),(12,800003,3001),(13,800004,1000),(14,800005,1000),(15,800001,1002),(16,800002,1002),(17,800003,1002),(18,800004,1002),(19,800005,1002);
/*!40000 ALTER TABLE `frequentazione` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-29 17:08:49
