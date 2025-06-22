package com.mcp.io.reader.impl;

import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CsvReader implements TabularReader {

    private String[] headers = null;

    @Override
    public Iterable<ReadBatch> read(String path, int batchSize) {
        return new Iterable<ReadBatch>() {
            @Override
            public Iterator<ReadBatch> iterator() {
                try {
                    return new CsvBatchIterator(path, batchSize);
                } catch (IOException e) {
                    throw new RuntimeException("Error opening CSV file: " + e.getMessage(), e);
                }
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

        public CsvBatchIterator(String path, int batchSize) throws IOException {
            this.batchSize = batchSize;
            this.reader = new BufferedReader(new FileReader(path));
            this.sourceName = extractFileName(path);

            String headerLine = reader.readLine();
            if (headerLine == null) {
                hasMore = false;
                reader.close();
                throw new IOException("CSV file is empty or missing headers: " + path);
            }
            headers = headerLine.split(",");
        }

        @Override
        public boolean hasNext() {
            return hasMore;
        }

        @Override
        public ReadBatch next() {
            if (!hasMore) throw new NoSuchElementException("No more data to read");

            ArrayList<String[]> rows = new ArrayList<>();
            int start = currentRowIndex;
            int count = 0;

            try {
                String line;
                while (count < batchSize && (line = reader.readLine()) != null) {
                    rows.add(line.split(",", -1)); // -1 para mantener columnas vacías
                    count++;
                    currentRowIndex++;
                }

                if (rows.isEmpty()) {
                    hasMore = false;
                    reader.close();
                    return null;
                }

                if (count < batchSize) {
                    hasMore = false;
                    reader.close();
                }

                String[][] batchRows = rows.toArray(new String[0][]);
                return new ReadBatch(batchRows, headers, sourceName, start, currentRowIndex - 1);

            } catch (IOException e) {
                hasMore = false;
                try { reader.close(); } catch (IOException ignored) {}
                throw new RuntimeException("Error reading CSV batch: " + e.getMessage(), e);
            }
        }

        private String extractFileName(String path) {
            String clean = path.replace("\\", "/");
            return clean.substring(clean.lastIndexOf('/') + 1);
        }
    }
}
