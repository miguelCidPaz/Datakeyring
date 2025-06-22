package com.mcp.io.reader.impl;

import com.mcp.io.models.ReadBatch;
import com.mcp.io.reader.TabularReader;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {
	
	@Test
	void testReadBasicCsv() throws URISyntaxException {
	    String filePath = Paths.get(
	        getClass().getClassLoader().getResource("testdata.csv").toURI()
	    ).toString();

	    TabularReader reader = new CsvReader();
	    int batchSize = 2;

	    List<ReadBatch> batches = new ArrayList<>();
	    for (ReadBatch batch : reader.read(filePath, batchSize)) {
	        if (batch != null) {
	            batches.add(batch);
	        }
	    }

	    // Confirmamos que hay 2 bloques (2 + 2)
	    assertEquals(2, batches.size(), "Debe haber 2 bloques");

	    // Batch 1
	    ReadBatch b1 = batches.get(0);
	    assertArrayEquals(new String[]{"id", "name", "score"}, b1.getHeaders());
	    assertEquals(2, b1.getBatchSize());
	    assertEquals(0, b1.getStartRowIndex());
	    assertEquals(1, b1.getEndRowIndex());

	    // Batch 2
	    ReadBatch b2 = batches.get(1);
	    assertEquals(2, b2.getBatchSize());
	    assertEquals(2, b2.getStartRowIndex());
	    assertEquals(3, b2.getEndRowIndex());

	}

}
