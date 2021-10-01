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

import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/** Denotes an archive format such as zip or tar. */
public enum ArchiveFormat {

  /** Constant used to identify the AR archive format. */
  AR(ArchiveStreamFactory.AR, ".ar"),
  /** Constant used to identify the CPIO archive format. */
  CPIO(ArchiveStreamFactory.CPIO, ".cpio"),
  /** Constant used to identify the Unix DUMP archive format. */
  DUMP(ArchiveStreamFactory.DUMP, ".dump"),
  /** Constant used to identify the JAR archive format. */
  JAR(ArchiveStreamFactory.JAR, ".jar"),
  /** Constant used to identify the 7z archive format. */
  SEVEN_Z(ArchiveStreamFactory.SEVEN_Z, ".7z"),
  /** Constant used to identify the TAR archive format. */
  TAR(ArchiveStreamFactory.TAR, ".tar"),
  /** Constant used to identify the ZIP archive format. */
  ZIP(ArchiveStreamFactory.ZIP, ".zip");

  /** The name by which the compression algorithm is identified. */
  private final String name;

  /** default file extension the archive format is mapped to */
  private final String defaultFileExtension;

  private ArchiveFormat(final String name, final String defaultFileExtension) {
    this.name = name;
    this.defaultFileExtension = defaultFileExtension;
  }

  /**
   * Checks if the given archive format name is valid and known format.
   *
   * @param archiveFormat the archive format name
   * @return true if the given archive format is known to the factory, false otherwise
   */
  public static boolean isValidArchiveFormat(final String archiveFormat) {
    for (final ArchiveFormat format : values()) {
      if (archiveFormat.trim().equalsIgnoreCase(format.getName())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Attempts to return the {@link ArchiveFormat} instance from a possible given string
   * representation. Ignores case.
   *
   * @param archiveFormat string representation of the archive format. E.g. "tar" or "ZIP".
   * @return the compression type enum
   * @throws IllegalArgumentException if the given archive format is unknown.
   */
  public static ArchiveFormat fromString(final String archiveFormat) {
    for (final ArchiveFormat format : values()) {
      if (archiveFormat.trim().equalsIgnoreCase(format.getName())) {
        return format;
      }
    }

    throw new IllegalArgumentException("Unknown archive format " + archiveFormat);
  }

  /**
   * Returns the name by which the archive format is identified.
   *
   * @return the archiver format name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the default file extension for this compression type. E.g. ".gz" for gzip.
   *
   * @return the default file extension preceded by a dot
   */
  public String getDefaultFileExtension() {
    return this.defaultFileExtension;
  }
}
