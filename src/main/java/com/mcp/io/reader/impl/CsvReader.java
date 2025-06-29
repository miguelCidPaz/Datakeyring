package com.mcp.io.reader.impl;

import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CsvReader implements TabularReader {

    private String[] headers = null;
    private static final CsvParserSettings CSV_SETTINGS = new CsvParserSettings();

    static {
        CSV_SETTINGS.setMaxCharsPerColumn(-1);     // sin límite de caracteres
        CSV_SETTINGS.setLineSeparatorDetectionEnabled(true);
        CSV_SETTINGS.setSkipEmptyLines(false);
        CSV_SETTINGS.getFormat().setDelimiter(',');
    }

    @Override
    public Iterable<ReadBatch> read(String path, int batchSize) {
        return () -> {
            try {
                return new CsvBatchIterator(path, batchSize);
            } catch (IOException e) {
                throw new RuntimeException("Error opening CSV file: " + e.getMessage(), e);
            }
        };
    }

    @Override
    public String[] getHeaders() {
        return headers;
    }

    private class CsvBatchIterator implements Iterator<ReadBatch> {
        private final BufferedReader reader;
        private final int batchSize;
        private boolean hasMore = true;
        private int currentRowIndex = 0;
        private final String sourceName;
        private final CsvParser parser;

        public CsvBatchIterator(String path, int batchSize) throws IOException {
            this.batchSize  = batchSize;
            this.reader     = new BufferedReader(new FileReader(path), 8 << 20);
            this.sourceName = extractFileName(path);

            // ── Crea el parser y deja que LEA la cabecera ──
            parser  = new CsvParser(CSV_SETTINGS);
            parser.beginParsing(reader);

            headers = parser.parseNext();          // primera fila = cabecera
            if (headers == null) {
                hasMore = false;
                reader.close();
                throw new IOException("CSV empty or corrupt: " + path);
            }
        }

        @Override public boolean hasNext() { return hasMore; }

        @Override
        public ReadBatch next() {
            if (!hasMore) throw new NoSuchElementException("No more data to read");

            ArrayList<String[]> rows = new ArrayList<>(batchSize);
            int start = currentRowIndex;

            String[] row;
            while (rows.size() < batchSize && (row = parser.parseNext()) != null) {
                rows.add(row);
                currentRowIndex++;
            }

            if (rows.isEmpty()) {          // EOF total
                hasMore = false;
                parser.stopParsing();
                try { reader.close(); } catch (IOException ignored) {}
                return null;
            }
            if (rows.size() < batchSize) { // EOF parcial (último lote)
                hasMore = false;
                parser.stopParsing();
                try { reader.close(); } catch (IOException ignored) {}
            }

            String[][] batchRows = rows.toArray(new String[0][]);
            return new ReadBatch(batchRows, headers, sourceName, start, currentRowIndex - 1);
        }

        private String extractFileName(String path) {
            String clean = path.replace("\\", "/");
            return clean.substring(clean.lastIndexOf('/') + 1);
        }
    }
}
