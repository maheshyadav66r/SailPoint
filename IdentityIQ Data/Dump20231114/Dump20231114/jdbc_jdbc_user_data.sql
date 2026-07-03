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
-- Table structure for table `jdbc_user_data`
--

DROP TABLE IF EXISTS `jdbc_user_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jdbc_user_data` (
  `id` int(11) NOT NULL,
  `Name` varchar(17) NOT NULL,
  `FirstName` varchar(11) NOT NULL,
  `LastName` varchar(25) NOT NULL,
  `Department` varchar(22) NOT NULL,
  `EmployeeID` varchar(8) NOT NULL,
  `EmailAddress` varchar(28) NOT NULL,
  `NetworkID` varchar(17) NOT NULL,
  `managerID` varchar(75) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jdbc_user_data`
--

LOCK TABLES `jdbc_user_data` WRITE;
/*!40000 ALTER TABLE `jdbc_user_data` DISABLE KEYS */;
INSERT INTO `jdbc_user_data` VALUES (1,'James Smith','James','Smith','Executive Management','1a','James.Smith1@eshiam.com','James.Smith',NULL),(2,'Mary Johnson','Mary1','Johnson','Regional Operations','1a2a','Mary.Johnson@eshiam.com','Mary.Johnson','1a'),(3,'Robert Brown','Robert','Brown','Information Technology','1a2a3a','Robert.Brown@eshiam.com','Robert.Brown','1a2a'),(4,'Joseph Thompson','Joseph','Thompson','Information Technology','1a2a3a4a','Joseph.Thompson@eshiam.com','Joseph.Thompson','1a2a3a'),(5,'Margaret Garcia','Margaret','Garcia','Information Technology','1a2a3a4b','Margaret.Garcia@eshiam.com','Margaret.Garcia','1a2a3a'),(7,'Dorothy Robinson','Dorothy','Robinson','Information Technology','1a2a3a4d','Dorothy.Robinson@eshiam.com','Dorothy.Robinson','1a2a3a'),(8,'Christopher Clark','Christopher','Clark','Information Technology','1a2a3a4e','Christopher.Clark@eshiam.com','Christopher.Clark','1a2a3a'),(9,'Linda Davis','Linda','Davis','Information Technology','1a2a3b','Linda.Davis@eshiam.com','Linda.Davis','1a2a'),(10,'Lisa Rodriguez','Lisa','Rodriguez','Information Technology','1a2a3b4a','Lisa.Rodriguez@eshiam.com','Lisa.Rodriguez','1a2a3b'),(11,'Daniel Lewis','Daniel','Lewis','Information Technology','1a2a3b4b','Daniel.Lewis@eshiam.com','Daniel.Lewis','1a2a3b'),(12,'Nancy Lee','Nancy','Lee','Information Technology','1a2a3b4c','Nancy.Lee@eshiam.com','Nancy.Lee','1a2a3b'),(13,'Paul Walker','Paul','Walker','Information Technology','1a2a3b4d','Paul.Walker@eshiam.com','Paul.Walker','1a2a3b'),(14,'Karen Hall','Karen','Hall','Information Technology','1a2a3b4e','Karen.Hall@eshiam.com','Karen.Hall','1a2a3b'),(15,'Michael Miller','Michael','Miller','Engineering','1a2a3c','Michael.Miller@eshiam.com','Michael.Miller','1a2a'),(16,'Mark Allen','Mark','Allen','Engineering','1a2a3c4a','Mark.Allen@eshiam.com','Mark.Allen','1a2a3c'),(17,'Betty Young','Betty','Young','Engineering','1a2a3c4b','Betty.Young@eshiam.com','Betty.Young','1a2a3c'),(18,'Donald Hernandez','Donald','Hernandez','Engineering','1a2a3c4c','Donald.Hernandez@eshiam.com','Donald.Hernandez','1a2a3c'),(19,'Helen King','Helen','King','Engineering','1a2a3c4d','Helen.King@eshiam.com','Helen.King','1a2a3c'),(20,'George Wright','George','Wright','Engineering','1a2a3c4e','George.Wright@eshiam.com','George.Wright','1a2a3c'),(21,'Amy Cox','Amy','Cox','Accounting','1a2c3b4c','Amy.Cox@eshiam.com','Amy.Cox','Maria White'),(22,'Amanda Ross','Amanda','Ross','Regional Operations','1b2c','Amanda.Ross@eshiam.com','Amanda.Ross','Jerry Bennett'),(23,'Alan Bradley','Alan','Bradley','Engineering','1c2a3c4c','Alan.Bradley@eshiam.com','Alan.Bradley','Eugene Hawkins'),(24,'Ann Alexander','Ann','Alexander','Information Technology','1b2a3a4d','Ann.Alexander@eshiam.com','Ann.Alexander','Walter Henderson'),(26,'Andrea Hudson','Andrea','Hudson','Human Resources','1c2b3b','Andrea.Hudson@eshiam.com','Andrea.Hudson','Randy Knight'),(27,'Anna Ward','Anna','Ward','Accounting','1a2c3b4e','Anna.Ward@eshiam.com','Anna.Ward','Maria White'),(28,'Andrew Gray','Andrew','Gray','Finance','1a2c3c4c','Andrew.Gray@eshiam.com','Andrew.Gray','Charles Harris'),(29,'Annie Chavez','Annie','Chavez','Human Resources','1c2b3b4a','Annie.Chavez@eshiam.com','Annie.Chavez','Andrea Hudson'),(30,'Anthony Roberts','Anthony','Roberts','Human Resources','1a2b3b4b','Anthony.Roberts@eshiam.com','Anthony.Roberts','Elizabeth Taylor'),(31,'Antonio Franklin','Antonio','Franklin','Inventory','1c2b3c4a','Antonio.Franklin@eshiam.com','Antonio.Franklin','Russell Spencer'),(32,'Benjamin Hicks','Benjamin','Hicks','Inventory','1b2b3d4d','Benjamin.Hicks@eshiam.com','Benjamin.Hicks','Marie Hughes'),(33,'Betty Young','Betty','Young','Engineering','1a2a3c4b','Betty.Young@eshiam.com','Betty.Young','Michael Miller'),(34,'Ashley Simpson','Ashley','Simpson','Inventory','1b2b3c4d','Ashley.Simpson@eshiam.com','Ashley.Simpson','Harold Patterson'),(35,'Carlos Perkins','Carlos','Perkins','Human Resources','1c2b3a','Carlos.Perkins@eshiam.com','Carlos.Perkins','Randy Knight'),(36,'Carmen Hansen','Carmen','Hansen','Finance','1c2c3c4d','Carmen.Hansen@eshiam.com','Carmen.Hansen','Victor Pierce'),(37,'Bobby Stephens','Bobby','Stephens','Accounting','1c2c3a','Bobby.Stephens@eshiam.com','Bobby.Stephens','Lori Ferguson'),(38,'Clarence Harper','Clarence','Harper','Engineering','1c2a3d4b','Clarence.Harper@eshiam.com','Clarence.Harper','Marilyn Dunn'),(40,'Christine Long','Christine','Long','Human Resources','1b2b3b','Christine.Long@eshiam.com','Christine.Long','Dennis Barnes'),(41,'Cindy Little','Cindy','Little','Finance','1c2c3d4c','Cindy.Little@eshiam.com','Cindy.Little','Sara Berry'),(42,'Adam Kennedy','Adam','Kennedynew','Accounting','1b2c3a4e','Adam.Kennedy@eshiam.com','Adam.Kennedy','Douglas Flores');
/*!40000 ALTER TABLE `jdbc_user_data` ENABLE KEYS */;
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
