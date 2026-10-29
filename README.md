# RutaExpress — Microservicio de envíos

`ms-envios` es un microservicio REST desarrollado con Java 17 y Spring Boot 3. Permite consultar la configuración activa del servicio y calcular cotizaciones de envío. El proyecto también demuestra cómo separar la configuración de la aplicación mediante archivos YAML externos y cómo suministrar un token usando un Secret de Kubernetes.

## Funcionalidad

- `GET /api/envios/config`: devuelve moneda, tarifas, umbral de envío gratis, peso máximo e indica si el token está configurado. No devuelve el valor del token.
- `POST /api/envios/cotizar`: calcula el costo según peso, monto de compra y modalidad normal o express. Valida el rango de peso y responde `400 Bad Request` si no es válido.

La configuración predeterminada está en `src/main/resources/application.yml`. Para la ejecución local, `config/application.yml` la complementa con los valores del entorno y simula el ConfigMap que Kubernetes montaría en `/config`. Los manifiestos de Kubernetes están en `k8s/`.

## Requisitos

- JDK 17
- Maven 3.6 o posterior
- Conexión a Internet la primera vez para descargar dependencias
- Postman (opcional, para probar los endpoints)

No se necesita Kubernetes para ejecutar ni validar el servicio localmente.

## Ejecutar localmente

1. Abre en IntelliJ IDEA la carpeta del proyecto que contiene `pom.xml`, o navega a esa carpeta desde una terminal.
2. Desde la raíz del proyecto, inicia el servicio:

   **Windows PowerShell**
   ```powershell
   .\run-local.ps1
   ```

   **Linux/macOS**
   ```bash
   ./run-local.sh
   ```

   Los scripts establecen `COURIER_API_TOKEN` para simular el Secret y pasan `config/` como ubicación adicional de configuración para simular el ConfigMap.
3. Espera a que Spring Boot indique que la aplicación inició. El servicio estará disponible en `http://localhost:8080`.
4. Para detenerlo, pulsa `Ctrl+C` en la terminal.

También puedes iniciar el servicio con Maven desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

Para que esta ejecución use la configuración externa y tenga el token configurado, establece esas opciones antes de arrancar; los scripts `run-local` ya lo hacen.

## Probar los endpoints

Puedes importar `ms-envios.postman_collection.json` en Postman o usar las peticiones de `peticiones.http`.

Ejemplos:

```http
GET http://localhost:8080/api/envios/config
```

```http
POST http://localhost:8080/api/envios/cotizar
Content-Type: application/json

{
  "pesoKg": 4,
  "montoCompra": 100,
  "express": false
}
```

Con la configuración externa del proyecto, esa cotización normal cuesta `18.00 PEN`. Si se solicita como express, cuesta `23.40 PEN`; una compra de `150 PEN` o más tiene envío normal gratis. Los pesos mayores a `30 kg` o menores o iguales a cero producen `400 Bad Request`.

## Validar el taller

Ejecuta `src/main/java/com/nttdata/envios/validacion/Validador.java` desde IntelliJ IDEA (botón **Run** en la clase). El validador comprueba la configuración, los manifiestos, el tratamiento del token y las reglas de cotización.

## Seguridad

El token de los scripts y manifiestos es un valor de prueba para el taller. No reutilices credenciales reales ni guardes secretos en el repositorio. En un entorno real, suministra el token mediante un gestor de secretos apropiado.
