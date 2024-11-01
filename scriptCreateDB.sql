DROP DATABASE IF EXISTS [DBMisCuentas]
CREATE DATABASE [DBMisCuentas]
GO
USE [DBMisCuentas]
GO
/****** Object:  Database [DBMisCuentas]    Script Date: 17/02/2024 20:30:24 ******/
GO
CREATE SCHEMA app;
GO
/****** Object:  Table [app].[t_registros]    Script Date: 17/02/2024 20:30:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [app].[t_registros](
	[id_registro] [int] IDENTITY(1,1) NOT NULL,
	[nombre] [varchar](100) NOT NULL,
	[correo] [varchar](200) NOT NULL,
	[contrasenna] [nvarchar](max) NOT NULL,
 CONSTRAINT [PK_registros] PRIMARY KEY CLUSTERED 
(
	[id_registro] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [app].[t_participantes]    Script Date: 17/02/2024 20:30:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [app].[t_participantes](
	[id_participante] [int] IDENTITY(1,1) NOT NULL,
	[nombre] [varchar](100) NOT NULL,
	[correo] [varchar](200) NULL,
 CONSTRAINT [PK_participantes] PRIMARY KEY CLUSTERED 
(
	[nombre] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [app].[t_hojas_cab]    Script Date: 17/02/2024 20:30:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [app].[t_hojas_cab](
	[id_hoja] [int] IDENTITY(1,1) NOT NULL,
	[titulo] [varchar](100) NOT NULL,
	[fecha_cierre] [date] NULL,
	[limite] [int] NULL,
	[status] [char] NOT NULL,
 CONSTRAINT [PK_hojas_cab] PRIMARY KEY CLUSTERED 
(
	[id_hoja] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [app].[t_hojas_lin]    Script Date: 17/02/2024 20:30:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [app].[t_hojas_lin](
	[id_hoja] [int] NOT NULL,
	[linea] [int] NOT NULL,
	[id_participante] [int] NOT NULL,
	[status_linea] [char] NOT NULL,
 CONSTRAINT [PK_t_hojas_lin] PRIMARY KEY CLUSTERED 
(
	[id_hoja] ASC,
	[linea] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [app].[t_hojas_lin_det]    Script Date: 17/02/2024 20:30:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [app].[t_hojas_lin_det](
	[id_hoja] [int] NOT NULL,
	[linea] [int] NOT NULL,
	[linea_detalle] [int] NOT NULL,
	[id_gasto] [int] NOT NULL,
	[concepto] [varchar](100) NOT NULL,
	[importe] [int] NOT NULL,
 CONSTRAINT [PK_hojas_lin_det] PRIMARY KEY CLUSTERED 
(
	[id_hoja] ASC,
	[linea] ASC,
	[linea_detalle] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [app].[t_gastos]    Script Date: 17/02/2024 20:30:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [app].[t_gastos](
	[id_gasto] [int]IDENTITY(1,1) NOT NULL,
	[nombre] [nvarchar](100) NOT NULL,
 CONSTRAINT [PK_gastos] PRIMARY KEY CLUSTERED 
(
	[id_gasto] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** Insert    Script Date: 17/02/2024 20:30:24 ******/

SET IDENTITY_INSERT [app].[t_registros] ON 
INSERT [app].[t_registros] ([id_registro], [nombre], [correo], [contrasenna]) VALUES (0, 'Leo', 'leon1982care@gmail.com', '111nonamaEM')
SET IDENTITY_INSERT [dbo].[t_registros] OFF

SET IDENTITY_INSERT [app].[t_gastos] ON 
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (0, 'Varios')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (1, 'Supermercado')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (2, 'Recibo')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (3, 'Regalo')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (4, 'Cine')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (5, 'Coche')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (6, 'Ropa')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (7, 'Telefono')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (8, 'Vacaciones')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (9, 'Viajes')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (10, 'Restaurante')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (6, 'Hogar')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (7, 'Credito')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (8, 'Tecnologia')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (9, 'Wifi')
INSERT [app].[t_gastos] ([id_gasto], [Nombre], [nombre]) VALUES (10, 'Mascotas')
SET IDENTITY_INSERT [dbo].[t_gastos] OFF

/****** Alter    Script Date: 17/02/2024 20:30:24 ******/

ALTER TABLE [app].[t_hojas_lin]  WITH CHECK ADD  CONSTRAINT [FK_hojas_lin] FOREIGN KEY([id_hoja])
REFERENCES [app].[t_hojas_cab] ([id_hoja])
GO
ALTER TABLE [app].[t_hojas_lin] CHECK CONSTRAINT [FK_hojas_lin]
GO

ALTER TABLE [app].[t_hojas_lin]  WITH CHECK ADD  CONSTRAINT [FK_hojas_lin_participantes] FOREIGN KEY([id_participante])
REFERENCES [app].[t_participantes] ([id_participante])
GO
ALTER TABLE [app].[t_hojas_lin] CHECK CONSTRAINT [FK_hojas_lin_participantes]
GO

ALTER TABLE [app].[t_hojas_lin_det]  WITH CHECK ADD  CONSTRAINT [FK_hojas_lin_det_id] FOREIGN KEY([id_hoja])
REFERENCES [app].[t_hojas_lin] ([id_hoja])
GO
ALTER TABLE [app].[t_hojas_lin_det] CHECK CONSTRAINT [FK_hojas_lin_det_id]
GO

ALTER TABLE [app].[t_hojas_lin_det]  WITH CHECK ADD  CONSTRAINT [FK_hojas_lin_linea] FOREIGN KEY([linea])
REFERENCES [app].[t_hojas_lin] ([linea])
GO
ALTER TABLE [app].[t_hojas_lin_det] CHECK CONSTRAINT [FK_hojas_lin_linea]
GO

ALTER TABLE [app].[t_hojas_lin_det]  WITH CHECK ADD  CONSTRAINT [FK_hojas_lin_det_gasto] FOREIGN KEY([id_gasto])
REFERENCES [app].[t_gastos] ([id_gasto])
GO
ALTER TABLE [app].[t_hojas_lin_det] CHECK CONSTRAINT [FK_hojas_lin_det_gasto]
GO


/********** Alter Database  ***********/
GO
USE [master]
GO
ALTER DATABASE [BDMontecastelo] SET  READ_WRITE 
GO
