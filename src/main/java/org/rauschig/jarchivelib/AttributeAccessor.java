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
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

/**
 * Adapter for accessing mode flags from the different types of ArchiveEntries.
 *
 * @param <E> the type of ArchiveEntry
 */
abstract class AttributeAccessor<E extends ArchiveEntry> {

  private final E entry;

  public AttributeAccessor(final E entry) {
    this.entry = entry;
  }

  /**
   * Detects the type of the given ArchiveEntry and returns an appropriate AttributeAccessor for it.
   *
   * @param entry the adaptee
   * @return a new attribute accessor instance
   */
  public static AttributeAccessor<?> create(final ArchiveEntry entry) {
    if (entry instanceof TarArchiveEntry) {
      return new TarAttributeAccessor((TarArchiveEntry) entry);
    } else if (entry instanceof ZipArchiveEntry) {
      return new ZipAttributeAccessor((ZipArchiveEntry) entry);
    } else if (entry instanceof CpioArchiveEntry) {
      return new CpioAttributeAccessor((CpioArchiveEntry) entry);
    } else if (entry instanceof ArjArchiveEntry) {
      return new ArjAttributeAccessor((ArjArchiveEntry) entry);
    } else if (entry instanceof ArArchiveEntry) {
      return new ArAttributeAccessor((ArArchiveEntry) entry);
    }

    return new FallbackAttributeAccessor(entry);
  }

  /**
   * Returns the entry that is being accessed
   *
   * @return the entry
   */
  public E getEntry() {
    return this.entry;
  }

  /**
   * Returns the unix file mode.
   *
   * @return unix file mode flags
   * @throws java.io.IOException
   */
  public abstract int getMode() throws IOException;

  public static class FallbackAttributeAccessor extends AttributeAccessor<ArchiveEntry> {
    protected FallbackAttributeAccessor(final ArchiveEntry entry) {
      super(entry);
    }

    @Override
    public int getMode() {
      return 0;
    }
  }

  public static class TarAttributeAccessor extends AttributeAccessor<TarArchiveEntry> {
    public TarAttributeAccessor(final TarArchiveEntry entry) {
      super(entry);
    }

    @Override
    public int getMode() {
      return this.getEntry().getMode();
    }
  }

  public static class ZipAttributeAccessor extends AttributeAccessor<ZipArchiveEntry> {
    public ZipAttributeAccessor(final ZipArchiveEntry entry) {
      super(entry);
    }

    @Override
    public int getMode() {
      return this.getEntry().getUnixMode();
    }
  }

  public static class CpioAttributeAccessor extends AttributeAccessor<CpioArchiveEntry> {
    public CpioAttributeAccessor(final CpioArchiveEntry entry) {
      super(entry);
    }

    @Override
    public int getMode() {
      return (int) this.getEntry().getMode();
    }
  }

  public static class ArjAttributeAccessor extends AttributeAccessor<ArjArchiveEntry> {
    public ArjAttributeAccessor(final ArjArchiveEntry entry) {
      super(entry);
    }

    @Override
    public int getMode() throws IOException {
      return this.getEntry().getMode();
    }
  }

  public static class ArAttributeAccessor extends AttributeAccessor<ArArchiveEntry> {
    public ArAttributeAccessor(final ArArchiveEntry entry) {
      super(entry);
    }

    @Override
    public int getMode() throws IOException {
      return this.getEntry().getMode();
    }
  }
}
