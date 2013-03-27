package org.rauschig.jarchivelib;

/**
 * Factory for creating {@link Compressor} instances by a given compression algorithm. Use the constants in this class
 * to pass to the factory method.
 */
public final class CompressorFactory {

    private CompressorFactory() {

    }

    /**
     * Creates a compressor from the given compression type
     * 
     * @throws IllegalArgumentException if the compression type is unknown
     */
    public static Compressor createCompressor(String compression) {
        if (!CompressionType.isValidCompressionType(compression)) {
            throw new IllegalArgumentException("Unkonwn compression type " + compression);
        }

        return new GenericCompressor(compression);
    }

    /**
     * Creates a compressor from the given CompressionType
     */
    public static Compressor createCompressor(CompressionType compression) {
        return new GenericCompressor(compression.getName());
    }

}
