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

import java.io.File;

/**
 * Factory for creating {@link Compressor} instances by a given compression algorithm. Use the constants in this class
 * to pass to the factory method.
 */
public final class CompressorFactory {

    private CompressorFactory() {

    }

    /**
     * Probes the given {@link File} for its file type and creates a {@link Compressor} based on this file type.
     *
     * @param file the file to check.
     * @return a new Compressor instance
     * @throws IllegalArgumentException if the given file is not a known compressed file type
     */
    public static Compressor createCompressor(File file) throws IllegalArgumentException {
        FileType fileType = FileType.get(file);

        if (fileType == FileType.UNKNOWN) {
            throw new IllegalArgumentException("Unknown file extension " + file.getName());
        }

        return createCompressor(fileType);
    }

    /**
     * Creates a new {@link Compressor} for the given {@link FileType}.
     *
     * @param fileType the file type to create the compressor for
     * @return a new Compressor instance
     * @throws IllegalArgumentException if the given file type is not a known compression type
     */
    public static Compressor createCompressor(FileType fileType) throws IllegalArgumentException {
        if (fileType == FileType.UNKNOWN) {
            throw new IllegalArgumentException("Unknown file type");
        }

        if (fileType.isCompressed()) {
            return createCompressor(fileType.getCompressionType());
        } else {
            throw new IllegalArgumentException("Unknown compressed file type " + fileType);
        }
    }

    /**
     * Creates a compressor from the given compression type.
     *
     * @param compression the name of the compression algorithm e.g. "gz" or "bzip2".
     * @return a new {@link Compressor} instance for the given compression algorithm
     * @throws IllegalArgumentException if the compression type is unknown
     */
    public static Compressor createCompressor(String compression) throws IllegalArgumentException {
        if (!CompressionType.isValidCompressionType(compression)) {
            throw new IllegalArgumentException("Unkonwn compression type " + compression);
        }

        return createCompressor(CompressionType.fromString(compression));
    }

    /**
     * Creates a compressor from the given CompressionType.
     *
     * @param compression the type of the compression algorithm
     * @return a new {@link Compressor} instance that uses the specified compression algorithm.
     */
    public static Compressor createCompressor(CompressionType compression) {
        return new CommonsCompressor(compression);
    }

}
