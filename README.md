# Sellify API 🚀

Backend oficial de la plataforma e-commerce Sellify. Desarrollado con una arquitectura de **Monolito Modular**, diseñado para ser escalable, eficiente y con soporte nativo para entornos Multi-empresa (Multi-tenant).

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 25
* **Framework:** Spring Boot 4.0.5
* **Base de Datos:** PostgreSQL 18
* **Seguridad:** Spring Security 6 + JWT
* **Persistencia:** Spring Data JPA
* **Gestor de Dependencias:** Maven

## 🏗️ Estructura del Proyecto (Arquitectura Modular)

El código está organizado por dominios de negocio que corresponden a los esquemas de tu base de datos (`core`, `people`, `auth`). El paquete raíz es `com.sellify.api`.

```text
src/main/java/com/sellify/api/
├── SellifyApiApplication.java
│
├── common/                 # Componentes transversales
│   ├── auditing/           # Auditoría JPA (created_at, updated_at)
│   ├── config/             # Configuración de CORS, Swagger, etc.
│   └── exception/          # Manejador global de errores
│
├── security/               # Capa de Seguridad Global
│   ├── config/             # SecurityFilterChain
│   ├── context/            # Gestión del contexto del usuario/tenant
│   └── jwt/                # Filtros y utilidades de JWT
│
└── modules/                # Módulos de Negocio (Esquemas)
    ├── auth/               # Gestión de usuarios, roles y sesiones
    │   ├── controller/
    │   ├── domain/
    │   ├── repository/
    │   └── service/
    │
    ├── core/               # Tablas Maestras, Empresas y Sucursales
    │   ├── controller/
    │   ├── domain/
    │   ├── repository/
    │   └── service/
    │
    └── people/             # Identidades, Empleados, Clientes y Proveedores
        ├── controller/
        ├── domain/
        ├── repository/
        └── service/
```

## ⚙️ Configuración del Entorno

El proyecto utiliza el archivo `src/main/resources/application.properties`. Asegúrate de configurar las siguientes propiedades antes de iniciar:

```properties
# Configuración de la Base de Datos
spring.datasource.url=jdbc:postgresql://localhost:5432/sellify_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuración de JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Configuración de Seguridad (JWT)
jwt.secret=tu_clave_secreta_muy_larga_en_base64
jwt.expiration=86400000
```

## 🚀 Instalación y Ejecución

1. Base de Datos: Crea una base de datos en PostgreSQL 18 llamada sellify_db.
2. Scripts: Coloca tu script inicial en src/main/resources/db/migration/ si usas Flyway.
3. Compilar:

```bash
mvn clean install
```

4. Ejecutar:

```bash
mvn spring-boot:run
```

## 🛣️ Convención de Rutas API

Todos los endpoints siguen la estructura de prefijos por módulo para facilitar el enrutamiento desde Angular 21:

1. Autenticación: `GET/POST /api/v1/auth/**`
2. Negocio/Maestras: `GET/POST /api/v1/core/**`
3. Personas/Entidades: `GET/POST /api/v1/people/**`