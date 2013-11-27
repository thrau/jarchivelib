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
import java.util.Date;

/**
 * The entry of an archive.
 * <p>
 * The life of an {@link ArchiveEntry} is dependent on the status of the {@link ArchiveStream} it came from. Once
 * retrieved via {@link ArchiveStream#getNextEntry()}, the entry can be used as long as the {@code ArchiveStream}
 * remains on this entry, i.e. {@code getNextEntry()} was not called, and the stream was not since closed.
 */
public interface ArchiveEntry {

    /**
     * Special value indicating that the size is unknown
     */
    static final long UNKNOWN_SIZE = -1;

    /**
     * The name of the entry in the archive. May refer to a file or directory or other item.
     * 
     * @return the name of the item
     */
    String getName();

    /**
     * The (uncompressed) size of the entry. May be -1 (UNKNOWN_SIZE) if the size is unknown
     * 
     * @return the size of the entry once uncompressed, or -1 if unknown.
     */
    long getSize();

    /**
     * Returns the last modified date of the entry.
     * 
     * @return the date the entry was last modified.
     */
    Date getLastModifiedDate();

    /**
     * Checks whether the given entry is a directory.
     * 
     * @return true if the entry refers to a directory
     */
    boolean isDirectory();

    /**
     * Extracts the entry to the given destination directory.
     * <p>
     * The destination is expected to be a writable directory.
     * 
     * @param destination the directory to etract the value to
     * @return the extracted File
     * @throws IOException propagated I/O errors by {@code java.io}
     * @throws IllegalStateException if the entry is out of sync with the stream
     * @throws IllegalArgumentException if the destination is not a directory, or a directory can not be created at the
     *         given location
     */
    File extract(File destination) throws IOException, IllegalStateException, IllegalArgumentException;

}
