/**
 * Copyright 2013 Thomas Rausch
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
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
 * Archiver that overwrites the extraction of Zip archives. It provides a wrapper for ZipFile as an
 * ArchiveInputStream to retrieve file attributes properly.
 */
class ZipFileArchiver extends CommonsArchiver {

  ZipFileArchiver() {
    super(ArchiveFormat.ZIP);
  }

  @Override
  protected ArchiveInputStream createArchiveInputStream(final File archive) throws IOException {
    return new ZipFileArchiveInputStream(new ZipFile(archive));
  }

  /** Wraps a ZipFile to make it usable as an ArchiveInputStream. */
  static class ZipFileArchiveInputStream extends ArchiveInputStream {

    private final ZipFile file;

    private Enumeration<ZipArchiveEntry> entries;
    private ZipArchiveEntry currentEntry;
    private InputStream currentEntryStream;

    public ZipFileArchiveInputStream(final ZipFile file) {
      this.file = file;
    }

    @Override
    public ZipArchiveEntry getNextEntry() throws IOException {
      final Enumeration<ZipArchiveEntry> entries = this.getEntries();

        this.closeCurrentEntryStream();

        this.currentEntry = (entries.hasMoreElements()) ? entries.nextElement() : null;
        this.currentEntryStream = (this.currentEntry != null) ? this.file.getInputStream(
            this.currentEntry) : null;

      return this.currentEntry;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
      final int read = this.getCurrentEntryStream().read(b, off, len);

      if (read == -1) {
        IOUtils.closeQuietly(this.getCurrentEntryStream());
      }

        this.count(read);

      return read;
    }

    @Override
    public boolean canReadEntryData(final ArchiveEntry archiveEntry) {
      return archiveEntry == this.getCurrentEntry();
    }

    public ZipArchiveEntry getCurrentEntry() {
      return this.currentEntry;
    }

    public InputStream getCurrentEntryStream() {
      return this.currentEntryStream;
    }

    private Enumeration<ZipArchiveEntry> getEntries() {
      if (this.entries == null) {
          this.entries = this.file.getEntriesInPhysicalOrder();
      }
      return this.entries;
    }

    private void closeCurrentEntryStream() {
      final InputStream stream = this.getCurrentEntryStream();
      IOUtils.closeQuietly(stream);

        this.currentEntryStream = null;
    }

    private void closeFile() {
      try {
          this.file.close();
      } catch (final IOException e) {
        // close quietly
      }
    }

    @Override
    public void close() throws IOException {
        this.closeCurrentEntryStream();
        this.closeFile();

      super.close();
    }
  }
}
