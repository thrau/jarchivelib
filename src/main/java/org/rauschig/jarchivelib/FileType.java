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

import static org.rauschig.jarchivelib.ArchiveFormat.AR;
import static org.rauschig.jarchivelib.ArchiveFormat.CPIO;
import static org.rauschig.jarchivelib.ArchiveFormat.DUMP;
import static org.rauschig.jarchivelib.ArchiveFormat.JAR;
import static org.rauschig.jarchivelib.ArchiveFormat.SEVEN_Z;
import static org.rauschig.jarchivelib.ArchiveFormat.TAR;
import static org.rauschig.jarchivelib.ArchiveFormat.ZIP;
import static org.rauschig.jarchivelib.CompressionType.BZIP2;
import static org.rauschig.jarchivelib.CompressionType.GZIP;
import static org.rauschig.jarchivelib.CompressionType.PACK200;
import static org.rauschig.jarchivelib.CompressionType.XZ;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the file extension as String and the corresponding {@link ArchiveFormat} and/or {@link
 * CompressionType}.
 */
public final class FileType {

  /** Special case object for an unknown archive/compression file type. */
  public static final FileType UNKNOWN = new FileType("", null, null);
  private static final Map<String, FileType> MAP = new LinkedHashMap<>();

  static {
    // compressed archives
    add(".tar.gz", TAR, GZIP);
    add(".tgz", TAR, GZIP);
    add(".tar.bz2", TAR, BZIP2);
    add(".tbz2", TAR, BZIP2);
    add(".tar.xz", TAR, XZ);
    add(".txz", TAR, XZ);
    // archive formats
    add(".7z", SEVEN_Z);
    add(".a", AR);
    add(".ar", AR);
    add(".deb", AR);
    add(".rpm", CPIO);
    add(".cpio", CPIO);
    add(".dump", DUMP);
    add(".jar", JAR);
    add(".tar", TAR);
    add(".zip", ZIP);
    add(".zipx", ZIP);
    // compression formats
    add(".bz2", BZIP2);
    add(".xz", XZ);
    add(".gzip", GZIP);
    add(".gz", GZIP);
    add(".pack", PACK200);
  }

  private final String suffix;
  private final ArchiveFormat archiveFormat;
  private final CompressionType compression;

  private FileType(final String suffix, final ArchiveFormat archiveFormat) {
    this(suffix, archiveFormat, null);
  }

  private FileType(final String suffix, final CompressionType compression) {
    this(suffix, null, compression);
  }

  private FileType(final String suffix, final ArchiveFormat archiveFormat, final CompressionType compression) {
    this.suffix = suffix;
    this.compression = compression;
    this.archiveFormat = archiveFormat;
  }

  /**
   * Checks the suffix of the given string for an entry in the map. If it exists, the corresponding
   * {@link FileType} entry will be returned.
   *
   * @param filename the filename to check
   * @return a {@link FileType} entry for the file extension of the given name, or the UNKNOWN type
   *     if it does not exist
   */
  public static FileType get(final String filename) {
    for (final Map.Entry<String, FileType> entry : MAP.entrySet()) {
      if (filename.toLowerCase().endsWith(entry.getKey())) {
        return entry.getValue();
      }
    }

    return UNKNOWN;
  }

  /**
   * Checks the suffix of the given {@link File} for an entry in the map. If it exists, the
   * corresponding {@link FileType} entry will be returned.
   *
   * @param file the file to check
   * @return a {@link FileType} entry for the file extension of the given file, or the UNKNOWN type
   *     if it does not exist
   */
  public static FileType get(final File file) {
    return get(file.getName());
  }

  private static void add(final String suffix, final ArchiveFormat archiveFormat) {
    MAP.put(suffix, new FileType(suffix, archiveFormat));
  }

  private static void add(final String suffix, final CompressionType compressionType) {
    MAP.put(suffix, new FileType(suffix, compressionType));
  }

  private static void add(
      final String suffix, final ArchiveFormat archiveFormat, final CompressionType compressionType) {
    MAP.put(suffix, new FileType(suffix, archiveFormat, compressionType));
  }

  /**
   * Returns true if the given file extension denotes an archive.
   *
   * @return true if file extension is an archive, false otherwise
   */
  public boolean isArchive() {
    return this.archiveFormat != null;
  }

  /**
   * Returns true if the given file extension denotes a compressed file.
   *
   * @return true if file extension is a compressed type, false otherwise
   */
  public boolean isCompressed() {
    return this.compression != null;
  }

  /**
   * Returns the file extension suffix (e.g. ".zip" or ".tar.gz").
   *
   * @return the file extension suffix
   */
  public String getSuffix() {
    return this.suffix;
  }

  /**
   * Returns the archive format corresponding to this file extension if any.
   *
   * @return the archive format or null if the file extension does not denote an archive
   */
  public ArchiveFormat getArchiveFormat() {
    return this.archiveFormat;
  }

  /**
   * Returns the compression type corresponding to this file extension if any.
   *
   * @return the compression type or null if the file extension does not denote a compressed file
   */
  public CompressionType getCompressionType() {
    return this.compression;
  }

  @Override
  public String toString() {
    return this.getSuffix();
  }
}
