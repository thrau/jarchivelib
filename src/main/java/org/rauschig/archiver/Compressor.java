package org.rauschig.archiver;

import java.io.File;
import java.io.IOException;

/**
 * A compressor facades a specific compression library, allowing for simple compression and decompression of files.
 */
public interface Compressor {

    /**
     * Compresses the given input file to the given destination file
     * 
     * @param source the source file to compress
     * @param destination the destination file
     * @throws IOException
     */
    void compress(File source, File destination) throws IOException;

    /**
     * Decompresses the given source file to the given destination file
     * 
     * @param source the compressed source file to decompress
     * @param destination the destination file
     * @throws IOException
     */
    void decompress(File source, File destination) throws IOException;
}
