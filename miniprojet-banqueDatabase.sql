-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 11, 2025 at 12:01 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `miniprojet-banque`
--

-- --------------------------------------------------------

--
-- Table structure for table `authentification`
--

CREATE TABLE `authentification` (
  `authId` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `motDePasse` varchar(255) NOT NULL,
  `clientId` int(11) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `authentification`
--

INSERT INTO `authentification` (`authId`, `nom`, `motDePasse`, `clientId`, `role`) VALUES
(3, 'Mohammed', '12346', 3, 'user'),
(1000, 'admin', 'admin123', 1000, 'admin'),
(1001, 'Hamada', '12345', 1006, 'user'),
(1003, 'Hassan', '123', 1010, 'user'),
(1004, 'Ayman', '12345', 1015, 'user'),
(1006, 'test', '123', 1017, 'user'),
(1015, 'Simo', 'Simo123', 1031, 'user');

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `clientId` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `tel` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`clientId`, `nom`, `prenom`, `email`, `tel`) VALUES
(3, 'Updated Name', 'Updated Surname', 'updated.email@example.com', '0987654321'),
(1000, 'Admin', 'User', 'admin@banque.com', '0600000000'),
(1001, 'Chamakh', 'Mohammed', 'Chamakh@gmail.com', '0600220022'),
(1002, 'tt', 'tst', 't@gmail.com', '0700229933'),
(1003, 'Tantani', 'Ayman', 'Tantani@gmail.com', '0611090901'),
(1004, 'test123', 'test123', 'test@gmail.com', '0622092123'),
(1005, 'Hassan', 'Ahmed', 'Ahmed@gmail.com', '0700990088'),
(1006, 'Hamada', 'Hamada', 'hamada@gmail.com', '0799223344'),
(1010, 'Hassan', 'Ahmed', 'Hassan', '0622938422'),
(1015, 'Tantani', 'Ayman', 'ayman@gmail.com', '0600009988'),
(1017, 'Johnn', 'Doee', 'john.doe@example.com', '0700556622'),
(1031, 'Simo', 'Simo', 'Simo@example.com', '0710556622');

-- --------------------------------------------------------

--
-- Table structure for table `compte`
--

CREATE TABLE `compte` (
  `numCompte` varchar(20) NOT NULL,
  `solde` decimal(15,2) DEFAULT 0.00,
  `dateCreation` date NOT NULL,
  `statut` varchar(20) DEFAULT 'ACTIVE',
  `clientId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `compte`
--

INSERT INTO `compte` (`numCompte`, `solde`, `dateCreation`, `statut`, `clientId`) VALUES
('12345', 2200.00, '2025-04-09', 'active', 3),
('453790', 1000.00, '2025-04-09', 'Active', 1031),
('456790', 1582.50, '2025-04-09', 'Active', 1017),
('ACC567890123', 600.00, '2025-04-10', 'Actif', 3);

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transactionId` int(11) NOT NULL,
  `type` varchar(20) NOT NULL,
  `montant` decimal(15,2) NOT NULL,
  `dateTransaction` datetime NOT NULL,
  `numCompte` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`transactionId`, `type`, `montant`, `dateTransaction`, `numCompte`) VALUES
(3, 'Deposit', 200.00, '2025-04-05 12:00:00', 'ACC567890123'),
(13, 'Virement Crédit', 5.00, '2025-04-09 12:55:09', 'ACC567890123'),
(15, 'Virement reçu', 5.00, '2025-04-09 12:55:09', 'ACC567890123'),
(17, 'Virement reçu', 5.00, '2025-04-09 13:09:30', 'ACC567890123'),
(18, 'Deposit', 90.00, '2025-04-09 13:11:02', 'ACC567890123'),
(19, 'Deposit', 10.00, '2025-04-09 13:12:08', 'ACC567890123'),
(20, 'Virement envoyé', 10.00, '2025-04-09 13:12:29', 'ACC567890123'),
(31, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(33, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(35, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(37, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(43, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(48, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(53, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(57, 'Deposit', 5000.00, '2025-04-10 01:42:21', 'ACC567890123'),
(58, 'Withdrawal', 50.00, '2025-04-10 01:44:35', 'ACC567890123'),
(59, 'Virement envoyé', 50.00, '2025-04-10 01:45:38', 'ACC567890123'),
(60, 'Virement reçu', 50.00, '2025-04-10 01:45:38', '456790'),
(62, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(64, 'Withdrawal', 300.00, '2025-04-10 02:50:41', '12345'),
(65, 'Deposit', 500.00, '2025-04-10 02:50:41', '12345'),
(66, 'Deposit', 500.00, '2025-04-09 00:00:00', '12345'),
(67, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(69, 'Withdrawal', 300.00, '2025-04-10 02:53:08', '12345'),
(70, 'Deposit', 500.00, '2025-04-10 02:53:08', '12345'),
(71, 'Deposit', 500.00, '2025-04-09 00:00:00', '12345'),
(72, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(74, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(76, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(78, 'Withdrawal', 300.00, '2025-04-10 03:26:38', '12345'),
(79, 'Deposit', 500.00, '2025-04-10 03:26:38', '12345'),
(80, 'Deposit', 500.00, '2025-04-09 00:00:00', '12345'),
(81, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(83, 'Withdrawal', 300.00, '2025-04-10 10:11:02', '12345'),
(84, 'Deposit', 500.00, '2025-04-10 10:11:02', '12345'),
(85, 'Deposit', 500.00, '2025-04-09 00:00:00', '12345'),
(86, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(88, 'Withdrawal', 300.00, '2025-04-10 10:12:23', '12345'),
(89, 'Deposit', 500.00, '2025-04-10 10:12:23', '12345'),
(90, 'Deposit', 500.00, '2025-04-09 00:00:00', '12345'),
(91, 'deposit', 500.00, '2025-04-09 00:00:00', 'ACC567890123'),
(93, 'Deposit', 800.00, '2025-04-10 22:17:45', '456790'),
(94, 'Deposit', 300.00, '2025-04-10 22:17:56', '456790'),
(95, 'Virement envoyé', 600.00, '2025-04-10 22:18:23', '456790'),
(96, 'Virement reçu', 600.00, '2025-04-10 22:18:23', 'ACC567890123'),
(97, 'Withdrawal', 90.00, '2025-04-10 22:18:31', '456790'),
(98, 'Deposit', 22.50, '2025-04-10 22:18:42', '456790'),
(99, 'Deposit', 100.00, '2025-04-10 22:18:52', '456790');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `authentification`
--
ALTER TABLE `authentification`
  ADD PRIMARY KEY (`authId`),
  ADD UNIQUE KEY `nom` (`nom`),
  ADD UNIQUE KEY `clientId` (`clientId`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`clientId`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `compte`
--
ALTER TABLE `compte`
  ADD PRIMARY KEY (`numCompte`),
  ADD KEY `fk_compte_client` (`clientId`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transactionId`),
  ADD KEY `fk_transaction_compte` (`numCompte`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `authentification`
--
ALTER TABLE `authentification`
  MODIFY `authId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1018;

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `clientId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1036;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transactionId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=100;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `authentification`
--
ALTER TABLE `authentification`
  ADD CONSTRAINT `fk_authentification_client` FOREIGN KEY (`clientId`) REFERENCES `client` (`clientId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `compte`
--
ALTER TABLE `compte`
  ADD CONSTRAINT `fk_compte_client` FOREIGN KEY (`clientId`) REFERENCES `client` (`clientId`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `fk_transaction_compte` FOREIGN KEY (`numCompte`) REFERENCES `compte` (`numCompte`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
