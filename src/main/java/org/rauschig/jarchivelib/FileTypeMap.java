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

import static org.rauschig.jarchivelib.ArchiveFormat.AR;
import static org.rauschig.jarchivelib.ArchiveFormat.CPIO;
import static org.rauschig.jarchivelib.ArchiveFormat.DUMP;
import static org.rauschig.jarchivelib.ArchiveFormat.JAR;
import static org.rauschig.jarchivelib.ArchiveFormat.TAR;
import static org.rauschig.jarchivelib.ArchiveFormat.ZIP;
import static org.rauschig.jarchivelib.CompressionType.BZIP2;
import static org.rauschig.jarchivelib.CompressionType.GZIP;
import static org.rauschig.jarchivelib.CompressionType.PACK200;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps file extensions (e.g. ".tar.gz") to a {@link FileType} entry.
 */
final class FileTypeMap {

    private static final Map<String, FileType> MAP = new LinkedHashMap<>();

    static {
        // compressed archives
        add(".tar.gz", TAR, GZIP);
        add(".tgz", TAR, GZIP);
        add(".tar.bz2", TAR, BZIP2);
        add(".tbz2", TAR, BZIP2);
        // archive formats
        add(".a", AR);
        add(".ar", AR);
        add(".cpio", CPIO);
        add(".dump", DUMP);
        add(".jar", JAR);
        add(".tar", TAR);
        add(".zip", ZIP);
        add(".zipx", ZIP);
        // compression formats
        add(".bz2", BZIP2);
        add(".gzip", GZIP);
        add(".gz", GZIP);
        add(".pack", PACK200);
    }

    private FileTypeMap() {

    }

    private static void add(String suffix, ArchiveFormat archiveFormat) {
        MAP.put(suffix, new FileType(suffix, archiveFormat));
    }

    private static void add(String suffix, CompressionType compressionType) {
        MAP.put(suffix, new FileType(suffix, compressionType));
    }

    private static void add(String suffix, ArchiveFormat archiveFormat, CompressionType compressionType) {
        MAP.put(suffix, new FileType(suffix, archiveFormat, compressionType));
    }

    /**
     * Checks the suffix of the given string for an entry in the map. If it exists, the corresponding {@link FileType}
     * entry will be returned.
     * 
     * @param filename the filename to check
     * @return a {@link FileType} entry for the file extension of the given name, or null if it does not exist
     */
    public static FileType get(String filename) {
        for (String suffix : MAP.keySet()) {
            if (filename.endsWith(suffix)) {
                return MAP.get(suffix);
            }
        }

        return null;
    }

    /**
     * Checks the suffix of the given {@link File} for an entry in the map. If it exists, the corresponding
     * {@link FileType} entry will be returned.
     * 
     * @param file the file to check
     * @return a {@link FileType} entry for the file extension of the given file, or null if it does not exist
     */
    public static FileType get(File file) {
        return get(file.getName());
    }

}
