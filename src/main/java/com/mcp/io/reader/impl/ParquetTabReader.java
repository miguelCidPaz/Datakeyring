package com.mcp.io.reader.impl;

import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;
import com.mcp.io.utils.ParquetUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;

import java.io.IOException;
import java.util.*;

public class ParquetTabReader implements TabularReader {

    private String[] headers = new String[0];

    @Override
    public Iterable<ReadBatch> read(String path, int batchSize) {
        return () -> {
            try {
                return new ParquetBatchIterator(path, batchSize);
            } catch (IOException e) {
                throw new RuntimeException("Error initializing ParquetReader", e);
            }
        };
    }

    @Override
    public String[] getHeaders() {
        return headers;
    }

    private class ParquetBatchIterator implements Iterator<ReadBatch> {
        private final ParquetReader<Group> reader;
        private final String datasetName;
        private final int batchSize;
        private boolean finished = false;
        private int currentRowIndex = 0;
        private Group nextGroup;
        private final LinkedHashSet<String> columnNames = new LinkedHashSet<>();

        public ParquetBatchIterator(String filePath, int batchSize) throws IOException {
            this.batchSize = batchSize;
            this.datasetName = extractFileName(filePath);
            Path path = new Path(filePath);
            Configuration conf = new Configuration();
            this.reader = ParquetReader.builder(new GroupReadSupport(), path).withConf(conf).build();
            this.nextGroup = reader.read();
        }

        @Override
        public boolean hasNext() {
            return !finished && nextGroup != null;
        }

        @Override
        public ReadBatch next() {
            if (!hasNext()) throw new NoSuchElementException();

            List<String[]> rows = new ArrayList<>();
            int start = currentRowIndex;

            try {
                while (rows.size() < batchSize && nextGroup != null) {
                    Group group = nextGroup;
                    int fieldCount = group.getType().getFieldCount();
                    String[] row = new String[fieldCount];

                    for (int i = 0; i < fieldCount; i++) {
                        String colName = group.getType().getFieldName(i);
                        columnNames.add(colName);

                        String value = "";
                        if (group.getFieldRepetitionCount(colName) > 0) {
                            String typeName = group.getType().getType(i).asPrimitiveType().getPrimitiveTypeName().name();

                            if ("INT96".equals(typeName)) {
                                value = ParquetUtils.convertInt96ToTimestamp(group.getInt96(i, 0));
                            } else {
                                value = group.getValueToString(i, 0);
                            }
                        }
                        row[i] = value.trim();
                    }

                    rows.add(row);
                    currentRowIndex++;
                    nextGroup = reader.read();
                }

                if (nextGroup == null) {
                    finished = true;
                    reader.close();
                }

                if (headers.length == 0) {
                    headers = columnNames.toArray(new String[0]);
                }

                return new ReadBatch(
                    rows.toArray(new String[0][]),
                    columnNames.toArray(new String[0]),
                    datasetName,
                    start,
                    currentRowIndex - 1
                );

            } catch (IOException e) {
                finished = true;
                try { reader.close(); } catch (IOException ignored) {}
                throw new RuntimeException("Error reading Parquet block", e);
            }
        }

        private String extractFileName(String path) {
            return path.replace("\\", "/").replaceAll(".*/", "");
        }
    }
}
