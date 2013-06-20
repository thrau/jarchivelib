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

    private CompressorStreamFactory streamFactory = new CompressorStreamFactory();

    private final String compressorName;
    private final String fileExtension;

    CommonsCompressor(String compressorName) {
        this.compressorName = compressorName.toLowerCase();
        this.fileExtension = "." + compressorName.toLowerCase();
    }

    /**
     * Returns the name of the compressor.
     * 
     * @return the compressor name.
     * @see CompressorFactory
     */
    public String getCompressorName() {
        return compressorName;
    }

    /**
     * Returns the file extension, which is equal to "." + {@link #getCompressorName()}.
     * 
     * @return the filename extension
     */
    public String getFileExtension() {
        return fileExtension;
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

        try (CompressorOutputStream compressed = createCompressorOutputStream(destination);
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

    /**
     * Uses the {@link #streamFactory} and the {@link #compressorName} to create a new {@link CompressorOutputStream}
     * for the given destination {@link File}.
     * 
     * @param destination the file to create the {@link CompressorOutputStream} for
     * @return a new {@link CompressorOutputStream}
     * @throws IOException if an I/O error occurs
     * @throws CompressorException if the compressor name is not known
     */
    protected CompressorOutputStream createCompressorOutputStream(File destination) throws IOException,
        CompressorException {
        return streamFactory.createCompressorOutputStream(compressorName, new FileOutputStream(destination));
    }

    /**
     * Uses the {@link #streamFactory} to create a new {@link CompressorInputStream} for the given source {@link File}.
     * 
     * @param source the file to create the {@link CompressorInputStream} for
     * @return a new {@link CompressorInputStream}
     * @throws IOException if an I/O error occurs
     * @throws CompressorException if the compressor name is not known
     */
    protected CompressorInputStream createCompressorInputStream(File source) throws IOException, CompressorException {
        return streamFactory.createCompressorInputStream(new BufferedInputStream(new FileInputStream(source)));
    }
}
