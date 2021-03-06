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
-- Table structure for table `esaminazione`
--

DROP TABLE IF EXISTS `esaminazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `esaminazione` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idStudente` int NOT NULL,
  `idEsame` int NOT NULL,
  `idVerbale` int DEFAULT NULL,
  `voto` enum('','assente','rimandato','riprovato','18','19','20','21','22','23','24','25','26','27','28','29','30','30 e lode') NOT NULL DEFAULT '',
  `stato` enum('non inserito','inserito','pubblicato','rifiutato','verbalizzato') NOT NULL DEFAULT 'non inserito',
  PRIMARY KEY (`id`),
  KEY `fk_idStudente_idx` (`idStudente`),
  KEY `fk_idEsame_idx` (`idEsame`),
  KEY `fk_idVerbale_idx` (`idVerbale`),
  CONSTRAINT `fk_idEsame` FOREIGN KEY (`idEsame`) REFERENCES `esame` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idStudente` FOREIGN KEY (`idStudente`) REFERENCES `utente` (`matricola`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idVerbale` FOREIGN KEY (`idVerbale`) REFERENCES `verbale` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `esaminazione`
--

LOCK TABLES `esaminazione` WRITE;
/*!40000 ALTER TABLE `esaminazione` DISABLE KEYS */;
INSERT INTO `esaminazione` VALUES (10000,800001,3000,3000,'rimandato','verbalizzato'),(10001,800001,3001,3001,'30','verbalizzato'),(20000,800001,2005,2005,'rimandato','verbalizzato'),(20001,800001,2006,2006,'rimandato','verbalizzato'),(20002,800001,2010,NULL,'','non inserito'),(20003,800002,2010,NULL,'','non inserito'),(20004,800003,2010,NULL,'','non inserito'),(20005,800004,2010,NULL,'','non inserito'),(20006,800005,2010,NULL,'','non inserito');
/*!40000 ALTER TABLE `esaminazione` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-27 13:23:57
