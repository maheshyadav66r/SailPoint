-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: jdbc
-- ------------------------------------------------------
-- Server version	5.7.42-log

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
-- Table structure for table `jdbc_groups_data`
--

DROP TABLE IF EXISTS `jdbc_groups_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jdbc_groups_data` (
  `gid` int(11) NOT NULL,
  `GroupName` varchar(20) NOT NULL,
  `GroupCategory` varchar(8) NOT NULL,
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jdbc_groups_data`
--

LOCK TABLES `jdbc_groups_data` WRITE;
/*!40000 ALTER TABLE `jdbc_groups_data` DISABLE KEYS */;
INSERT INTO `jdbc_groups_data` VALUES (1,'Finance','Security'),(2,'R&D','Security'),(3,'IT','Security'),(4,'HR','Security'),(5,'Executive','Security'),(6,'AccountingGeneral','Security'),(7,'AccountsPayable','Security'),(8,'AccountsReceivable','Security'),(9,'Admins','Security'),(10,'All_Users','Security'),(11,'AuditMgmt','Security'),(12,'BenefitCommittee','Security'),(13,'Benefits','Security'),(14,'Buyer','Security'),(15,'campusAccess','Security'),(16,'Compensation','Security'),(17,'Contractors','Security'),(18,'Corporate-VPN','Security'),(19,'Cost-SavingsAnalysis','Security'),(20,'customerDataCenter','Security'),(21,'DataArchive','Security'),(22,'DataComm','Security'),(23,'DBAs','Security'),(24,'DEV_All','Security'),(25,'DEV_EastCoast','Security'),(26,'DEV_Europe','Security'),(27,'DEV_Internal','Security'),(28,'DEV_Mgmt','Security'),(29,'DEV_Projects','Security'),(30,'DEV_Stage','Security'),(31,'DEV_WestCoast','Security'),(32,'Development','Security'),(33,'DispenseRX','Security'),(34,'DNSAdministration','Security'),(35,'Employeement','Security'),(36,'ENG_All','Security'),(37,'ENG_EastCoast','Security');
/*!40000 ALTER TABLE `jdbc_groups_data` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-14 11:00:35
