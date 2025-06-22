package com.mcp.io.reader;

import com.mcp.io.models.ReadBatch;

/**
 * Contrato general para cualquier lector tabular que entregue bloques de datos
 * leídos desde una fuente (archivo, stream, etc.) en forma de ReadBatch.
 */
public interface TabularReader {

    /**
     * Inicia el proceso de lectura de datos, dividiendo la fuente en bloques.
     *
     * @param path       Ruta al archivo o fuente de datos.
     * @param batchSize  Tamaño del lote (número de filas por bloque).
     * @return Iterable sobre bloques de lectura.
     */
    Iterable<ReadBatch> read(String path, int batchSize);

    /**
     * Devuelve las cabeceras detectadas tras la lectura.
     * Puede devolver null si aún no se ha leído ningún bloque.
     *
     * @return Array con nombres de columnas.
     */
    String[] getHeaders();

}
