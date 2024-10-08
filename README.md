# PROYECTO MISCUENTAS -version App movil-

## Descripción

Bienvenido a la aplicación MISCUENTAS, una app móvil desarrollada en Kotlin que permite a los usuarios gestionar sus gastos compartidos de manera fácil y eficiente. 
La aplicación incluye funcionalidades como el registro de usuarios, la creación de hojas de cálculo para gastos, el manejo de participantes y la captura de imágenes como recibos.
Los gastos y pagos registrados pueden tener una imagen adjunta como comprobante. 
Los usuarios pueden ser locales (gestionados manualmente por cada usuario) o en línea (compartiendo una hoja de gastos con otros usuarios en la nube).

## Tecnologías Utilizadas

- **Kotlin:** Lenguaje principal de desarrollo para la app móvil.
- **Jetpack Compose:** Para diseño de interfaces de usuario modernas y reactivas.
- **Room:** Biblioteca de persistencia para almacenar datos localmente en una base de datos SQLite.
- **Hilt:** Para la inyección de dependencias y simplificaar la estructura del código.
- **Ktor:** API REST backend para gestionar la comunicción del servidor.
- **Android Jetpack:** Múltiples componentes de Android Jetpack, como Navigation, ViewModel, LiveData y DataStore.

## Características

1. Registro de Usuarios
Permite a los usuarios registrarse en la aplicación proporcionando su nombre, correo electrónico y contraseña. Los registros se guardan en la base de datos local utilizando Room.

2. Hojas de Cálculo para Gastos
Los usuarios pueden crear hojas de cálculo para gestionar sus gastos. Cada hoja incluye:
- **Participantes:** Los participantes en cada hoja están asociados al registro principal y pueden agregar gastos.
- **Gastos:** Los participantes pueden registrar sus gastos, con detalles como concepto, tipo, importe y fecha.

3. Balance y Deudas
La aplicación calcula automáticamente el balance de cada participante para conocer cómo se deben dividir los gastos y quién tiene deudas pendientes. Cada deuda es calculada en función del total de gastos y se registran las futuras liquidaciones.

4. Captura de Fotografías y Almacenamiento
Se proporciona una funcionalidad para tomar fotos (ej. recibos o comprobantes de gasto) y almacenarlas en la galería del dispositivo o en la base de datos local para referencia futura. Utilizamos FileProvider para la captura de imágenes y almacenaje en el dispositivo.

5. Navegación en la Aplicación
La navegación está gestionada a través de Jetpack Navigation, proporcionando transiciones suaves entre diferentes pantallas como:
- **Splash Screen:** Pantalla inicial de bienvenida.
- **Inicio:** Menú con opciones como crear nuevas hojas, acceder a gastos y ver balances.
- **Mis Hojas:** Para ver todas las hojas de cálculo creadas y administrarlas.
- **Participantes:** Mostrar los participantes de cada hoja de cálculo.

5. Mas caracteristicas
- **Avisos de Pago**: Envía recordatorios de pago o solicitudes de pago a otros usuarios.
- **Comprobantes Adjuntos**: Permite adjuntar imágenes a los gastos o pagos como comprobante.
- **Usuarios Locales y en Línea**: Gestiona usuarios locales o compárte hojas de gastos con otros usuarios.
- **Inicio de Sesión con Huella**: Inicia sesión de manera rápida y segura usando la huella digital.
- **Compartir Capturas y Enlaces**: Comparte capturas de pantalla de las hojas de gastos o el enlace de descarga de la aplicación.
- **Contacto con el Desarrollador**: Fácil acceso para contactar con el desarrollador y enviar sugerencias.
- **Calificar la App**: Permite calificar la aplicación para mejorar la experiencia de usuario.

## Arquitectura de la Aplicación

La arquitectura sigue el patrón MVVM (Model-View-ViewModel) utilizando las siguientes capas:

- **Model:** Define los datos principales de la aplicación, incluyendo Registro, HojaCalculo, Participante, Gasto, Deuda y Pago. Estos modelos se sincronizan con la base de datos Room.
- **ViewModel:** Gestiona la lógica de negocio y proporciona los datos requeridos por las vistas.
- **View:** Implementada con Jetpack Compose. Incluye varios elementos visuales como botones personalizados, LazyColumn para listas de gastos y diálogos para confirmar acciones.

## Ejemplo de Modelos

A continuación se muestra un ejemplo de los modelos utilizados en la aplicación:
   ```bash
   @Serializable
   data class HojaCalculo(
       val idHoja: Long,
       val titulo: String,
       val fechaCreacion: String?,
       val fechaCierre: String?,
       val limite: String?,
       val status: String,
       val idRegistroHoja: Long?
   )

   @Serializable
   data class Gasto(
       val idGasto: Long,
       val tipo: Long,
       val concepto: String,
       val importe: Double,
       val fechaGasto: String?,
       val idParticipanteGasto: Long?
   )
   ```

## Requisitos de Permisos

Para utilizar la cámara y guardar imágenes, la aplicación solicita los siguientes permisos:
- **Cámara:** Para tomar fotos.
- **Almacenamiento:** Para guardar imágenes en la galería del dispositivo.

La solición de permisos se gestiona utilizando rememberMultiplePermissionsState y depende de la versión de Android, diferenciando entre Android Tiramisu y versiones anteriores.

## Tecnologías Utilizadas

- **Lenguaje de Programación**: Kotlin
- **Plataforma**: Android
- **Backend API**: Ktor (API REST)
- **Base de Datos**: MySQL


## Notas Sobre el Desarrollo

- La aplicación se apoya en ViewModel para gestionar los estados de la interfaz de usuario y facilitar el acceso a la base de datos.
- Las imágenes capturadas se guardan en el directorio de DCIM/Camera y se referencian mediante su URI en la base de datos local.
- Para mejorar la experiencia del usuario, se implementa un menú lateral solo disponible en la pantalla de Inicio, donde los usuarios pueden acceder fácilmente a las distintas funcionalidades.

## Instalación

1. Clona este repositorio:
   ```bash
   git clone git@github.com:LeoCare/MisCuentas.git
   ```
2. Abre el proyecto en Android Studio.
3. Configura las dependencias y la base de datos MySQL.
4. Compila y ejecuta la aplicación en un dispositivo o emulador Android.

## Uso

1. Regístrate o inicia sesión con tu huella digital.
2. Crea una nueva hoja de gastos y añade participantes (locales o en línea).
3. Introduce los gastos, adjunta comprobantes si es necesario.
4. Finaliza la hoja de gastos para ver el balance y las cantidades a pagar.
5. Envía avisos de pago a los participantes para saldar las deudas.

## Capturas de Pantalla

(Agregar aquí capturas de pantalla de la aplicación para ilustrar las diferentes funciones)

## Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir:
- Realiza un fork del repositorio.
- Crea una nueva rama con tus cambios.
- Envía un Pull Request.

## Contacto

Para preguntas, sugerencias o problemas, no dudes en contactarme:

- Email: desarrollador@example.com

## Licencia

Este proyecto está bajo la licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
