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

import static org.rauschig.jarchivelib.CommonsStreamFactory.createCompressorInputStream;
import static org.rauschig.jarchivelib.CommonsStreamFactory.createCompressorOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Implementation of a compressor that uses {@link CompressorStreamFactory} to generate compressor
 * streams by a given compressor name passed when creating the GenericCompressor. Thus, it can be
 * used for all compression algorithms the {@code org.apache.commons.compress} library supports.
 */
class CommonsCompressor implements Compressor {

  private final CompressionType compressionType;

  CommonsCompressor(final CompressionType type) {
    this.compressionType = type;
  }

  public CompressionType getCompressionType() {
    return this.compressionType;
  }

  @Override
  public void compress(final File source, File destination) throws IllegalArgumentException, IOException {
    this.assertSource(source);
    this.assertDestination(destination);

    if (destination.isDirectory()) {
      destination = new File(destination, this.getCompressedFilename(source));
    }

    CompressorOutputStream compressed = null;
    BufferedInputStream input = null;
    try {
      input = new BufferedInputStream(new FileInputStream(source));
      compressed = createCompressorOutputStream(this, destination);

      IOUtils.copy(input, compressed);
    } catch (final CompressorException e) {
      throw new IOException(e);
    } finally {
      IOUtils.closeQuietly(compressed);
      IOUtils.closeQuietly(input);
    }
  }

  @Override
  public void decompress(final File source, File destination) throws IOException {
    this.assertSource(source);
    this.assertDestination(destination);

    if (destination.isDirectory()) {
      destination = new File(destination, this.getDecompressedFilename(source));
    }

    CompressorInputStream compressed = null;
    FileOutputStream output = null;
    try {
      compressed = createCompressorInputStream(this.getCompressionType(), source);
      output = new FileOutputStream(destination);
      IOUtils.copy(compressed, output);
    } catch (final CompressorException e) {
      throw new IOException(e);
    } finally {
      IOUtils.closeQuietly(compressed);
      IOUtils.closeQuietly(output);
    }
  }

  @Override
  public InputStream decompressingStream(final InputStream compressedStream) throws IOException {
    try {
      return CommonsStreamFactory.createCompressorInputStream(
          this.getCompressionType(), compressedStream);
    } catch (final CompressorException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String getFilenameExtension() {
    return this.getCompressionType().getDefaultFileExtension();
  }

  private String getCompressedFilename(final File source) {
    return source.getName() + this.getFilenameExtension();
  }

  private String getDecompressedFilename(final File source) {
    final FileType fileType = FileType.get(source);

    if (this.compressionType != fileType.getCompressionType()) {
      throw new IllegalArgumentException(source + " is not of type " + this.compressionType);
    }

    return source.getName().substring(0, source.getName().length() - fileType.getSuffix().length());
  }

  private void assertSource(final File source) throws IllegalArgumentException, FileNotFoundException {
    if (source == null) {
      throw new IllegalArgumentException("Source is null");
    } else if (source.isDirectory()) {
      throw new IllegalArgumentException("Source " + source + " is a directory.");
    } else if (!source.exists()) {
      throw new FileNotFoundException(source.getName());
    } else if (!source.canRead()) {
      throw new IllegalArgumentException("Can not read from source " + source);
    }
  }

  private void assertDestination(final File destination) {
    if (destination == null) {
      throw new IllegalArgumentException("Destination is null");
    } else if (destination.isDirectory()) {
      if (!destination.canWrite()) {
        throw new IllegalArgumentException("Can not write to destination " + destination);
      }
    } else if (destination.exists() && !destination.canWrite()) {
      throw new IllegalArgumentException("Can not write to destination " + destination);
    }
  }
}
