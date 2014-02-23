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
        if (source.isDirectory()) {
            throw new IllegalArgumentException("Can not compress " + source + ". Source is a directory.");
        } else if (!source.exists()) {
            throw new FileNotFoundException(source.getPath());
        } else if (!source.canRead()) {
            throw new IllegalArgumentException("Can not compress " + source + ". Can not read from source.");
        } else if (destination.isDirectory()) {
            throw new IllegalArgumentException("Can not compress into " + destination
                    + ". Destination is a directory.");
        }

        try (CompressorOutputStream compressed = createCompressorOutputStream(this, destination);
             BufferedInputStream input = new BufferedInputStream(new FileInputStream(source))) {
            IOUtils.copy(input, compressed);
        } catch (CompressorException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void decompress(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            throw new IllegalArgumentException("Can not decompress " + source + ". Source is a directory.");
        } else if (!source.exists()) {
            throw new FileNotFoundException(source.getName());
        } else if (!source.canRead()) {
            throw new IllegalArgumentException("Can not decompress " + source + ". Can not read from source.");
        } else if (destination.isDirectory()) {
            throw new IllegalArgumentException("Can not decompress into " + destination
                    + ". Destination is a directory.");
        }

        try (CompressorInputStream compressed = createCompressorInputStream(source);
             FileOutputStream output = new FileOutputStream(destination)) {
            IOUtils.copy(compressed, output);
        } catch (CompressorException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getFilenameExtension() {
        return getCompressionType().getDefaultFileExtension();
    }
}
