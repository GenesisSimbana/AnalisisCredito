# Motor de Análisis Crediticio Automotriz

## Descripción

Sistema de evaluación automática de solicitudes de crédito automotriz que cumple con las reglas de negocio bancarias para minimizar el riesgo crediticio.

## Arquitectura

El proyecto sigue una arquitectura de microservicios con Spring Boot, implementando los siguientes módulos funcionales:

### 🔎 Módulo 1: Consulta Buró
- Consulta automática al buró externo solo para solicitudes en estado "Pendiente de evaluación"
- Almacenamiento de score externo, cuentas activas/morosas y días promedio de mora
- Manejo de reintentos en caso de errores temporales

### 📄 Módulo 2: Informe del Buró
- Guardado del informe JSON completo para trazabilidad
- Extracción de datos clave: monto total adeudado y número de deudas impagas
- Validación del formato del informe

### ⚙️ Módulo 3: Evaluación Interna
- Cálculo de capacidad de pago (30% del ingreso neto)
- Aplicación de reglas de negocio:
  - Score > 750 y sin moras → Aprobado automático
  - Score 600-750 → Revisión manual
  - Score < 600 o con moras → Rechazado automático
- Almacenamiento de resultados y observaciones

### 👤 Módulo 4: Revisión del Analista
- Modificación manual de decisiones automáticas
- Justificación obligatoria de cambios
- Trazabilidad completa de decisiones

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Cloud OpenFeign**
- **Lombok**
- **MapStruct**
- **OpenAPI 3 (Swagger)**
- **H2 Database** (desarrollo)
- **SLF4J** para logging

## Estructura del Proyecto

```
src/main/java/com/banquito/analisis/
├── controller/
│   ├── dto/                    # DTOs con validaciones OpenAPI
│   ├── mapper/                 # Mappers MapStruct
│   └── CreditRequestController # Controlador REST
├── exception/                  # Excepciones personalizadas
├── model/                      # Entidades JPA
├── repository/                 # Repositorios JPA
├── service/                    # Lógica de negocio
├── client/                     # Cliente Feign para servicios externos
├── config/                     # Configuraciones (CORS, OpenAPI)
└── AnalisisApplication.java    # Clase principal
```

## Endpoints de la API

### Solicitudes de Crédito

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/v1/credit-requests` | Crear nueva solicitud |
| GET | `/v1/credit-requests` | Obtener todas las solicitudes |
| GET | `/v1/credit-requests/{id}` | Obtener solicitud específica |
| POST | `/v1/credit-requests/{id}/bureau-consult` | Consultar buró de crédito |
| POST | `/v1/credit-requests/{id}/evaluate` | Evaluar solicitud automáticamente |
| PATCH | `/v1/credit-requests/{id}/analyst-decision` | Actualizar decisión del analista |
| PATCH | `/v1/credit-requests/{id}/update-expenses` | Actualizar egresos post-desembolso |
| DELETE | `/v1/credit-requests/{id}` | Eliminar solicitud (soft delete) |

## Configuración

### Propiedades Principales

```properties
# Base de datos
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# Servicio de buró
bureau.service.url=http://localhost:8081
bureau.service.authorization=Bearer your-api-key

# OpenAPI
springdoc.swagger-ui.path=/swagger-ui.html
```

## Instalación y Ejecución

### Prerrequisitos
- Java 17 o superior
- Maven 3.6+

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd analisis
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

4. **Acceder a la documentación**
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - H2 Console: http://localhost:8080/api/h2-console

## Reglas de Negocio

### Capacidad de Pago
- Se calcula como el 30% del ingreso neto (ingresos - egresos)
- Si la capacidad de pago es menor que la cuota mensual, la solicitud se rechaza automáticamente

### Evaluación por Score
- **Score > 750 y sin cuentas morosas**: Aprobado automático (Riesgo Bajo)
- **Score 600-750**: Revisión manual (Riesgo Medio)
- **Score < 600 o con cuentas morosas**: Rechazado automático (Riesgo Alto)

### Estados de Solicitud
- `PENDIENTE_EVALUACION`: Solicitud recién creada
- `EN_REVISION`: Requiere revisión manual del analista
- `APROBADO`: Solicitud aprobada
- `RECHAZADO`: Solicitud rechazada

## Integración con Servicios Externos

### Buró de Crédito
- Endpoint: `/v1/bureau/consult/{identificacion}`
- Headers requeridos: `Authorization`, `X-Request-ID`
- Respuesta incluye: score, cuentas activas/morosas, monto adeudado, etc.

### Originación
- Recibe datos de solicitud: ingresos, egresos, cuota mensual
- Devuelve resultado: Aprobado, Rechazado o En Revisión
- Actualización post-desembolso para recalcular capacidad de pago

## Buenas Prácticas Implementadas

### Arquitectura
- ✅ Separación de responsabilidades (Controller, Service, Repository)
- ✅ Uso de DTOs para transferencia de datos
- ✅ Mapeo automático con MapStruct
- ✅ Excepciones personalizadas
- ✅ Soft delete para auditoría

### API REST
- ✅ Nombres de recursos en plural
- ✅ Versionamiento de API
- ✅ Idempotencia con requestId
- ✅ Paginación y filtros
- ✅ Documentación OpenAPI
- ✅ Validaciones con Bean Validation

### Seguridad y Logging
- ✅ Logs estructurados con SLF4J
- ✅ Configuración de CORS
- ✅ Manejo de errores centralizado
- ✅ Trazabilidad completa

## Monitoreo y Métricas

- **Actuator**: Endpoints de salud y métricas
- **Logs**: Trazabilidad completa de operaciones
- **H2 Console**: Inspección de datos en desarrollo

## Próximos Pasos

1. **Base de datos de producción**: Migrar de H2 a PostgreSQL/MySQL
2. **Autenticación**: Implementar JWT o OAuth2
3. **Cache**: Agregar Redis para consultas frecuentes
4. **Mensajería**: Integrar con RabbitMQ/Kafka
5. **Tests**: Agregar tests unitarios e integración
6. **Docker**: Containerización de la aplicación

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles. 