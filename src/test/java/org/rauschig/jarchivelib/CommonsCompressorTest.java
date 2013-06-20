/**
 *    Copyright 2013 Thomas Rausch
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rauschig.jarchivelib;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Test;

public class CommonsCompressorTest extends AbstractArchiverTest {

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

    @Test(expected = FileNotFoundException.class)
    public void compress_gzip_nonExistingFile_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.compress(new File("some/file/that/does/not/exist/../hopefully"), destinationGz);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compress_gzip_nonReadableFile_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.compress(NON_READABLE_FILE, destinationGz);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compress_gzip_withDirectoryAsSource_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.compress(ARCHIVE_DIR, destinationGz);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compress_gzip_withDirectoryAsDestination_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.compress(source, ARCHIVE_DIR);
    }

    @Test(expected = FileNotFoundException.class)
    public void decompress_gzip_nonExistingFile_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.decompress(new File("some/file/that/does/not/exist/../hopefully"), destination);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decompress_gzip_nonReadableFile_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.decompress(NON_READABLE_FILE, destination);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decompress_gzip_withDirectoryAsSource_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.decompress(ARCHIVE_DIR, destination);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decompress_gzip_withDirectoryAsDestination_shouldFail() throws Exception {
        compressor = CompressorFactory.createCompressor(CompressionType.GZIP);

        compressor.decompress(sourceGz, ARCHIVE_DIR);
    }
}
