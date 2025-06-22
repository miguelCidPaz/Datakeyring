package com.mcp.io.reader.impl;

import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ParquetTabReaderTest {

    @Test
    void testReadBasicParquet() throws URISyntaxException {
        String filePath = Paths.get(
            getClass().getClassLoader().getResource("testdata.parquet").toURI()
        ).toString();

        TabularReader reader = new ParquetTabReader();
        int batchSize = 5;

        Iterator<ReadBatch> iterator = reader.read(filePath, batchSize).iterator();

        // Bloque 1
        assertTrue(iterator.hasNext());
        ReadBatch b1 = iterator.next();
        assertNotNull(b1.getHeaders());
        assertEquals(batchSize, b1.getBatchSize());
        assertEquals(0, b1.getStartRowIndex());
        assertEquals(batchSize - 1, b1.getEndRowIndex());

        // Bloque 2
        assertTrue(iterator.hasNext());
        ReadBatch b2 = iterator.next();
        assertEquals(batchSize, b2.getBatchSize());
        assertEquals(batchSize, b2.getStartRowIndex());
        assertEquals((batchSize * 2) - 1, b2.getEndRowIndex());

        // No validamos todos los bloques → solo estructura inicial
    }
}
