package com.mcp.io.models;

/**
 * Representa un bloque de datos leídos desde una fuente tabular,
 * incluyendo contexto de posición dentro del dataset.
 */
public class ReadBatch {

    private final String[][] rows;
    private final String[] headers;
    private final String sourceName;
    private final int startRowIndex;
    private final int endRowIndex;

    public ReadBatch(String[][] rows, String[] headers, String sourceName, int startRowIndex, int endRowIndex) {
        if (rows == null || headers == null || sourceName == null) {
            throw new IllegalArgumentException("rows, headers, and sourceName must not be null");
        }
        if (startRowIndex < 0 || endRowIndex < startRowIndex) {
            throw new IllegalArgumentException("Invalid row index range");
        }
        this.rows = rows;
        this.headers = headers;
        this.sourceName = sourceName;
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
    }


    public String[][] getData() {
        return rows;
    }

    public String[] getHeaders() {
        return headers;
    }

    public String getDatasetName() {
        return sourceName;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public int getEndRowIndex() {
        return endRowIndex;
    }
    
    public int getBatchSize() {
        return rows.length;
    }
    
    @Override
    public String toString() {
        return "ReadBatch{" +
                "source='" + sourceName + '\'' +
                ", start=" + startRowIndex +
                ", end=" + endRowIndex +
                ", rows=" + rows.length +
                ", headers=" + headers.length +
                '}';
    }

}
