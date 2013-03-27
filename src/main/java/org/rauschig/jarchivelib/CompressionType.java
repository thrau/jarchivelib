package org.rauschig.jarchivelib;

import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Denotes a compression algorithm such as gzip or bzip2
 */
public enum CompressionType {

    /**
     * Constant used to identify the BZIP2 compression algorithm.
     */
    BZIP2(CompressorStreamFactory.BZIP2),
    /**
     * Constant used to identify the GZIP compression algorithm.
     */
    GZIP(CompressorStreamFactory.GZIP),
    /**
     * Constant used to identify the PACK200 compression algorithm.
     */
    PACK200(CompressorStreamFactory.PACK200);

    /**
     * The name by which the compression algorithm is identified by
     */
    private final String name;

    private CompressionType(String name) {
        this.name = name;
    }

    /**
     * Returns the name by which the compression algorithm is identified by.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the given compression type is known to the factory, false otherwise.
     */
    public static boolean isValidCompressionType(String compression) {
        for (CompressionType type : values()) {
            if (compression.equalsIgnoreCase(type.getName())) {
                return true;
            }
        }

        return false;
    }
}
