package org.rauschig.jarchivelib;

/**
 * Factory for creating {@link Archiver} instances by a given archiver type name. Use the constants in this class to
 * pass to the factory method.
 */
public final class ArchiverFactory {

    private ArchiverFactory() {

    }

    /**
     * Creates an Archiver for the given archive format that uses compression.
     * 
     * @param archiveFormat the archive format e.g. "tar" or "zip"
     * @param compression the compression algorithm name e.g. "gz"
     * 
     * @return a new Archiver instance that also handles compression
     * @throws IllegalArgumentException if the archive format or the compression type is unknown
     */
    public static Archiver createArchiver(String archiveFormat, String compression) {
        if (!ArchiveFormat.isValidArchiveFormat(archiveFormat)) {
            throw new IllegalArgumentException("Unknown archive format " + archiveFormat);
        }
        if (!CompressionType.isValidCompressionType(compression)) {
            throw new IllegalArgumentException("Unknown compression type " + compression);
        }

        GenericArchiver archiver = new GenericArchiver(archiveFormat);
        GenericCompressor compressor = new GenericCompressor(compression);

        return new ArchiverCompressorDecorator(archiver, compressor);
    }

    /**
     * Creates an Archiver for the given archive format that uses compression.
     * 
     * @param archiveFormat the archive format
     * @param compression the compression algorithm
     * 
     * @return a new Archiver instance that also handles compression
     */
    public static Archiver createArchiver(ArchiveFormat archiveFormat, CompressionType compression) {
        return createArchiver(archiveFormat.getName(), compression.getName());
    }

    /**
     * Creates an Archiver for the given archive format
     * 
     * @param archiveFormat the archive format e.g. "tar" or "zip"
     * 
     * @return a new Archiver instance
     * @throws IllegalArgumentException if the archive format is unknown
     */
    public static Archiver createArchiver(String archiveFormat) {
        if (!ArchiveFormat.isValidArchiveFormat(archiveFormat)) {
            throw new IllegalArgumentException("Unknown archive format " + archiveFormat);
        }

        return new GenericArchiver(archiveFormat);
    }

    /**
     * Creates an Archiver for the given archive format
     * 
     * @param archiveFormat the archive format
     * 
     * @return a new Archiver instance
     */
    public static Archiver createArchiver(ArchiveFormat archiveFormat) {
        return new GenericArchiver(archiveFormat.getName());
    }

}
