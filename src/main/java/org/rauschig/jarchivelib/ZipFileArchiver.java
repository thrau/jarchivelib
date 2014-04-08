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
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * Archiver that overwrites the extraction of Zip archives. It provides a wrapper for ZipFile as an ArchiveInputStream
 * to retrieve file attributes properly.
 */
class ZipFileArchiver extends CommonsArchiver {

    ZipFileArchiver() {
        super(ArchiveFormat.ZIP);
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(File archive) throws IOException {
        return new ZipFileArchiveInputStream(new ZipFile(archive));
    }

    /**
     * Wraps a ZipFile to make it usable as an ArchiveInputStream.
     */
    static class ZipFileArchiveInputStream extends ArchiveInputStream {

        private ZipFile file;

        private Enumeration<ZipArchiveEntry> entries;
        private ZipArchiveEntry currentEntry;
        private InputStream currentEntryStream;

        public ZipFileArchiveInputStream(ZipFile file) {
            this.file = file;
        }

        @Override
        public ZipArchiveEntry getNextEntry() throws IOException {
            Enumeration<ZipArchiveEntry> entries = getEntries();

            closeCurrentEntryStream();

            currentEntry = (entries.hasMoreElements()) ? entries.nextElement() : null;
            currentEntryStream = (currentEntry != null) ? file.getInputStream(currentEntry) : null;

            return currentEntry;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read = getCurrentEntryStream().read(b, off, len);

            if (read == -1) {
                IOUtils.closeQuietly(getCurrentEntryStream());
            }

            count(read);

            return read;
        }

        @Override
        public boolean canReadEntryData(ArchiveEntry archiveEntry) {
            return archiveEntry == getCurrentEntry();
        }

        public ZipArchiveEntry getCurrentEntry() {
            return currentEntry;
        }

        public InputStream getCurrentEntryStream() {
            return currentEntryStream;
        }

        private Enumeration<ZipArchiveEntry> getEntries() {
            if (entries == null) {
                entries = file.getEntriesInPhysicalOrder();
            }
            return entries;
        }

        private void closeCurrentEntryStream() {
            InputStream stream = getCurrentEntryStream();
            IOUtils.closeQuietly(stream);

            currentEntryStream = null;
        }

        private void closeFile() {
            try {
                file.close();
            } catch (IOException e) {
                // close quietly
            }
        }

        @Override
        public void close() throws IOException {
            closeCurrentEntryStream();
            closeFile();

            super.close();
        }
    }
}
