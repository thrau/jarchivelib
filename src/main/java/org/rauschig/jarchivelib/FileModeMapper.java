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
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;

/**
 * Reads *nix file mode flags of commons-compress' ArchiveEntry (where possible) and maps them onto Files on the file
 * system.
 */
abstract class FileModeMapper {

    private static final Logger LOG = Logger.getLogger(FileModeMapper.class.getCanonicalName());

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
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            // FIXME: this is really horrid, but with java 6 i need the system call to 'chmod'
            // TODO: implement basic windows permission mapping (e.g. with File.setX or attrib)
            return new FallbackFileModeMapper(entry);
        }

        // please don't use me on OS/2
        return new UnixPermissionMapper(entry);
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
     * Uses an AttributeAccessor to extract the unix file mode from the ArchiveEntry and then invokes the ChmodCommand
     * on the given file.
     */
    public static class UnixPermissionMapper extends FileModeMapper {
        public static final int UNIX_PERMISSION_MASK = 0777;

        public UnixPermissionMapper(ArchiveEntry archiveEntry) {
            super(archiveEntry);
        }

        @Override
        public void map(File file) throws IOException {
            int perm = getMode() & UNIX_PERMISSION_MASK;

            if (perm > 0) {
                chmod(perm, file);
            }
        }

        public int getMode() throws IOException {
            return AttributeAccessor.create(getArchiveEntry()).getMode();
        }

        public ChmodCommand getChmodCommand() {
            return new FileSystemPreferencesReflectionChmodCommand();
        }

        private void chmod(int mode, File file) throws IOException {
            try {
                getChmodCommand().chmod(mode, file);
            } catch (Exception e) {
                LOG.warning("Could not set file permissions of " + file + ". Exception was: " + e.getMessage());
            }
        }

    }

    /**
     * Command interface for unix <code>chmod</code> call. Java 6 made me do it.
     */
    public static interface ChmodCommand {
        void chmod(int mode, File file) throws Exception;
    }

    /**
     * While still horribly wrong, this actually seems to be the safest way. It will invoke a reflective call on
     * java.utils.pref.FileSystemPreferences#chmod(String, Integer), which is a JNI call, making it (probably) the
     * safest bet.
     */
    public static class FileSystemPreferencesReflectionChmodCommand implements ChmodCommand {
        private static Method method;

        @Override
        public void chmod(int mode, File file) throws Exception {
            getMethod().invoke(null, file.getAbsolutePath(), mode);
        }

        private Method getMethod() throws Exception {
            if (method == null) {
                Class<?> clazz = Class.forName("java.util.prefs.FileSystemPreferences");
                method = clazz.getDeclaredMethod("chmod", String.class, Integer.TYPE);
                method.setAccessible(true);
            }

            return method;
        }
    }

    /**
     * This is just here for documentation really. Maybe it could be an alternative in some cases.
     */
    public static class RuntimeExecChmodCommand implements ChmodCommand {
        @Override
        public void chmod(int mode, File file) throws Exception {
            String cmd = "chmod " + Integer.toOctalString(mode) + " " + file.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        }
    }

}
