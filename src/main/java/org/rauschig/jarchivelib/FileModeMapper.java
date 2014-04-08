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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * Reads *nix file mode flags of commons-compress' ArchiveEntry (where possible) and maps them onto Files on the file
 * system.
 */
abstract class FileModeMapper<T extends ArchiveEntry> {

    public static int PERMISSION_MASK = 0777;

    private T archiveEntry;

    public FileModeMapper(T archiveEntry) {
        this.archiveEntry = archiveEntry;
    }

    public abstract void map(File file) throws IOException;

    public T getArchiveEntry() {
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
     * DefaultFileModeMapper that discretely does nothing.
     * 
     * @param entry the archive entry for which to create a FileModeMapper for
     * @return a new FileModeMapper instance
     */
    public static FileModeMapper<?> create(ArchiveEntry entry) {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            // FIXME: this is really horrid, but with java 6 i need a syscall to 'chmod'
            // TODO: implement basic windows permission mapping (e.g. with File.setX)
            return new DefaultFileModeMapper(entry);
        }

        if (entry instanceof TarArchiveEntry) {
            return new TarModeMapper((TarArchiveEntry) entry);
        } else if (entry instanceof JarArchiveEntry) {
            return new JarModeMapper((JarArchiveEntry) entry);
        } else if (entry instanceof ZipArchiveEntry) {
            return new ZipModeMapper((ZipArchiveEntry) entry);
        } else if (entry instanceof CpioArchiveEntry) {
            return new CpioModeMapper((CpioArchiveEntry) entry);
        }

        return new DefaultFileModeMapper(entry);
    }

    public static class DefaultFileModeMapper extends FileModeMapper<ArchiveEntry> {

        public DefaultFileModeMapper(ArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public void map(File file) {
            // do nothing
        }
    }

    public static abstract class AbstractUnixPermissionMapper<T extends ArchiveEntry> extends FileModeMapper<T> {
        public AbstractUnixPermissionMapper(T archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public void map(File file) throws IOException {
            int perm = getMode() & PERMISSION_MASK;

            if (perm > 0) {
                chmod(perm, file);
            }
        }

        private void chmod(int mode, File file) throws IOException {
            String cmd = String.format("chmod %s %s", Integer.toOctalString(mode), file.getAbsolutePath());

            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                // fail gracefully
            }
        }

        public abstract int getMode();
    }

    public static class TarModeMapper extends AbstractUnixPermissionMapper<TarArchiveEntry> {

        public TarModeMapper(TarArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public int getMode() {
            return getArchiveEntry().getMode();
        }

    }

    public static class ZipModeMapper extends AbstractUnixPermissionMapper<ZipArchiveEntry> {

        public ZipModeMapper(ZipArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public int getMode() {
            return getArchiveEntry().getUnixMode();
        }
    }

    public static class JarModeMapper extends AbstractUnixPermissionMapper<JarArchiveEntry> {

        public JarModeMapper(JarArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public int getMode() {
            return getArchiveEntry().getUnixMode();
        }
    }

    public static class CpioModeMapper extends AbstractUnixPermissionMapper<CpioArchiveEntry> {

        public CpioModeMapper(CpioArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public int getMode() {
            return (int) getArchiveEntry().getMode();
        }
    }

}
