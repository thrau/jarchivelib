/**
 *    Copyright 2017 Kirill Romanov
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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Reads file permissions and maps them (where possible) onto commons-compress' ArchiveEntry mode flags.
 */
public abstract class EntryModeMapper {
    private static final Logger LOG = Logger.getLogger(FileModeMapper.class.getCanonicalName());

    private ArchiveEntry entry;

    private static final Map<PosixFilePermission, Integer> posixPermissionToInteger = new HashMap<>();

    static {
        posixPermissionToInteger.put(PosixFilePermission.OWNER_EXECUTE, 0100);
        posixPermissionToInteger.put(PosixFilePermission.OWNER_WRITE, 0200);
        posixPermissionToInteger.put(PosixFilePermission.OWNER_READ, 0400);

        posixPermissionToInteger.put(PosixFilePermission.GROUP_EXECUTE, 0010);
        posixPermissionToInteger.put(PosixFilePermission.GROUP_WRITE, 0020);
        posixPermissionToInteger.put(PosixFilePermission.GROUP_READ, 0040);

        posixPermissionToInteger.put(PosixFilePermission.OTHERS_EXECUTE, 0001);
        posixPermissionToInteger.put(PosixFilePermission.OTHERS_WRITE, 0002);
        posixPermissionToInteger.put(PosixFilePermission.OTHERS_READ, 0004);
    }

    public EntryModeMapper(ArchiveEntry entry) {
        this.entry = entry;
    }

    public abstract void map(File file) throws IOException;

    public ArchiveEntry getEntry() {
        return entry;
    }

    /**
     * Utility method to create a EntryModeMapper for the given entry, and use it to map the file mode from
     * the given file.
     *
     * @param entry the archive entry to apply the mode onto
     * @param file  the file that holds the mode
     */
    public static void map(ArchiveEntry entry, File file) throws IOException {
        create(entry).map(file);
    }

    /**
     * Factory method for creating a EntryModeMapper for the given ArchiveEntry. Unknown types will yield a
     * FallbackEntryModeMapper that discretely does nothing.
     *
     * @param entry the archive entry for which to create a EntryModeMapper for
     * @return a new EntryModeMapper instance
     */
    public static EntryModeMapper create(ArchiveEntry entry) {
        if (System.getProperty("os.name").toLowerCase().contains("nix") ||
            System.getProperty("os.name").toLowerCase().contains("nux")) {
            if (entry instanceof TarArchiveEntry) {
                return new UnixTarEntryModeMapper((TarArchiveEntry) entry);
            }
        }

        return new FallbackEntryModeMapper(entry);
    }

    /**
     * Does nothing!
     */
    public static class FallbackEntryModeMapper extends EntryModeMapper {
        public FallbackEntryModeMapper(ArchiveEntry entry) {
            super(entry);
        }

        @Override
        public void map(File file) throws IOException {

        }
    }

    /**
     * Uses Files.getPosixFilePermissions to extract file permissions and then set mode to the entry.
     */
    public static class UnixTarEntryModeMapper extends EntryModeMapper {
        public UnixTarEntryModeMapper(TarArchiveEntry entry) {
            super(entry);
        }

        @Override
        public void map(File file) throws IOException {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file.toPath());
            int number = 0;
            for (PosixFilePermission permission : posixPermissionToInteger.keySet()) {
                if (permissions.contains(permission)) {
                    number += posixPermissionToInteger.get(permission);
                }
            }

            int mode = ((TarArchiveEntry) getEntry()).getMode();
            ((TarArchiveEntry) getEntry()).setMode(mode | number);
        }
    }
}
