-- MySQL dump 10.13  Distrib 5.7.26, for Linux (x86_64)
--
-- Host: host    Database: startapped
-- ------------------------------------------------------
-- Server version	version

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `${prefix}_accounts`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_accounts`
(
    `id`              varchar(255) NOT NULL,
    `username`        varchar(255) NOT NULL,
    `email`           longtext     NOT NULL,
    `hash`            longtext     NOT NULL,
    `phone_number`    varchar(255) NOT NULL,
    `birthday`        varchar(255) NOT NULL,
    `safe_search`     tinyint(1)   NOT NULL,
    `verified`        tinyint(1)   NOT NULL,
    `email_confirmed` tinyint(1)   NOT NULL,
    `admin`           tinyint(1)   NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_auth`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_auth`
(
    `id`            varchar(255) NOT NULL,
    `refresh_token` varchar(64)  NOT NULL,
    `access_token`  varchar(64)  NOT NULL,
    `expire`        mediumtext   NOT NULL,
    PRIMARY KEY (`refresh_token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_blog`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_blog`
(
    `id`               varchar(255) NOT NULL,
    `base_url`         longtext     NOT NULL,
    `complete_url`     longtext     NOT NULL,
    `blog_type`        varchar(255) NOT NULL,
    `name`             longtext     NOT NULL,
    `description`      longtext     NOT NULL,
    `icon_url`         longtext     NOT NULL,
    `background_color` varchar(255) NOT NULL,
    `background_url`   longtext     NOT NULL,
    `allow_under_18`   tinyint(1)   NOT NULL,
    `nsfw`             tinyint(1)   NOT NULL,
    `show_age`         tinyint(1)   DEFAULT NULL,
    `owners`           longtext,
    `owner`            varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_bookmark`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_bookmark`
(
    `id`        int(11)      NOT NULL AUTO_INCREMENT,
    `user_id`   varchar(255) NOT NULL,
    `post_id`   varchar(255) NOT NULL,
    `timestamp` mediumtext   NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 40
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_confirmation`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_confirmation`
(
    `id`   varchar(255) NOT NULL,
    `code` varchar(32)  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_file`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_file`
(
    `hash`        varchar(255) NOT NULL,
    `uploader_id` varchar(255) NOT NULL,
    `name`        longtext     NOT NULL,
    `url`         longtext     NOT NULL,
    `path`        longtext     NOT NULL,
    `timestamp`   mediumtext   NOT NULL,
    PRIMARY KEY (`hash`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_follow`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_follow`
(
    `id`           int(11)      NOT NULL AUTO_INCREMENT,
    `user_id`      varchar(255) NOT NULL,
    `following_id` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_post`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_post`
(
    `id`             varchar(255) NOT NULL,
    `creator_id`     varchar(255) NOT NULL,
    `origin_blog_id` varchar(255) NOT NULL,
    `permalink`      longtext     NOT NULL,
    `full_url`       longtext     NOT NULL,
    `post_type`      varchar(255) NOT NULL,
    `timestamp`      mediumtext   NOT NULL,
    `title`          longtext     NOT NULL,
    `body`           longtext     NOT NULL,
    `nsfw`           tinyint(1)   NOT NULL,
    `parent`         varchar(255) DEFAULT NULL,
    `tags`           longtext     NOT NULL,
    `image_url`      longtext,
    `audio_url`      longtext,
    `video_url`      longtext,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `${prefix}_record`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `${prefix}_record`
(
    `blog_id`   varchar(255) NOT NULL,
    `record_id` longtext     NOT NULL,
    PRIMARY KEY (`blog_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2019-06-23  0:34:23
