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
 * Holds the file extension as String and the corresponding {@link ArchiveFormat} and/or {@link CompressionType}.
 */
final class FileType {

    private final String suffix;
    private final ArchiveFormat archiveFormat;
    private final CompressionType compression;

    public FileType(String suffix, ArchiveFormat archiveFormat) {
        this(suffix, archiveFormat, null);
    }

    public FileType(String suffix, CompressionType compression) {
        this(suffix, null, compression);
    }

    public FileType(String suffix, ArchiveFormat archiveFormat, CompressionType compression) {
        this.suffix = suffix;
        this.compression = compression;
        this.archiveFormat = archiveFormat;
    }

    /**
     * Returns true if the given file extension denotes an archive.
     * 
     * @return true if file extension is an archive, false otherwise
     */
    public boolean isArchive() {
        return archiveFormat != null;
    }

    /**
     * Returns true if the given file extension denotes a compressed file.
     * 
     * @return true if file extension is a compressed type, false otherwise
     */
    public boolean isCompressed() {
        return compression != null;
    }

    /**
     * Returns the file extension suffix (e.g. ".zip" or ".tar.gz").
     * 
     * @return the file extension suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Returns the archive format corresponding to this file extension if any.
     * 
     * @return the archive format or null if the file extension does not denote an archive
     */
    public ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    /**
     * Returns the compression type corresponding to this file extension if any.
     * 
     * @return the compression type or null if the file extension does not denote a compressed file
     */
    public CompressionType getCompression() {
        return compression;
    }

}
