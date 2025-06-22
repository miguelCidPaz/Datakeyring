package com.mcp.io.reader.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;

import java.io.*;
import java.util.*;

public class JsonlReader implements TabularReader {

    private String[] headers = new String[0];

    @Override
    public Iterable<ReadBatch> read(String path, int batchSize) {
        return () -> {
            try {
                return new JsonlBatchIterator(path, batchSize);
            } catch (IOException e) {
                throw new RuntimeException("Error initializing JsonlBatchIterator", e);
            }
        };
    }

    @Override
    public String[] getHeaders() {
        return headers;
    }

    private class JsonlBatchIterator implements Iterator<ReadBatch> {
        private final BufferedReader reader;
        private final ObjectMapper mapper = new ObjectMapper();
        private final String datasetName;
        private final int batchSize;
        private boolean finished = false;
        private int currentRowIndex = 0;

        public JsonlBatchIterator(String path, int batchSize) throws IOException {
            this.batchSize = batchSize;
            this.datasetName = extractFileName(path);
            this.reader = new BufferedReader(new FileReader(path));
        }

        @Override
        public boolean hasNext() {
            return !finished;
        }

        @Override
        public ReadBatch next() {
            if (!hasNext()) throw new NoSuchElementException("No more data");

            List<String[]> currentRows = new ArrayList<>();
            LinkedHashSet<String> columns = new LinkedHashSet<>();
            int start = currentRowIndex;

            try {
                String line = null;
                int count = 0;

                while (count < batchSize && (line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue; // saltar líneas vacías

                    JsonNode node = mapper.readTree(line);

                    for (Iterator<String> it = node.fieldNames(); it.hasNext(); )
                        columns.add(it.next());

                    String[] row = new String[columns.size()];
                    int i = 0;
                    for (String col : columns) {
                        JsonNode val = node.get(col);
                        row[i++] = (val != null && !val.isNull()) ? val.asText() : "";
                    }

                    currentRows.add(row);
                    count++;
                    currentRowIndex++;
                }

                if (line == null) {
                    finished = true;
                    reader.close();
                }

                if (headers.length == 0)
                    headers = columns.toArray(new String[0]);

                return new ReadBatch(
                    currentRows.toArray(new String[0][]),
                    columns.toArray(new String[0]),
                    datasetName,
                    start,
                    currentRowIndex - 1
                );

            } catch (IOException e) {
                finished = true;
                try { reader.close(); } catch (IOException ignored) {}
                throw new RuntimeException("Error reading JSONL", e);
            }
        }

        private String extractFileName(String path) {
            return path.replace("\\", "/").replaceAll(".*/", "");
        }
    }
}
