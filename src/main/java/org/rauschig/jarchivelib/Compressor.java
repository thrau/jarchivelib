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
import java.io.IOException;

/**
 * A compressor facades a specific compression library, allowing for simple compression and decompression of files.
 */
public interface Compressor {

    /**
     * Compresses the given input file to the given destination file.
     * 
     * @param source the source file to compress
     * @param destination the destination file
     * @throws IOException when an I/O error occurs
     */
    void compress(File source, File destination) throws IOException;

    /**
     * Decompresses the given source file to the given destination file.
     * 
     * @param source the compressed source file to decompress
     * @param destination the destination file
     * @throws IOException when an I/O error occurs
     */
    void decompress(File source, File destination) throws IOException;
}
