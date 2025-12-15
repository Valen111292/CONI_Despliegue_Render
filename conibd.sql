-- DROP SCHEMA public;

CREATE SCHEMA public AUTHORIZATION coni_user;

COMMENT ON SCHEMA public IS 'standard public schema';

-- DROP SEQUENCE public.actas_id_acta_seq;

CREATE SEQUENCE public.actas_id_acta_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.informes_generados_id_seq;

CREATE SEQUENCE public.informes_generados_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.solicitudes_compra_id_seq;

CREATE SEQUENCE public.solicitudes_compra_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;
-- DROP SEQUENCE public.usuarios_id_seq;

CREATE SEQUENCE public.usuarios_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;-- public.actas definition

-- Drop table

-- DROP TABLE public.actas;

CREATE TABLE public.actas (
	id_acta serial4 NOT NULL,
	nombre_completo varchar(100) NOT NULL,
	cedula varchar(20) NOT NULL,
	n_inventario varchar(255) NOT NULL,
	fecha date DEFAULT CURRENT_DATE NULL,
	ruta_pdf varchar(255) NOT NULL,
	CONSTRAINT actas_pkey PRIMARY KEY (id_acta)
);


-- public.empleados definition

-- Drop table

-- DROP TABLE public.empleados;

CREATE TABLE public.empleados (
	id_empleado varchar(10) NOT NULL,
	nombre varchar(100) NOT NULL,
	cedula varchar(15) NOT NULL,
	email varchar(100) NOT NULL,
	cargo varchar(50) NOT NULL,
	fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT empleados_cedula_key UNIQUE (cedula),
	CONSTRAINT empleados_pkey PRIMARY KEY (id_empleado)
);


-- public.equipos_perifericos definition

-- Drop table

-- DROP TABLE public.equipos_perifericos;

CREATE TABLE public.equipos_perifericos (
	n_inventario varchar(50) NOT NULL,
	n_serie varchar(100) NULL,
	tipo varchar(100) NULL,
	clase varchar(50) NULL,
	marca varchar(100) NULL,
	ram varchar(50) NULL,
	disco varchar(50) NULL,
	procesador varchar(100) NULL,
	estado varchar(50) NULL,
	CONSTRAINT equipos_perifericos_n_serie_key UNIQUE (n_serie),
	CONSTRAINT equipos_perifericos_pkey PRIMARY KEY (n_inventario)
);


-- public.usuarios definition

-- Drop table

-- DROP TABLE public.usuarios;

CREATE TABLE public.usuarios (
	id serial4 NOT NULL,
	nombre varchar(200) NOT NULL,
	cedula varchar(100) NOT NULL,
	rol varchar(20) NOT NULL,
	username varchar(50) NOT NULL,
	"password" varchar(100) NOT NULL,
	email varchar(250) NOT NULL,
	token_recuperacion varchar(100) NULL,
	expiracion_token timestamp NULL,
	CONSTRAINT usuarios_pkey PRIMARY KEY (id)
);


-- public.informes_generados definition

-- Drop table

-- DROP TABLE public.informes_generados;

CREATE TABLE public.informes_generados (
	id serial4 NOT NULL,
	fecha_generacion timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	id_usuario_generador int4 NOT NULL,
	estado_filtro varchar(50) NULL,
	reporte_json text NOT NULL,
	CONSTRAINT informes_generados_pkey PRIMARY KEY (id),
	CONSTRAINT fk_informe_usuario FOREIGN KEY (id_usuario_generador) REFERENCES public.usuarios(id) ON DELETE CASCADE
);


-- public.solicitudes_compra definition

-- Drop table

-- DROP TABLE public.solicitudes_compra;

CREATE TABLE public.solicitudes_compra (
	id serial4 NOT NULL,
	tipo_solicitud varchar(255) NOT NULL,
	descripcion text NOT NULL,
	alta_prioridad bool DEFAULT false NULL,
	fecha_solicitud timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	id_usuario int4 NULL,
	estado varchar(50) DEFAULT 'Pendiente'::character varying NULL,
	CONSTRAINT solicitudes_compra_pkey PRIMARY KEY (id),
	CONSTRAINT fk_solicitud_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuarios(id) ON DELETE SET NULL
);