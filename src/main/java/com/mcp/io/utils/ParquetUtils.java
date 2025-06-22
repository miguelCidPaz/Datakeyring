package com.mcp.io.utils;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.parquet.io.api.Binary;

public class ParquetUtils {
	
	public static String convertInt96ToTimestamp(Binary int96Binary) {
	    ByteBuffer buffer = int96Binary.toByteBuffer();

	    long nanos = buffer.getLong();
	    int julianDay = buffer.getInt();

	    long epochMillis = (julianDay - 2440588) * 86400L * 1000;
	    long millisOfDay = nanos / 1_000_000;
	    epochMillis += millisOfDay;

	    Instant instant = Instant.ofEpochMilli(epochMillis);
	    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

	    return dateTime.toString();
	}


}
