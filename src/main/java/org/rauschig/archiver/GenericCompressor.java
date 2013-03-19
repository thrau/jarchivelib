package org.rauschig.archiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Implementation of a compressor that uses {@link CompressorStreamFactory} to generate compressor streams by a given
 * compressor name passed when creating the GenericCompressor. Thus, it can be used for all compression algorithms the
 * {@code org.apache.commons.compress} library supports.
 */
class GenericCompressor implements Compressor {

    private CompressorStreamFactory streamFactory = new CompressorStreamFactory();

    private final String compressorName;
    private final String fileExtension;

    GenericCompressor(String compressorName) {
        this.compressorName = compressorName.toLowerCase();
        this.fileExtension = "." + compressorName.toLowerCase();
    }

    /**
     * Returns the name of the compressor.
     * 
     * @see CompressorFactory
     */
    public String getCompressorName() {
        return compressorName;
    }

    /**
     * Returns the file extension, which is equal to "." + {@link #getCompressorName()}
     */
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void compress(File source, File destination) throws IOException {
        try (CompressorOutputStream compressed = createCompressorOutputStream(destination);
                BufferedInputStream input = new BufferedInputStream(new FileInputStream(source))) {
            IOUtils.copy(input, compressed);
        } catch (CompressorException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void decompress(File source, File destination) throws IOException {
        try (CompressorInputStream compressed = createCompressorInputStream(source);
                FileOutputStream output = new FileOutputStream(destination)) {
            IOUtils.copy(compressed, output);
        } catch (CompressorException e) {
            throw new IOException(e);
        }
    }

    /**
     * Uses the {@link #streamFactory} and the {@link #compressorName} to create a new {@link CompressorOutputStream}
     * for the given destination {@link File}.
     * 
     * @param destination the file to create the {@link CompressorOutputStream} for
     * @return a new {@link CompressorOutputStream}
     * @throws IOException
     * @throws CompressorException if the compressor name is not known
     */
    protected CompressorOutputStream createCompressorOutputStream(File destination) throws IOException,
            CompressorException {
        return streamFactory.createCompressorOutputStream(compressorName, new FileOutputStream(destination));
    }

    /**
     * Uses the {@link #streamFactory} to create a new {@link CompressorInputStream} for the given source {@link File}.
     * 
     * @param spirce the file to create the {@link CompressorInputStream} for
     * @return a new {@link CompressorInputStream}
     * @throws IOException
     * @throws CompressorException if the compressor name is not known
     */
    protected CompressorInputStream createCompressorInputStream(File source) throws IOException, CompressorException {
        return streamFactory.createCompressorInputStream(new BufferedInputStream(new FileInputStream(source)));
    }
}
