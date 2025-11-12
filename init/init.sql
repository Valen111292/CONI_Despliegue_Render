-- Este script se ejecuta automáticamente cuando se inicia el contenedor de MySQL.
-- Contiene las sentencias de CREATE TABLE y la inserción de datos (INSERT INTO).

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Estructura de tabla para la tabla `actas`
--
CREATE TABLE `actas` (
  `id_acta` int(11) NOT NULL,
  `nombre_completo` varchar(100) NOT NULL,
  `cedula` varchar(20) NOT NULL,
  `n_inventario` varchar(255) NOT NULL,
  `fecha` date NOT NULL DEFAULT (CURRENT_DATE),
  `ruta_pdf` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `actas`
--

INSERT INTO `actas` (`id_acta`, `nombre_completo`, `cedula`, `n_inventario`, `fecha`, `ruta_pdf`) VALUES
(1, 'christian', '1110550665', 'PER03,EQ04', '2025-06-17', 'pdfs/Acta_1110550665.pdf'),
(2, 'christian', '940726', 'EQ05', '2025-06-17', 'pdfs/Acta_940726.pdf'),
(3, 'sfthy', '1583', 'PER06', '2025-07-04', 'pdfs/Acta_1583.pdf'),
(4, 'christian', '6654565962', 'PER01,PER03,EQ05', '2025-07-06', 'pdfs/Acta_6654565962.pdf'),
(5, 'christian', '6654565962', 'PER01,PER03,EQ05', '2025-07-06', 'pdfs/Acta_6654565962.pdf'),
(6, 'carlos', '654687463', 'PER06', '2025-07-06', 'pdfs/Acta_654687463.pdf');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleados`
--

CREATE TABLE `empleados` (
  `id_empleado` varchar(10) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `cedula` varchar(15) NOT NULL,
  `email` varchar(100) NOT NULL,
  `cargo` varchar(50) NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `empleados`
--

INSERT INTO `empleados` (`id_empleado`, `nombre`, `cedula`, `email`, `cargo`, `fecha_registro`) VALUES
('APR02', 'valentina osorio', '12346835', 'valen@gmail.com', 'aprendiz', '2025-06-18 00:05:24'),
('APR03', 'christian', '94072604843', 'andres@gmail.com', 'aprendiz', '2025-06-19 00:47:14'),
('APR04', 'valentina osorio', '68545688', 'valentina@gmail.com', 'Aprendiz', '2025-06-20 03:17:35'),
('APR05', 'colmo', '1234567', 'colmillo@gmail.com', 'Aprendiz', '2025-06-25 03:26:41'),
('EMP01', 'yotas', '1234567890', 'yotas@gmail.com', 'Otro', '2025-06-25 02:18:50'),
('EMP02', 'sadsaj', '346834513', 'asdkjah@asdkha.com', '--- Seleccione un cargo ---', '2025-06-25 02:27:59'),
('GER01', 'christian', '1110550665', 'an.sa.pa@gmail.com', 'Gerente de Distribuciones', '2025-06-17 22:41:58'),
('GER02', 'ANA MARIA CORTES', '1110550660', 'ANAMARIA@GMAIL.COM', 'Gerente de Distribuciones', '2025-11-04 16:53:08'),
('LOG01', 'mijo', '0123456789', 'mijo@gmail.com', 'Auxiliar de Logistica', '2025-06-25 02:21:40'),
('LOG02', 'kjasdaiu', '654335', 'asdjjlaskdjli@gkashdka.com', 'Auxiliar de Logistica', '2025-06-25 02:37:12'),
('TES01', 'carlos', '111111111', 'carlos@gmail.com', 'Tesorero', '2025-06-25 02:17:41'),
('VEN01', 'chris', '6545494', 'dsjkdgsk@josdi.com', 'Ejecutivo(a) de Ventas', '2025-06-20 00:12:45'),
('VEN02', 'huesito chavacano ', '321654987', 'hueso@gmail.com', 'Ejecutivo(a) de Ventas', '2025-07-05 20:20:41'),
('VEN03', 'FABIAN CONTRERAS', '658263954', 'FABIANCON@GMAIL.COM', 'Ejecutivo(a) de Ventas', '2025-11-03 22:32:31');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `equipos_perifericos`
--

CREATE TABLE `equipos_perifericos` (
  `n_inventario` varchar(512) DEFAULT NULL,
  `n_serie` varchar(512) DEFAULT NULL,
  `tipo` varchar(100) DEFAULT NULL,
  `clase` varchar(512) DEFAULT NULL,
  `marca` varchar(512) DEFAULT NULL,
  `ram` varchar(512) DEFAULT NULL,
  `disco` varchar(512) DEFAULT NULL,
  `procesador` varchar(512) DEFAULT NULL,
  `estado` varchar(512) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `equipos_perifericos`
--

INSERT INTO `equipos_perifericos` (`n_inventario`, `n_serie`, `tipo`, `clase`, `marca`, `ram`, `disco`, `procesador`, `estado`) VALUES
('PER01', 'NKSJDOA', 'MOUSE', 'PERIFERICO', 'LENOVO', '', '', '', 'asignado'),
('EQ03', 'NKSJDOA2', 'ESCRITORIO', 'EQUIPO', 'HP', '16', '512', 'INTEL', 'asignado'),
('PER03', 'DSFLSJOJÑ', 'PROYECTOR', 'PERIFERICO', 'EPSON', '', '', '', 'DISPONIBLE'),
('EQ04', 'KASDOISOJOI', 'ESCRITORIO', 'EQUIPO', 'LENOVO', '8', '256', 'INTEL', 'asignado'),
('PER04', 'SAKDLI', 'MOUSE', 'PERIFERICO', 'LOGITECH', '', '', '', 'asignado'),
('PER05', 'SAKDLI2', 'MOUSE', 'PERIFERICO', 'LENOVO', '', '', '', 'DISPONIBLE'),
('EQ05', 'ALKSDFSKJDL', 'ESCRITORIO', 'EQUIPO', 'ASUS', '12', '512', 'RYZEN', 'DISPONIBLE'),
('PER06', 'KJSDFISDH', 'MOUSE', 'PERIFERICO', 'SADJ', '', '', '', 'DISPONIBLE'),
('PER07', 'LKASDKJB', 'TECLADO', 'PERIFERICO', 'JHG', '', '', '', 'asignado'),
('EQ06', 'JDFHSKLJ534', 'LAPTOP', 'EQUIPO', 'LENOVO', '12GB', '512', 'INTEL CORE I5', 'asignado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `informes_generados`
--

CREATE TABLE `informes_generados` (
  `id` int(11) NOT NULL,
  `fecha_generacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `id_usuario_generador` int(11) NOT NULL,
  `estado_filtro` varchar(50) DEFAULT NULL,
  `reporte_json` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `informes_generados`
--

INSERT INTO `informes_generados` (`id`, `fecha_generacion`, `id_usuario_generador`, `estado_filtro`, `reporte_json`) VALUES
(1, '2025-07-05 21:19:39', 4, 'all', '[{\"n_inventario\":\"EQ05\",\"tipo\":\"ESCRITORIO\",\"estado\":\"asignado\",\"fechaAsignacion\":null,\"n_serie\":\"ALKSDFSKJDL\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"EQUIPO\",\"disco\":\"512\",\"procesador\":\"RYZEN\",\"clase\":\"EQUIPO\",\"marca\":\"ASUS\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"ALKSDFSKJDL\",\"id\":\"EQ05\",\"ram\":\"12\"},{\"n_inventario\":\"EQ03\",\"tipo\":\"ESCRITORIO\",\"estado\":\"PENDIENTE\",\"fechaAsignacion\":null,\"n_serie\":\"NKSJDOA2\",\"estadoAsignacion\":\"PENDIENTE\",\"categoria\":\"EQUIPO\",\"disco\":\"512\",\"procesador\":\"INTEL\",\"clase\":\"EQUIPO\",\"marca\":\"HP\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"NKSJDOA2\",\"id\":\"EQ03\",\"ram\":\"16\"},{\"n_inventario\":\"EQ04\",\"tipo\":\"ESCRITORIO\",\"estado\":\"asignado\",\"fechaAsignacion\":1750136400000,\"n_serie\":\"KASDOISOJOI\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"EQUIPO\",\"disco\":\"256\",\"procesador\":\"INTEL\",\"clase\":\"EQUIPO\",\"marca\":\"LENOVO\",\"asignadoA\":\"christian\",\"fechaDevolucion\":null,\"serial\":\"KASDOISOJOI\",\"id\":\"EQ04\",\"ram\":\"8\"},{\"n_inventario\":\"PER01\",\"tipo\":\"MOUSE\",\"estado\":\"ASIGNADO\",\"fechaAsignacion\":null,\"n_serie\":\"NKSJDOA\",\"estadoAsignacion\":\"ASIGNADO\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"LENOVO\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"NKSJDOA\",\"id\":\"PER01\",\"ram\":\"\"},{\"n_inventario\":\"PER05\",\"tipo\":\"MOUSE\",\"estado\":\"ASIGNADO\",\"fechaAsignacion\":null,\"n_serie\":\"SAKDLI2\",\"estadoAsignacion\":\"ASIGNADO\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"lenovo\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"SAKDLI2\",\"id\":\"PER05\",\"ram\":\"\"},{\"n_inventario\":\"PER04\",\"tipo\":\"MOUSE\",\"estado\":\"PENDIENTE\",\"fechaAsignacion\":null,\"n_serie\":\"SAKDLI\",\"estadoAsignacion\":\"PENDIENTE\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"LOGITECH\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"SAKDLI\",\"id\":\"PER04\",\"ram\":\"\"},{\"n_inventario\":\"PER06\",\"tipo\":\"MOUSE\",\"estado\":\"asignado\",\"fechaAsignacion\":null,\"n_serie\":\"KJSDFISDH\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"SADJ\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"KJSDFISDH\",\"id\":\"PER06\",\"ram\":\"\"},{\"n_inventario\":\"PER03\",\"tipo\":\"PROYECTOR\",\"estado\":\"asignado\",\"fechaAsignacion\":1750136400000,\"n_serie\":\"DSFLSJOJÑ\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"EPSON\",\"asignadoA\":\"christian\",\"fechaDevolucion\":null,\"serial\":\"DSFLSJOJÑ\",\"id\":\"PER03\",\"ram\":\"\"},{\"n_inventario\":\"PER07\",\"tipo\":\"TECLADO\",\"estado\":\"DISPONIBLE\",\"fechaAsignacion\":null,\"n_serie\":\"LKASDKJB\",\"estadoAsignacion\":\"DISPONIBLE\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"JHG\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"LKASDKJB\",\"id\":\"PER07\",\"ram\":\"\"}]'),
(2, '2025-07-05 21:20:23', 4, 'ASIGNADO', '[{\"n_inventario\":\"EQ05\",\"tipo\":\"ESCRITORIO\",\"estado\":\"asignado\",\"fechaAsignacion\":null,\"n_serie\":\"ALKSDFSKJDL\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"EQUIPO\",\"disco\":\"512\",\"procesador\":\"RYZEN\",\"clase\":\"EQUIPO\",\"marca\":\"ASUS\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"ALKSDFSKJDL\",\"id\":\"EQ05\",\"ram\":\"12\"},{\"n_inventario\":\"EQ04\",\"tipo\":\"ESCRITORIO\",\"estado\":\"asignado\",\"fechaAsignacion\":1750136400000,\"n_serie\":\"KASDOISOJOI\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"EQUIPO\",\"disco\":\"256\",\"procesador\":\"INTEL\",\"clase\":\"EQUIPO\",\"marca\":\"LENOVO\",\"asignadoA\":\"christian\",\"fechaDevolucion\":null,\"serial\":\"KASDOISOJOI\",\"id\":\"EQ04\",\"ram\":\"8\"},{\"n_inventario\":\"PER01\",\"tipo\":\"MOUSE\",\"estado\":\"ASIGNADO\",\"fechaAsignacion\":null,\"n_serie\":\"NKSJDOA\",\"estadoAsignacion\":\"ASIGNADO\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"LENOVO\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"NKSJDOA\",\"id\":\"PER01\",\"ram\":\"\"},{\"n_inventario\":\"PER05\",\"tipo\":\"MOUSE\",\"estado\":\"ASIGNADO\",\"fechaAsignacion\":null,\"n_serie\":\"SAKDLI2\",\"estadoAsignacion\":\"ASIGNADO\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"lenovo\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"SAKDLI2\",\"id\":\"PER05\",\"ram\":\"\"},{\"n_inventario\":\"PER06\",\"tipo\":\"MOUSE\",\"estado\":\"asignado\",\"fechaAsignacion\":null,\"n_serie\":\"KJSDFISDH\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"SADJ\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"KJSDFISDH\",\"id\":\"PER06\",\"ram\":\"\"},{\"n_inventario\":\"PER03\",\"tipo\":\"PROYECTOR\",\"estado\":\"asignado\",\"fechaAsignacion\":1750136400000,\"n_serie\":\"DSFLSJOJÑ\",\"estadoAsignacion\":\"asignado\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"EPSON\",\"asignadoA\":\"christian\",\"fechaDevolucion\":null,\"serial\":\"DSFLSJOJÑ\",\"id\":\"PER03\",\"ram\":\"\"}]'),
(3, '2025-07-05 21:22:37', 27, 'DISPONIBLE', '[{\"n_inventario\":\"PER07\",\"tipo\":\"TECLADO\",\"estado\":\"DISPONIBLE\",\"fechaAsignacion\":null,\"n_serie\":\"LKASDKJB\",\"estadoAsignacion\":\"DISPONIBLE\",\"categoria\":\"PERIFERICO\",\"disco\":\"\",\"procesador\":\"\",\"clase\":\"PERIFERICO\",\"marca\":\"JHG\",\"asignadoA\":\"N/A\",\"fechaDevolucion\":null,\"serial\":\"LKASDKJB\",\"id\":\"PER07\",\"ram\":\"\"}]');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `solicitudes_compra`
--

CREATE TABLE `solicitudes_compra` (
  `id` int(11) NOT NULL,
  `tipo_solicitud` varchar(255) NOT NULL,
  `descripcion` text NOT NULL,
  `alta_prioridad` tinyint(1) DEFAULT 0,
  `fecha_solicitud` datetime DEFAULT current_timestamp(),
  `id_usuario` int(11) DEFAULT NULL,
  `estado` varchar(50) DEFAULT 'Pendiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `solicitudes_compra`
--

INSERT INTO `solicitudes_compra` (`id`, `tipo_solicitud`, `descripcion`, `alta_prioridad`, `fecha_solicitud`, `id_usuario`, `estado`) VALUES
(1, 'Equipo', 'liowuefda\nTipo de Equipo: Laptop\nAlmacenamiento: 512GB SSD\nRAM: 8GB\nProcesador: AMD Ryzen 9', 1, '2025-07-04 21:16:59', 46, 'Pendiente'),
(2, 'Periferico', 'kuhik\nTipo Periférico: salida\nPeriférico Específico: Diademas', 1, '2025-07-04 21:17:39', 4, 'Pendiente'),
(3, 'Equipo/Periferico', 'iyihkj\nTipo de Equipo: Tablet\nAlmacenamiento: 256GB SSD\nRAM: 4GB\nProcesador: M1 Max\nTipo Periférico: salida\nPeriférico Específico: Monitor 19in a 24in', 0, '2025-07-04 22:22:05', 46, 'Pendiente'),
(4, 'Periferico', 'IUHKH\nTipo Periférico: salida\nPeriférico Específico: Parlantes', 0, '2025-07-04 22:34:23', 46, 'Pendiente'),
(5, 'Equipo', 'sfeaasd\nTipo de Equipo: CPU\nAlmacenamiento: 512GB SSD\nRAM: 16GB\nProcesador: M1 Ultra', 0, '2025-07-04 22:43:15', 46, 'Pendiente'),
(6, 'Periferico', 'llasdoaskpd\nTipo Periférico: entrada\nPeriférico Específico: Webcam', 1, '2025-07-05 15:15:41', 4, 'Pendiente'),
(7, 'Equipo', 'jksdhufi\nTipo de Equipo: CPU\nAlmacenamiento: 512GB SSD\nRAM: 8GB\nProcesador: M1 Pro', 0, '2025-07-05 15:16:26', 28, 'Pendiente'),
(8, 'Equipo', 'asdkjhisaksoail\nTipo de Equipo: CPU\nAlmacenamiento: 256GB SSD\nRAM: 4GB\nProcesador: Intel Core i3', 1, '2025-07-06 14:47:54', 46, 'Pendiente'),
(9, 'Equipo/Periferico', 'SE REQUIERE EQUIPO CON PROCESADOR GRAFICO \nTipo de Equipo: Computadora de escritorio\nAlmacenamiento: 512GB SSD\nRAM: 16GB\nProcesador: AMD Ryzen 3\nTipo Periférico: Entrada\nPeriférico Específico: Mouse', 1, '2025-11-03 17:25:53', 4, 'Pendiente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) NOT NULL,
  `cedula` varchar(100) NOT NULL,
  `rol` enum('admin','usuario','','') NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(250) NOT NULL,
  `token_recuperacion` varchar(100) DEFAULT NULL,
  `expiracion_token` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `cedula`, `rol`, `username`, `password`, `email`, `token_recuperacion`, `expiracion_token`) VALUES
(4, 'yuly caballero', '1007963690', 'usuario', 'yuly', '940726', 'yuly@gmail.com', NULL, NULL),
(16, 'valentina osorio', '1234567895', 'admin', 'valen', '123456', 'valentina@gmail.com', NULL, NULL),
(27, 'christian sanchez', '1110550665', 'admin', 'christian', '940726', 'an.sa.pa26@gmail.com', NULL, NULL),
(28, 'carlos melendez', '1564869869', 'usuario', 'carlos', '123456', 'carlos@gmail.com', NULL, NULL),
(32, 'chris', '6545494', 'admin', '6545494', '6545494', 'dsjkdgsk@josdi.com', NULL, NULL),
(34, 'christian', '1110550665', 'usuario', '1110550665', '1110550665', 'an.sa.pa@gmail.com', NULL, NULL),
(36, 'valentina osorio', '12346835', 'usuario', '12346835', '12346835', 'valen@gmail.com', NULL, NULL),
(40, 'valentina osorio', '68545688', 'admin', '68545688', '68545688', 'valentina@gmail.com', NULL, NULL),
(43, 'christian', '94072604843', 'admin', '94072604843', '94072604843', 'andres@gmail.com', NULL, NULL),
(44, 'carlos', '156845784', 'usuario', '156845784', '123456', 'carlos@gmail.com', NULL, NULL);
--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `actas`
--
ALTER TABLE `actas`
  ADD PRIMARY KEY (`id_acta`);

--
-- Indices de la tabla `empleados`
--
ALTER TABLE `empleados`
  ADD PRIMARY KEY (`id_empleado`),
  ADD UNIQUE KEY `cedula` (`cedula`);
ALTER TABLE `empleados` ADD FULLTEXT KEY `id_empleado` (`id_empleado`,`nombre`,`cedula`,`email`,`cargo`);

--
-- Indices de la tabla `equipos_perifericos`
--
ALTER TABLE `equipos_perifericos`
  ADD UNIQUE KEY `unique_n_inventario` (`n_inventario`),
  ADD UNIQUE KEY `unique_n_serie` (`n_serie`);
ALTER TABLE `equipos_perifericos` ADD FULLTEXT KEY `n_inventario` (`n_inventario`,`n_serie`,`tipo`,`clase`,`marca`,`ram`,`disco`,`procesador`,`estado`);
ALTER TABLE `equipos_perifericos` ADD FULLTEXT KEY `n_inventario_2` (`n_inventario`,`n_serie`,`tipo`,`clase`,`marca`,`ram`,`disco`,`procesador`,`estado`);

--
-- Indices de la tabla `informes_generados`
--
ALTER TABLE `informes_generados`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_usuario_generador` (`id_usuario_generador`);

--
-- Indices de la tabla `solicitudes_compra`
--
ALTER TABLE `solicitudes_compra`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `actas`
--
ALTER TABLE `actas`
  MODIFY `id_acta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `informes_generados`
--
ALTER TABLE `informes_generados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `solicitudes_compra`
--
ALTER TABLE `solicitudes_compra`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `informes_generados`
--
ALTER TABLE `informes_generados`
  ADD CONSTRAINT `informes_generados_ibfk_1` FOREIGN KEY (`id_usuario_generador`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;