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