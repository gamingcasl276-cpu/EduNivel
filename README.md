# EduNivel — Proyecto base Spring Boot

Este proyecto integra la interfaz gamificada existente con una base inicial de backend Java/Spring Boot y el modelo relacional descrito en el documento del proyecto.

## Requisitos
- JDK 21 o superior compatible con Spring Boot 3.5.x.
- MySQL Server 8.x (o una versión compatible).
- Visual Studio Code con Extension Pack for Java y soporte Maven, o IntelliJ IDEA.
- Maven Wrapper no está incluido; Maven instalado o usar Maven desde VS Code.

## Estructura
- `src/main/java/bo/edu/edunivel`: aplicación y código Java.
- `model`: entidades Estudiante, Evaluacion, Encuesta, Pregunta y Respuesta.
- `repository`: interfaces Spring Data JPA.
- `controller`: ruta pública principal y comprobación de estado.
- `src/main/resources/templates/index.html`: interfaz EduNivel.
- `src/main/resources/static`: CSS y JavaScript de EduNivel.

## Preparar MySQL
1. Abre MySQL Workbench o phpMyAdmin.
2. Ejecuta:
   ```sql
   CREATE DATABASE edunivel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Revisa `src/main/resources/application.properties`.
4. Si tu usuario MySQL no es `root` o tiene contraseña, ajusta `DB_USERNAME` y `DB_PASSWORD` en las variables de entorno, o modifica el archivo localmente. No publiques contraseñas.

## Ejecutar
Desde la carpeta que contiene `pom.xml`, abre una terminal y ejecuta:
```bash
mvn spring-boot:run
```
Luego abre:
- Página: http://localhost:8081/
- Estado: http://localhost:8081/health

También puedes ejecutar `EduNivelApplication.java` desde VS Code si tienes configurado el soporte Java.

## Importante: alcance de esta base
Esta entrega es una base ejecutable para continuar el desarrollo; no es todavía una plataforma lista para publicar con datos reales.
- La página pública se sirve desde Spring Boot.
- Las entidades JPA representan las cinco tablas del modelo: ESTUDIANTE, EVALUACIÓN, ENCUESTA, PREGUNTA y RESPUESTA.
- Spring JPA puede crear/actualizar las tablas localmente al iniciar.
- Todavía no incluye autenticación, autorización, formularios conectados, importación de datos, reportes consolidados ni persistencia del progreso de juegos.
- No se deben cargar nombres, calificaciones ni encuestas reales hasta implementar inicio de sesión, permisos, controles de privacidad y revisar las autorizaciones institucionales.
- Los niveles implementan provisionalmente la regla del documento: ALTO 80–100, MEDIO 60–79 y BAJO 0–59, y el promedio se calcula cuando existen las cuatro áreas. El documento también contiene tablas consolidadas con tres áreas y estados de aprobación distintos; esa diferencia debe validarse con el equipo antes de usar resultados oficiales.

## Próximas etapas
1. Ejecutar esta base y comprobar MySQL.
2. Construir login y roles con contraseñas cifradas.
3. Conectar formularios de evaluación y encuesta.
4. Crear reportes estadísticos grupales y gráficos.
5. Probar seguridad y publicar backend y base de datos con almacenamiento persistente.
