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
 * Implementation of a compressor that uses {@link CompressorStreamFactory} to generate compressor streams by a given
 * compressor name passed when creating the GenericCompressor. Thus, it can be used for all compression algorithms the
 * {@code org.apache.commons.compress} library supports.
 */
class CommonsCompressor implements Compressor {

    private final CompressionType compressionType;

    CommonsCompressor(CompressionType type) {
        this.compressionType = type;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    @Override
    public void compress(File source, File destination) throws IllegalArgumentException, IOException {
        assertSource(source);
        assertDestination(destination);

        if (destination.isDirectory()) {
            destination = new File(destination, getCompressedFilename(source));
        }

        CompressorOutputStream compressed = null;
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            compressed = createCompressorOutputStream(this, destination);

            IOUtils.copy(input, compressed);
        } catch (CompressorException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(compressed);
            IOUtils.closeQuietly(input);
        }
    }

    @Override
    public void decompress(File source, File destination) throws IOException {
        assertSource(source);
        assertDestination(destination);

        if (destination.isDirectory()) {
            destination = new File(destination, getDecompressedFilename(source));
        }

        CompressorInputStream compressed = null;
        FileOutputStream output = null;
        try {
            compressed = createCompressorInputStream(getCompressionType(), source);
            output = new FileOutputStream(destination);
            IOUtils.copy(compressed, output);
        } catch (CompressorException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(compressed);
            IOUtils.closeQuietly(output);
        }
    }

    @Override
    public InputStream decompressingStream(InputStream compressedStream) throws IOException {
        try {
            return CommonsStreamFactory.createCompressorInputStream(getCompressionType(), compressedStream);
        } catch (CompressorException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getFilenameExtension() {
        return getCompressionType().getDefaultFileExtension();
    }

    private String getCompressedFilename(File source) {
        return source.getName() + getFilenameExtension();
    }

    private String getDecompressedFilename(File source) {
        FileType fileType = FileType.get(source);

        if (compressionType != fileType.getCompressionType()) {
            throw new IllegalArgumentException(source + " is not of type " + compressionType);
        }

        return source.getName().substring(0, source.getName().length() - fileType.getSuffix().length());
    }

    private void assertSource(File source) throws IllegalArgumentException, FileNotFoundException {
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

    private void assertDestination(File destination) {
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
