-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 28-11-2025 a las 16:33:49
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `registros`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `perfiles`
--

CREATE TABLE `perfiles` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `nombre_completo` varchar(100) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `perfiles`
--

INSERT INTO `perfiles` (`id`, `usuario_id`, `nombre_completo`, `correo`) VALUES
(1, 15, 'Cristian Gonzalez Villanueva', 'whoIsCriisss@gmail.com'),
(2, 19, 'Juana Ramirez Hernandez', 'ibarraaghty@gmail.com'),
(3, 21, 'AristPamGH', 'pamelaArista@gmail.com'),
(4, 24, 'Karolina Herrera', 'KarHer56@outlook.com'),
(5, 25, 'Isabel Viña Villanueva', 'isabelHgJ@hotmail.com'),
(6, 26, 'Abril Martínez Juarez', 'abrilMJ@hotmail.com'),
(7, 27, 'Cristofer Villegas Leandro', 'CrisThof@gmail.com'),
(8, 28, 'Jordan Michael', 'JordanMichael@gmail.com'),
(9, 30, 'Ice Piper', 'piperghJvc@gmail.com'),
(10, 31, 'noe ceron', 'neogta1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `usuario`, `correo`, `password`, `fecha_registro`) VALUES
(1, 'Cristian', 'crr7alejandrogc@gmail.com', '$2y$10$HG1IHgexR6D5oQ.klperQOjSDOKCFAn.JkBjzi5sVgxQSFyGU3Lxy', '2025-11-05 23:54:29'),
(2, 'Marissa', 'ItsMargg@gmail.com', '$2y$10$Jt2kW2gsXJ020QXosJPY1eSI.QK/qFf/tGn0wVIqJmwDFSWLA4Tki', '2025-11-06 00:00:58'),
(3, 'EduardoMD', 'eduardomont@gmail.com', '$2y$10$vQs4afK7DjfSY/NTEk7Lye77z5oT.v9hJxmOp0x3XJC6matYOnmtu', '2025-11-06 15:31:59'),
(10, 'Cris_Gunn', 'crr7alejandroc@gmail.com', '$2y$10$qzU.JZVF1BUpSdbpuJsAm.cQ7Ww3u2Imdy7EGM3cymaKMZmJsE0ni', '2025-11-15 02:02:04'),
(11, 'Risiss_Gunn', 'crisu@gmail.com', '$2y$10$LW27i6uvZhhwBaHz8G0JjeqQ2Eb79W6SoYb2BX0mz3w2v8KiJR0HO', '2025-11-15 02:09:08'),
(12, 'CrisDeidad', 'crr6gchyN@gmail.com', '$2y$10$jy5NdeTJvhN5PJXGmptfnO/lXitVTPBXs.2UwZl8At9I/pDtN41Wa', '2025-11-15 02:19:07'),
(13, 'Mariana', 'MariGGs@gmail.com', '$2y$10$CQwSbTD/Y65oCdgwMEDHI.qW0.n0EE7DwwNtBK6z5D08L.CG67yjG', '2025-11-15 02:26:33'),
(14, 'WhoIsMarG', 'itsMarigs@gmail.com', '$2y$10$BXm4Vg4.woWE8YzZgm1mbu00mtTMSvRE4VKIe093ytCA.JQQPwmoa', '2025-11-15 02:36:17'),
(15, 'WhoIsCros', 'whoIsCriisss@gmail.com', '$2y$10$uF26hdon.zK0.fRraa1W8.EDj2uiczkh1PpqAMtSZCsc4p.MPZ11O', '2025-11-15 02:44:21'),
(19, 'JuanaRam', 'ibarraaghty@gmail.com', '$2y$10$O53TNwmHpcFYnlggLo1RCuC1mkW0jY1NNtxJ0Ne7gcuV4NoSylVii', '2025-11-26 05:14:13'),
(21, 'Pamela Arista', 'pamelaArista@gmail.com', '$2y$10$DsrtAv6H5QV2Zcwiiy.jHu.Fv59WBwtH3NBB2bGDaP5YPZNKHaTxa', '2025-11-27 01:06:51'),
(24, 'Karolina', 'KarHer56@outlook.com', '$2y$10$EIf00lv8FOSgayb4A5ldOO2fhVKA88ONQOrdLDrFeHewatleeidqi', '2025-11-27 01:13:46'),
(25, 'IsabelIJ', 'isabelHgJ@hotmail.com', '$2y$10$q.2f4GAd8s5eJiHMvW0dl.G/YBSZshY4h5HdFCHUJ98Y0FtJL4cRe', '2025-11-27 02:00:45'),
(26, 'AbrilLlantiGH', 'abrilMJ@hotmail.com', '$2y$10$qRZ8oBE/tQqCq4DHrC0cU.QkSd6uy447d4OvpX/WKWeOtBMCoKarS', '2025-11-27 02:10:35'),
(27, 'CrisVillegas', 'CrisThof@gmail.com', '$2y$10$dxMoYXxuUywL9dznSLi/MujtOoye0Oz.KpVCO0cqk4dAZGpOgnqhO', '2025-11-27 02:53:48'),
(28, 'JordiGG', 'JordanMichael@gmail.com', '$2y$10$.nGlUizX9IahKib7xwJvR.UmT70AwnZo6MGDm/ayCluS86DJd/97e', '2025-11-27 04:19:30'),
(29, 'CrisAristaGunn', 'crisbj0510@gmail.com', '123456', '2025-11-27 04:29:30'),
(30, 'PiperGG', 'piperghJvc@gmail.com', '$2y$10$XyMMv9rpgQmRJXyCN5VvquzPIr3JOJJGx1apED/V0qUgPtbYdm3bG', '2025-11-27 06:25:21'),
(31, 'noe', 'neogta1', '$2y$10$08phmk.shgPkTrVCx9pdYO3FGjSs6Ie.vaCYml5PDMEv3gvMEl9Y.', '2025-11-27 15:39:35');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `perfiles`
--
ALTER TABLE `perfiles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `usuario` (`usuario`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `perfiles`
--
ALTER TABLE `perfiles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `perfiles`
--
ALTER TABLE `perfiles`
  ADD CONSTRAINT `perfiles_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
