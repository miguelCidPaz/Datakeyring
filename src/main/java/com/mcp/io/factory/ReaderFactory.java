package com.mcp.io.factory;

import com.mcp.io.reader.TabularReader;
import com.mcp.io.reader.impl.CsvReader;
import com.mcp.io.reader.impl.JsonReader;
import com.mcp.io.reader.impl.JsonlReader;
import com.mcp.io.reader.impl.ParquetTabReader;

import java.util.Locale;

public class ReaderFactory {

    public static TabularReader of(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path no puede ser nulo o vacío");
        }

        String extension = extractExtension(path.toLowerCase(Locale.ROOT));

        switch (extension) {
            case "csv":
                return new CsvReader();
            case "json":
                return new JsonReader();
            case "jsonl":
                return new JsonlReader();
            case "parquet":
                return new ParquetTabReader();
            default:
                throw new UnsupportedOperationException("Formato no soportado: " + path);
        }
    }

    private static String extractExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        return (lastDot != -1 && lastDot < path.length() - 1)
            ? path.substring(lastDot + 1)
            : "";
    }
}
