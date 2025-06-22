package com.mcp.io.reader.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonReader implements TabularReader {

    private String[] headers = new String[0]; // se irá actualizando con el primer batch

    @Override
    public Iterable<ReadBatch> read(String path, int batchSize) {
        return () -> {
            try {
                return new JsonBatchIterator(path, batchSize);
            } catch (IOException e) {
                throw new RuntimeException("Error initializing JsonBatchIterator", e);
            }
        };
    }

    @Override
    public String[] getHeaders() {
        return headers;
    }

    private class JsonBatchIterator implements Iterator<ReadBatch> {
        private final JsonParser parser;
        private final ObjectMapper mapper = new ObjectMapper();
        private final String datasetName;
        private final int batchSize;
        private boolean finished = false;
        private int currentRowIndex = 0;
        private JsonToken nextToken;

        public JsonBatchIterator(String path, int batchSize) throws IOException {
            this.batchSize = batchSize;
            this.datasetName = extractFileName(path);
            JsonFactory factory = new JsonFactory();
            this.parser = factory.createParser(new File(path));

            this.nextToken = parser.nextToken();
            if (nextToken != JsonToken.START_ARRAY) {
                finished = true;
                parser.close();
                throw new IOException("JSON root is not an array");
            }

            // Avanza al primer objeto
            nextToken = parser.nextToken();
        }

        @Override
        public boolean hasNext() {
            return !finished && nextToken == JsonToken.START_OBJECT;
        }

        @Override
        public ReadBatch next() {
            if (!hasNext()) throw new NoSuchElementException("No data found in batch");

            List<String[]> currentRows = new ArrayList<>();
            LinkedHashSet<String> columns = new LinkedHashSet<>();
            int start = currentRowIndex;

            try {
                int count = 0;
                while (count < batchSize && nextToken == JsonToken.START_OBJECT) {
                    JsonNode node = mapper.readTree(parser);

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

                    nextToken = parser.nextToken();
                }

                if (nextToken == null || nextToken == JsonToken.END_ARRAY) {
                    finished = true;
                    parser.close();
                }

                // actualiza headers global si están vacíos
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
                try { parser.close(); } catch (IOException ignored) {}
                throw new RuntimeException("Error reading JSON", e);
            }
        }

        private String extractFileName(String path) {
            return path.replace("\\", "/").replaceAll(".*/", "");
        }
    }
}