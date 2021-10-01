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

import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveInputStream;

/**
 * {@link ArchiveStream} implementation that wraps a commons compress {@link ArchiveInputStream}.
 */
class CommonsArchiveStream extends ArchiveStream {

  private final ArchiveInputStream stream;

  CommonsArchiveStream(final ArchiveInputStream stream) {
    this.stream = stream;
  }

  @Override
  protected ArchiveEntry createNextEntry() throws IOException {
    final org.apache.commons.compress.archivers.ArchiveEntry next = this.stream.getNextEntry();

    return (next == null) ? null : new CommonsArchiveEntry(this, next);
  }

  @Override
  public int read() throws IOException {
    return this.stream.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return this.stream.read(b);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return this.stream.read(b, off, len);
  }

  @Override
  public void close() throws IOException {
    super.close();
    this.stream.close();
  }
}
