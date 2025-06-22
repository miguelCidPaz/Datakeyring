
# 🔐 DataKeyring – Format keyring for batch ingestion

🌐 Available languages: [🇬🇧 English](README.md) | [🇪🇸 Español](README.es.md)

**DataKeyring** is a modular utility for batch reading of tabular files in the following formats:

- **CSV**
- **JSON**
- **JSONL**
- **Parquet**

> 🧪 Experimental support for NetCDF (`.nc`) files is under development.

It is designed to be used in batch processing systems where you need to read a specific number of rows per block, without worrying about the underlying format.

---

## 🚀 Features

- ✅ Plug & play reading of the 4 main formats.
- ✅ Batch support: you can specify how many rows you want in each iteration.
- ✅ Lightweight implementation with no unnecessary dependencies.
- ✅ Designed to decouple reading logic from main processing engines.
- ✅ Based on **Jackson** (JSON, JSONL) and **Apache Hadoop** (Parquet).
- ✅ Fully integrable into `DataSlice`, orchestrators, or external engines.

---

## 🧪 Tests

This project includes unit tests for each of the supported formats.

- Tests expect to find a test file named `testdata` with the corresponding extension inside the following folder:
  ```
  src/test/resources
  ```

- For example:
  - `src/test/resources/testdata.csv`
  - `src/test/resources/testdata.json`
  - `src/test/resources/testdata.jsonl`
  - `src/test/resources/testdata.parquet`

---

## 📦 Usage in Maven

```xml
<dependency>
	<groupId>com.mcp.datakey</groupId>
	<artifactId>DataKeyring</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

## 🧱 Structure

```
src/
├── main/
│   └── java/com/yourcompany/datakeyring/
│       ├── CsvReader.java
│       ├── JsonReader.java
│       ├── JsonlReader.java
│       └── ParquetReader.java
└── test/
    └── java/com/yourcompany/datakeyring/
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

## 🔧 In development

- Reading `.nc` (NetCDF) files.
- Batch writing module.
- Partitioning and additional metadata for structural analysis.

---

## 📜 License

Distributed under the **Apache 2.0** license.  
You can use, modify, and distribute it freely, as long as proper attribution is maintained.

---

## ✍️ Author

Developed by [Miguel Cid](https://github.com/miguelCidPaz) as part of a modular tabular processing stack.
