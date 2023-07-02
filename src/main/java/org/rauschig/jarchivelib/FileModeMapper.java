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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;

/**
 * Reads *nix file mode flags of commons-compress' ArchiveEntry (where possible) and maps them onto Files on the file
 * system.
 */
abstract class FileModeMapper {

    private static final Logger LOG = Logger.getLogger(FileModeMapper.class.getCanonicalName());
    private static boolean IS_POSIX = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

    private ArchiveEntry archiveEntry;

    public FileModeMapper(ArchiveEntry archiveEntry) {
        this.archiveEntry = archiveEntry;
    }

    public abstract void map(File file) throws IOException;

    public ArchiveEntry getArchiveEntry() {
        return archiveEntry;
    }

    /**
     * Utility method to create a FileModeMapper for the given entry, and use it to map the file mode onto the given
     * file.
     * 
     * @param entry the archive entry that holds the mode
     * @param file the file to apply the mode onto
     */
    public static void map(ArchiveEntry entry, File file) throws IOException {
        create(entry).map(file);
    }

    /**
     * Factory method for creating a FileModeMapper for the given ArchiveEntry. Unknown types will yield a
     * FallbackFileModeMapper that discretely does nothing.
     * 
     * @param entry the archive entry for which to create a FileModeMapper for
     * @return a new FileModeMapper instance
     */
    public static FileModeMapper create(ArchiveEntry entry) {
        if (IS_POSIX) {
            return new PosixPermissionMapper(entry);
        }

        // TODO: implement basic windows permission mapping (e.g. with File.setX or attrib)
        return new FallbackFileModeMapper(entry);
    }

    /**
     * Does nothing!
     */
    public static class FallbackFileModeMapper extends FileModeMapper {

        public FallbackFileModeMapper(ArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public void map(File file) throws IOException {
            // do nothing
        }
    }

    /**
     * Uses an AttributeAccessor to extract the posix file permissions from the ArchiveEntry and sets them
     * on the given file.
     */
    public static class PosixPermissionMapper extends FileModeMapper {
        public static final int UNIX_PERMISSION_MASK = 0777;

        public PosixPermissionMapper(ArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public void map(File file) throws IOException {
            int mode = getMode() & UNIX_PERMISSION_MASK;

            if (mode > 0) {
                setPermissions(mode, file);
            }
        }

        public int getMode() throws IOException {
            return AttributeAccessor.create(getArchiveEntry()).getMode();
        }

        private void setPermissions(int mode, File file) {
            try {
                Set<PosixFilePermission> posixFilePermissions = new PosixFilePermissionsMapper().map(mode);
                Files.setPosixFilePermissions(file.toPath(), posixFilePermissions);
            } catch (Exception e) {
                LOG.warning("Could not set file permissions of " + file + ". Exception was: " + e.getMessage());
            }
        }
    }

    public static class PosixFilePermissionsMapper {

        public static Map<Integer, PosixFilePermission> intToPosixFilePermission = new HashMap<>();

        static {
            intToPosixFilePermission.put(0400, PosixFilePermission.OWNER_READ);
            intToPosixFilePermission.put(0200, PosixFilePermission.OWNER_WRITE);
            intToPosixFilePermission.put(0100, PosixFilePermission.OWNER_EXECUTE);

            intToPosixFilePermission.put(0040, PosixFilePermission.GROUP_READ);
            intToPosixFilePermission.put(0020, PosixFilePermission.GROUP_WRITE);
            intToPosixFilePermission.put(0010, PosixFilePermission.GROUP_EXECUTE);

            intToPosixFilePermission.put(0004, PosixFilePermission.OTHERS_READ);
            intToPosixFilePermission.put(0002, PosixFilePermission.OTHERS_WRITE);
            intToPosixFilePermission.put(0001, PosixFilePermission.OTHERS_EXECUTE);
        }

        public Set<PosixFilePermission> map(int mode) {
            Set<PosixFilePermission> permissionSet = new HashSet<>();
            for (Map.Entry<Integer, PosixFilePermission> entry : intToPosixFilePermission.entrySet()) {
                if ((mode & entry.getKey()) > 0) {
                     permissionSet.add(entry.getValue());
                }
            }
            return permissionSet;
        }
    }

}
