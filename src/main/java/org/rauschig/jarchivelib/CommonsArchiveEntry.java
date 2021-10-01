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
import java.util.Date;

/**
 * Implementation of an {@link ArchiveEntry} that wraps the commons compress version of the same
 * type.
 */
class CommonsArchiveEntry implements ArchiveEntry {

  /** The wrapped {@code ArchiveEntry} entry. */
  private final org.apache.commons.compress.archivers.ArchiveEntry entry;

  /** The {@link ArchiveStream} this entry belongs to. */
  private final ArchiveStream stream;

  CommonsArchiveEntry(
      final ArchiveStream stream, final org.apache.commons.compress.archivers.ArchiveEntry entry) {
    this.stream = stream;
    this.entry = entry;
  }

  @Override
  public String getName() {
    this.assertState();
    return this.entry.getName();
  }

  @Override
  public long getSize() {
    this.assertState();
    return this.entry.getSize();
  }

  @Override
  public Date getLastModifiedDate() {
    this.assertState();
    return this.entry.getLastModifiedDate();
  }

  @Override
  public boolean isDirectory() {
    this.assertState();
    return this.entry.isDirectory();
  }

  @Override
  public File extract(final File destination)
      throws IOException, IllegalStateException, IllegalArgumentException {
    this.assertState();
    IOUtils.requireDirectory(destination);

    final File file = new File(destination, this.entry.getName());

    if (this.entry.isDirectory()) {
      file.mkdirs();
    } else {
      file.getParentFile().mkdirs();
      IOUtils.copy(this.stream, file);
    }

    FileModeMapper.map(this.entry, file);

    return file;
  }

  private void assertState() {
    if (this.stream.isClosed()) {
      throw new IllegalStateException("Stream has already been closed");
    }
    if (this != this.stream.getCurrentEntry()) {
      throw new IllegalStateException("Illegal stream pointer");
    }
  }
}
