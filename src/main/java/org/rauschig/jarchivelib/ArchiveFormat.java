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

import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/**
 * Denotes an archive format such as zip or tar.
 */
public enum ArchiveFormat {

    /**
     * Constant used to identify the AR archive format.
     */
    AR(ArchiveStreamFactory.AR),
    /**
     * Constant used to identify the CPIO archive format.
     */
    CPIO(ArchiveStreamFactory.CPIO),
    /**
     * Constant used to identify the Unix DUMP archive format.
     */
    DUMP(ArchiveStreamFactory.DUMP),
    /**
     * Constant used to identify the JAR archive format.
     */
    JAR(ArchiveStreamFactory.JAR),
    /**
     * Constant used to identify the TAR archive format.
     */
    TAR(ArchiveStreamFactory.TAR),
    /**
     * Constant used to identify the ZIP archive format.
     */
    ZIP(ArchiveStreamFactory.ZIP);

    /**
     * The name by which the compression algorithm is identified by
     */
    private final String name;

    private ArchiveFormat(String name) {
        this.name = name;
    }

    /**
     * Returns the name by which the archive format is identified by.
     * 
     * @return the archiver format name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the given archive format name is valid and known format.
     * 
     * @param archiveFormat the archive format name
     * @return true if the given archive format is known to the factory, false otherwise
     */
    public static boolean isValidArchiveFormat(String archiveFormat) {
        for (ArchiveFormat format : values()) {
            if (archiveFormat.equalsIgnoreCase(format.getName())) {
                return true;
            }
        }

        return false;
    }
}
