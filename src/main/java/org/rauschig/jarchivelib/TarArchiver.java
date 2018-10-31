package org.rauschig.jarchivelib;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

/**
 * Archiver to handle tar archives.
 * <p>
 * Created by masc on 31.10.18.
 */
public class TarArchiver extends CommonsArchiver {
    private static final int DEFAULT_EXECUTABLE_FILE_MODE = 0100755;

    TarArchiver() {
        super(ArchiveFormat.TAR);
    }

    @Override
    protected void processArchiveEntry(ArchiveEntry entry) {
        if (entry instanceof TarArchiveEntry) {

            TarArchiveEntry tarEntry = (TarArchiveEntry) entry;

            // Preserve executability of files
            if (tarEntry.isFile() && tarEntry.getFile().canExecute())
                tarEntry.setMode(DEFAULT_EXECUTABLE_FILE_MODE);
        }
    }
}