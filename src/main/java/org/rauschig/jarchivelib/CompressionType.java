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

import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Denotes a compression algorithm such as gzip or bzip2
 */
public enum CompressionType {

    /**
     * Constant used to identify the BZIP2 compression algorithm.
     */
    BZIP2(CompressorStreamFactory.BZIP2),
    /**
     * Constant used to identify the GZIP compression algorithm.
     */
    GZIP(CompressorStreamFactory.GZIP),
    /**
     * Constant used to identify the PACK200 compression algorithm.
     */
    PACK200(CompressorStreamFactory.PACK200);

    /**
     * The name by which the compression algorithm is identified by
     */
    private final String name;

    private CompressionType(String name) {
        this.name = name;
    }

    /**
     * Returns the name by which the compression algorithm is identified by.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the given compression type is known to the factory, false otherwise.
     */
    public static boolean isValidCompressionType(String compression) {
        for (CompressionType type : values()) {
            if (compression.equalsIgnoreCase(type.getName())) {
                return true;
            }
        }

        return false;
    }
}
