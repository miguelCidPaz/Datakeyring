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

## 📦 Usage in Maven

To use this package from **GitHub Packages**, add the following to your `pom.xml`:

### ① Add the repository

```xml
<repositories>
  <repository>
    <id>github</id>
    <name>GitHub Packages</name>
    <url>https://maven.pkg.github.com/miguelCidPaz/Datakeyring</url>
  </repository>
</repositories>
```

### ② Add the dependency

```xml
<dependency>
  <groupId>io.github.miguelCidPaz</groupId>
  <artifactId>datakeyring</artifactId>
  <version>0.0.2</version>
</dependency>
```

Make sure you’ve configured authentication in your `~/.m2/settings.xml` as explained [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).

---

## 🧪 Tests

This project includes unit tests for each of the supported formats.

Test files should be located under:

```
src/test/resources/
```

For example:

- `testdata.csv`
- `testdata.json`
- `testdata.jsonl`
- `testdata.parquet`

---

## 🧱 Structure

```
src/
├── main/
│   └── java/io/github/miguelCidPaz/datakeyring/
│       ├── CsvReader.java
│       ├── JsonReader.java
│       ├── JsonlReader.java
│       └── ParquetReader.java
└── test/
    └── java/io/github/miguelCidPaz/datakeyring/
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

Developed by [Miguel Cid](https://github.com/miguelCidPaz)
