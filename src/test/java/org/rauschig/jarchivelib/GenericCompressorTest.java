package org.rauschig.archiver;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Test;

public class GenericCompressorTest extends AbstractArchiverTest {

    private Compressor compressor;

    private File source = new File(RESOURCES_DIR, "compress.txt");
    private File sourceGz = new File(RESOURCES_DIR, "compress.txt.gz");
    private File sourceBzip2 = new File(RESOURCES_DIR, "compress.txt.bzip2");

    private File destination = new File(ARCHIVE_EXTRACT_DIR, "compress.txt");
    private File destinationGz = new File(ARCHIVE_CREATE_DIR, "compress.txt.gz");
    private File destinationBzip2 = new File(ARCHIVE_CREATE_DIR, "compress.txt.bzip2");

    @After
    public void tearDown() {
        compressor = null;

        if (destination.exists()) {
            destination.delete();
        }
        if (destinationGz.exists()) {
            destinationGz.delete();
        }
        if (destinationBzip2.exists()) {
            destinationBzip2.delete();
        }
    }

    @Test
    public void decompress_gzip_decompressesFileCorrectly() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.decompress(sourceGz, destination);

        assertTrue(destination.exists());
        assertFileContentEquals(source, destination);
    }

    @Test
    public void compress_gzip_compressesFileCorrectly() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.compress(source, destinationGz);

        assertTrue(destinationGz.exists());

        compressor.decompress(destinationGz, destination);
        assertFileContentEquals(source, destination);
    }

    @Test
    public void decompress_bzip2_decompressesFileCorrectly() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.BZIP2);

        compressor.decompress(sourceBzip2, destination);

        assertTrue(destination.exists());
        assertFileContentEquals(source, destination);
    }

    @Test
    public void compress_bzip2_compressesFileCorrectly() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.BZIP2);

        compressor.compress(source, destinationBzip2);

        assertTrue(destinationBzip2.exists());

        compressor.decompress(destinationBzip2, destination);
        assertFileContentEquals(source, destination);
    }
}
