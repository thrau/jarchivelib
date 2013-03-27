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

/**
 * Factory for creating {@link Compressor} instances by a given compression algorithm. Use the constants in this class
 * to pass to the factory method.
 */
public final class CompressorFactory {

    private CompressorFactory() {

    }

    /**
     * Creates a compressor from the given compression type
     * 
     * @throws IllegalArgumentException if the compression type is unknown
     */
    public static Compressor createCompressor(String compression) {
        if (!CompressionType.isValidCompressionType(compression)) {
            throw new IllegalArgumentException("Unkonwn compression type " + compression);
        }

        return new GenericCompressor(compression);
    }

    /**
     * Creates a compressor from the given CompressionType
     */
    public static Compressor createCompressor(CompressionType compression) {
        return new GenericCompressor(compression.getName());
    }

}
