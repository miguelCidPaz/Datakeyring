# 🔐 DataKeyring – Llavero de formatos para ingesta batch

**DataKeyring** es una utilidad modular para la lectura por lotes de archivos en los siguientes formatos tabulares:

- **CSV**
- **JSON**
- **JSONL**
- **Parquet**

> 🧪 Soporte experimental para archivos NetCDF (`.nc`) en desarrollo.

Está pensado para usarse en sistemas de procesamiento batch donde necesitas leer un número específico de filas por bloque, sin preocuparte por el formato subyacente.

---

## 🚀 Características

- ✅ Lectura plug & play de los 4 formatos principales.
- ✅ Soporte por lotes: puedes indicar cuántas filas deseas en cada iteración.
- ✅ Implementación ligera, sin dependencias innecesarias.
- ✅ Diseñado para desacoplar la lógica de lectura de los motores principales.
- ✅ Basado en **Jackson** (JSON, JSONL) y **Apache Hadoop** (Parquet).
- ✅ Totalmente integrable en `DataSlice`, orquestadores o motores.

---

## 🧪 Tests

Este proyecto incluye tests unitarios para cada uno de los formatos soportados.

- Los tests esperan encontrar un archivo de prueba con nombre `testdata` y la extensión correspondiente dentro de la carpeta:
  ```
  src/test/resources
  ```

- Por ejemplo:
  - `src/test/resources/testdata.csv`
  - `src/test/resources/testdata.json`
  - `src/test/resources/testdata.jsonl`
  - `src/test/resources/testdata.parquet`

---

## 📦 Uso en Maven

Para usar este paquete desde **GitHub Packages**, añade lo siguiente a tu `pom.xml`:

### ① Añade el repositorio

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub Packages</name>
    <url>https://maven.pkg.github.com/miguelCidPaz/Datakeyring</url>
  </repository>
</repositories>
```

### ② Añade la dependencia

```xml
<dependency>
  <groupId>io.github.miguelCidPaz</groupId>
  <artifactId>datakeyring</artifactId>
  <version>0.0.2</version>
</dependency>
```

Asegúrate de haber configurado la autenticación en tu archivo `~/.m2/settings.xml` como se explica [aquí](https://docs.github.com/es/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).

---

## 🧱 Estructura

```
src/
├── main/
│   └── java/com/tuempresa/datakeyring/
│       ├── CsvReader.java
│       ├── JsonReader.java
│       ├── JsonlReader.java
│       └── ParquetReader.java
└── test/
    └── java/com/tuempresa/datakeyring/
        ├── CsvReaderTest.java
        ├── JsonReaderTest.java
        ├── JsonlReaderTest.java
        └── ParquetReaderTest.java
    └── resources/
        └── testdata.csv
        └── testdata.json
        └── testdata.jsonl
        └── testdata.parquet
```

---

## 🔧 En desarrollo

- Lectura de archivos `.nc` (NetCDF).
- Módulo de escritura por lotes.
- Particionado y metadatos adicionales para análisis estructurado.

---

## 📜 Licencia

Distribuido bajo licencia **Apache 2.0**.  
Puedes usarlo libremente, modificarlo y distribuirlo, siempre que mantengas la atribución correspondiente.

---

## ✍️ Autor

Desarrollado por [Miguel Cid](https://github.com/miguelCidPaz)
