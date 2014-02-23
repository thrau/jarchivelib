package org.rauschig.jarchivelib;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractCompressorTest extends AbstractResourceTest {

    private File original = new File(RESOURCES_DIR, "compress.txt");

    private File decompressDestinationDir = ARCHIVE_EXTRACT_DIR;
    private File decompressDestinationFile;

    private File compressDestinationDir = ARCHIVE_CREATE_DIR;
    private File compressDestinationFile;

    private Compressor compressor;
    private File compressedFile;

    @Before
    public void setUp() throws Exception {
        compressor = getCompressor();
        compressedFile = getCompressedFile();

        decompressDestinationFile = new File(decompressDestinationDir, "compress.txt");
        compressDestinationFile = new File(compressDestinationFile, "compress.txt" + compressor.getFilenameExtension());
    }

    @After
    public void tearDown() throws Exception {
        compressor = null;
        compressedFile = null;

        if (decompressDestinationFile.exists()) {
            decompressDestinationFile.delete();
        }
        if (compressDestinationFile.exists()) {
            compressDestinationFile.delete();
        }

        decompressDestinationFile = null;
        compressDestinationFile = null;
    }

    protected abstract File getCompressedFile();

    protected abstract Compressor getCompressor();

    @Test
    public void compress_withFileDestination_compressesFileCorrectly() throws Exception {
        compressor.compress(original, compressDestinationFile);

        assertCompressionWasSuccessful();
    }

    //@Test
    public void compress_withDirectoryDestination_compressesFileCorrectly() throws Exception {
        compressor.compress(original, compressDestinationDir);

        assertCompressionWasSuccessful();
    }

    @Test(expected = IllegalArgumentException.class)
    public void compress_nonReadableFile_throwsException() throws Exception {
        try {
            compressor.compress(NON_READABLE_FILE, compressDestinationFile);
        } finally {
            assertFalse(compressDestinationFile.exists());
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void compress_nonExistingFile_throwsException() throws Exception {
        try {
            compressor.compress(new File("some/file/that/does/not/exist/../hopefully"), compressDestinationFile);
        } finally {
            assertFalse(compressDestinationFile.exists());
        }
    }

    @Test
    public void decompress_withFileDestination_decompressesFileCorrectly() throws Exception {
        compressor.decompress(compressedFile, decompressDestinationFile);

        assertDecompressionWasSuccessful();
    }

    //@Test
    public void decompress_withDirectoryDestination_decompressesFileCorrectly() throws Exception {
        compressor.decompress(compressedFile, decompressDestinationDir);

        assertDecompressionWasSuccessful();
    }

    @Test(expected = FileNotFoundException.class)
    public void decompress_nonExistingFile_throwsException() throws Exception {
        compressor.decompress(new File("some/file/that/does/not/exist/../hopefully"), decompressDestinationFile);
    }

    private void assertCompressionWasSuccessful() throws Exception {
        assertTrue(compressDestinationFile.exists());
        compressor.decompress(compressDestinationFile, decompressDestinationFile);
        assertDecompressionWasSuccessful();
    }

    private void assertDecompressionWasSuccessful() throws Exception {
        assertTrue(decompressDestinationFile.exists());
        assertFileContentEquals(original, decompressDestinationFile);
    }
}
