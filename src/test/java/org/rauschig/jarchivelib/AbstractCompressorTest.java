/**
 * Copyright 2013 Thomas Rausch
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    compressDestinationFile =
        new File(compressDestinationDir, "compress.txt" + compressor.getFilenameExtension());
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

  @Test
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
      compressor.compress(NON_EXISTING_FILE, compressDestinationFile);
    } finally {
      assertFalse(compressDestinationFile.exists());
    }
  }

  @Test(expected = FileNotFoundException.class)
  public void compress_withNonExistingDestination_throwsException() throws Exception {
    compressor.compress(original, NON_EXISTING_FILE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void compress_withNonWritableDestinationFile_throwsException() throws Exception {
    compressor.compress(original, NON_WRITABLE_FILE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void compress_withNonWritableDestinationDirectory_throwsException() throws Exception {
    compressor.compress(original, NON_WRITABLE_DIR);
  }

  @Test
  public void decompress_withFileDestination_decompressesFileCorrectly() throws Exception {
    compressor.decompress(compressedFile, decompressDestinationFile);

    assertDecompressionWasSuccessful();
  }

  @Test
  public void decompress_withDirectoryDestination_decompressesFileCorrectly() throws Exception {
    compressor.decompress(compressedFile, decompressDestinationDir);

    assertDecompressionWasSuccessful();
  }

  @Test(expected = FileNotFoundException.class)
  public void decompress_withNonExistingDestination_throwsException() throws Exception {
    compressor.decompress(compressedFile, NON_EXISTING_FILE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void decompress_withNonWritableDestinationFile_throwsException() throws Exception {
    compressor.decompress(compressedFile, NON_WRITABLE_FILE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void decompress_withNonWritableDestinationDirectory_throwsException() throws Exception {
    compressor.decompress(compressedFile, NON_WRITABLE_DIR);
  }

  @Test(expected = FileNotFoundException.class)
  public void decompress_nonExistingFile_throwsException() throws Exception {
    compressor.decompress(NON_EXISTING_FILE, decompressDestinationFile);
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
