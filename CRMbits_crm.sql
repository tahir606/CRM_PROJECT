-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 12, 2018 at 06:51 PM
-- Server version: 10.1.24-MariaDB
-- PHP Version: 7.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bits_crm`
--

-- --------------------------------------------------------

--
-- Table structure for table `email_settings`
--

DROP TABLE IF EXISTS `email_settings`;
CREATE TABLE `email_settings` (
  `ECODE` int(11) NOT NULL,
  `HOST` varchar(500) NOT NULL,
  `EMAIL` varchar(500) NOT NULL,
  `PASS` varchar(500) NOT NULL,
  `FSPATH` varchar(2000) DEFAULT NULL,
  `AUTOCHK` tinyint(1) DEFAULT NULL,
  `DISCCHK` tinyint(1) DEFAULT NULL,
  `AUTOTXT` text,
  `DISCTXT` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `email_settings`
--

INSERT INTO `email_settings` (`ECODE`, `HOST`, `EMAIL`, `PASS`, `FSPATH`, `AUTOCHK`, `DISCCHK`, `AUTOTXT`, `DISCTXT`) VALUES
(1, 'burhanisolutions.com.pk', 'sales@burhanisolutions.com.pk', 'burhanisales', 'C:\\Users\\Tahir\\Bits\\CRM\\Files\\', 1, 1, 'Thank you for contacting Burhani Customer Service.\nYour complaint has been successfully registered.\nOur IT department has started working to resolve your issue.\nWe will notify you of any further development.', 'BITS Customer Support.');

-- --------------------------------------------------------

--
-- Table structure for table `email_store`
--

DROP TABLE IF EXISTS `email_store`;
CREATE TABLE `email_store` (
  `EMNO` int(11) NOT NULL,
  `MSGNO` int(11) NOT NULL,
  `SBJCT` varchar(10000) NOT NULL,
  `TOADD` varchar(1000) NOT NULL,
  `FRADD` varchar(1000) NOT NULL,
  `EBODY` text NOT NULL,
  `ATTCH` varchar(10000) NOT NULL,
  `CCADD` varchar(5000) NOT NULL,
  `ESOLV` char(1) NOT NULL,
  `SOLVBY` int(11) DEFAULT NULL,
  `LOCKD` int(11) NOT NULL,
  `TSTMP` datetime NOT NULL,
  `FREZE` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `email_store`
--

INSERT INTO `email_store` (`EMNO`, `MSGNO`, `SBJCT`, `TOADD`, `FRADD`, `EBODY`, `ATTCH`, `CCADD`, `ESOLV`, `SOLVBY`, `LOCKD`, `TSTMP`, `FREZE`) VALUES
(12, 20, 'Tesat', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\ntesla\r\n', 'No Attachments', '', 'S', 0, 1, '2018-01-02 11:40:31', 1),
(13, 21, 'Test', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nTest with different attach\r\n', '.\\files\\companyprofile.pdf^', '', 'S', 0, 1, '2018-01-02 12:49:01', 1),
(14, 25, 'mail', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nconnect exception\r\n', 'No Attachments', '', 'S', 0, 1, '2018-01-04 02:06:21', 1),
(15, 26, 'Check Ticket', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nReply added\r\n', 'No Attachments', '', 'S', 0, 1, '2018-01-04 03:58:16', 1),
(16, 27, 'MAIL TEST', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nTEST MAIL\r\n', 'No Attachments', '', 'N', 0, 1, '2018-01-04 10:17:08', 1),
(17, 28, 'MAIL test', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\ntest 2 mail\r\n', 'No Attachments', '', 'N', 0, 1, '2018-01-04 10:22:08', 1),
(18, 11, 'Checking Reply', '^sales@burhanisolutions.com.pk', '^tahir606@burhanisolutions.com.pk', '\nEmail to Check Reply\r\n\r\n', 'No Attachments', '^tahir60652@gmail.com', 'S', 0, 1, '2018-01-05 11:33:58', 1),
(19, 10, 'What', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nTesting the smol btns\r\n', 'No Attachments', '', 'N', 0, 0, '2018-01-05 03:28:11', 1),
(20, 11, 'Testing', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nTesting load\r\n', 'No Attachments', '', 'N', 0, 1, '2018-01-05 03:29:04', 1),
(21, 12, 'NO CLUE', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nNO CLUE WHATS HAPPENING\r\n', 'No Attachments', '', 'N', 0, 1, '2018-01-05 04:09:31', 1),
(22, 13, 'I AM', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nLet it gooooo let it\r\n\r\n\r\ngooooo\r\n', 'No Attachments', '', 'N', 0, 1, '2018-01-05 04:28:55', 1),
(23, 14, 'check', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\ncheck imdex\r\n', 'No Attachments', '', 'N', 0, 1, '2018-01-05 04:30:11', 1),
(24, 15, 'w', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nwhasd\r\n', 'No Attachments', '', 'N', 0, 0, '2018-01-05 04:32:03', 1),
(25, 1, 'No', '^\"sales@burhanisolutions.com.pk\" <sales@burhanisolutions.com.pk>', '^Tahir Shakir <tahir60652@gmail.com>', '\nJust just\r\nNo\r\n', 'No Attachments', '', 'N', NULL, 0, '2018-01-11 01:08:21', 1),
(26, 2, 'Teset', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\ntest email\r\n', 'No Attachments', '', 'N', NULL, 2, '2018-01-11 04:28:06', 0),
(27, 4, 'Attachment Check Again', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nWhat the thel\r\n', 'No Attachments', '', 'S', NULL, 0, '2018-01-13 07:48:31', 0),
(28, 5, 'Attahm', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nforgot to\r\n', 'C:/Users/Tahir/Bits/CRM/Files/HPS.pdf^', '', 'S', NULL, 1, '2018-01-13 07:49:25', 0),
(29, 9, 'After', '^sales@burhanisolutions.com.pk', '^Tahir Shakir <tahir60652@gmail.com>', '\nI hjave no idea\r\n', 'C:/Users/Tahir/Bits/CRM/Files/HPS.pdf^', '', 'S', NULL, 1, '2018-01-16 11:56:20', 1),
(30, 15, 'VARnish', '^sales@burhanisolutions.com.pk', '^Tahir Shakir <tahir60652@gmail.com>', '\nI have no idea\r\n', 'C:\\Users\\Tahir\\Bits\\CRM\\Files\\2nd-Hourly-Sun.pdf^', '', 'S', NULL, 0, '2018-01-17 09:52:58', 1),
(31, 11, 'Testing', '^sales@burhanisolutions.com.pk', '^Tahir Shakir <tahir60652@gmail.com>', '\nTesting email file\r\n', 'C:\\Users\\Tahir\\Bits\\CRM\\Files\\2nd-Hourly-Mon.pdf^', '', 'S', NULL, 0, '2018-01-20 06:04:40', 1),
(32, 8, 'TRISTATE', '^sales@burhanisolutions.com.pk', '^Tahir Shakir <tahir60652@gmail.com>', '\nTRISTATE AREA  TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE\r\nAREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE\r\nAREA TRISTATE\r\nAREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE\r\nAREA TRISTATE\r\nAREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE AREA TRISTATE\r\nAREA TRISTATE\r\nAREA TRISTATE AREA TRISTATE AREA TRISTATE AREA\r\n', 'C:\\Users\\Tahir\\Bits\\CRM\\Files\\\\Essay.docx^', '', 'N', NULL, 0, '2018-01-30 10:13:44', 1),
(33, 9, 'Test Email', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nBRUKH BRUKH\r\n', '', '', 'S', NULL, 1, '2018-02-02 07:54:20', 0),
(34, 10, 'TRAY', '^sales@burhanisolutions.com.pk', '^tahir shakir <tahirshakir606@gmail.com>', '\nTrayer Grave\r\n', '', '', 'S', NULL, 1, '2018-02-02 07:54:46', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `UCODE` int(11) NOT NULL,
  `FNAME` varchar(200) NOT NULL,
  `UNAME` varchar(300) NOT NULL,
  `Email` varchar(500) NOT NULL,
  `UPASS` varchar(500) NOT NULL,
  `NOTE` varchar(500) DEFAULT NULL,
  `URIGHT` varchar(100) NOT NULL,
  `FREZE` varchar(1) NOT NULL,
  `SOLV` int(11) DEFAULT NULL,
  `LOCKD` int(11) DEFAULT NULL,
  `ISLOG` tinyint(1) DEFAULT NULL,
  `ISEMAIL` char(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UCODE`, `FNAME`, `UNAME`, `Email`, `UPASS`, `NOTE`, `URIGHT`, `FREZE`, `SOLV`, `LOCKD`, `ISLOG`, `ISEMAIL`) VALUES
(1, 'Tahir Shakir', 'tahir606', 'tahir60652@gmail.com', 'pin123', 'Super Admin', 'Admin', 'N', 10, 0, 1, 'Y'),
(2, 'Shakir Hussain', 'mustoopk', 'mustoopk@gmail.com', 'pin123', 'Admin', 'Admin', 'N', NULL, 0, 0, 'N'),
(4, 'Mufaddal ', 'Muffi', 'm@g.com', 'pin123', NULL, 'Not Admin', 'N', NULL, NULL, 0, 'N');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `email_store`
--
ALTER TABLE `email_store`
  ADD PRIMARY KEY (`EMNO`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`UCODE`),
  ADD UNIQUE KEY `UNAME` (`UNAME`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;