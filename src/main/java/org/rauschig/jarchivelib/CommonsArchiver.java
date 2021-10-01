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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * Implementation of an {@link Archiver} that uses {@link ArchiveStreamFactory} to generate archive
 * streams by a given archiver name passed when creating the {@code GenericArchiver}. Thus, it can
 * be used for all archive formats the {@code org.apache.commons.compress} library supports.
 */
class CommonsArchiver implements Archiver {

  private final ArchiveFormat archiveFormat;

  CommonsArchiver(final ArchiveFormat archiveFormat) {
    this.archiveFormat = archiveFormat;
  }

  public ArchiveFormat getArchiveFormat() {
    return this.archiveFormat;
  }

  @Override
  public File create(final String archive, final File destination, final File source) throws IOException {
    return this.create(archive, destination, IOUtils.filesContainedIn(source));
  }

  @Override
  public File create(final String archive, final File destination, final File... sources) throws IOException {

    IOUtils.requireDirectory(destination);

    final File archiveFile = this.createNewArchiveFile(archive, this.getFilenameExtension(), destination);

    ArchiveOutputStream outputStream = null;
    try {
      outputStream = this.createArchiveOutputStream(archiveFile);
      this.writeToArchive(sources, outputStream);

      outputStream.flush();
    } finally {
      IOUtils.closeQuietly(outputStream);
    }

    return archiveFile;
  }

  @Override
  public void extract(final File archive, final File destination) throws IOException {
    this.assertExtractSource(archive);

    IOUtils.requireDirectory(destination);

    ArchiveInputStream input = null;
    try {
      input = this.createArchiveInputStream(archive);
      this.extract(input, destination);

    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  @Override
  public void extract(final InputStream archive, final File destination) throws IOException {
    final ArchiveInputStream input = this.createArchiveInputStream(archive);
    this.extract(input, destination);
  }

  private void extract(final ArchiveInputStream input, final File destination) throws IOException {
    ArchiveEntry entry;
    while ((entry = input.getNextEntry()) != null) {
      final File file = new File(destination, entry.getName());

      if (entry.isDirectory()) {
        file.mkdirs();
      } else {
        file.getParentFile().mkdirs();
        IOUtils.copy(input, file);
      }

      FileModeMapper.map(entry, file);
    }
  }

  @Override
  public ArchiveStream stream(final File archive) throws IOException {
    return new CommonsArchiveStream(this.createArchiveInputStream(archive));
  }

  @Override
  public String getFilenameExtension() {
    return this.getArchiveFormat().getDefaultFileExtension();
  }

  /**
   * Returns a new ArchiveInputStream for reading archives. Subclasses can override this to return
   * their own custom implementation.
   *
   * @param archive the archive file to stream from
   * @return a new ArchiveInputStream for the given archive file
   * @throws IOException propagated IO exceptions
   */
  protected ArchiveInputStream createArchiveInputStream(final File archive) throws IOException {
    try {
      return CommonsStreamFactory.createArchiveInputStream(archive);
    } catch (final ArchiveException e) {
      throw new IOException(e);
    }
  }

  /**
   * Returns a new ArchiveInputStream for reading archives. Subclasses can override this to return
   * their own custom implementation.
   *
   * @param archive the archive contents to stream from
   * @return a new ArchiveInputStream for the given archive file
   * @throws IOException propagated IO exceptions
   */
  protected ArchiveInputStream createArchiveInputStream(final InputStream archive) throws IOException {
    try {
      return CommonsStreamFactory.createArchiveInputStream(archive);
    } catch (final ArchiveException e) {
      throw new IOException(e);
    }
  }

  /**
   * Returns a new ArchiveOutputStream for creating archives. Subclasses can override this to return
   * their own custom implementation.
   *
   * @param archiveFile the archive file to stream to
   * @return a new ArchiveOutputStream for the given archive file.
   * @throws IOException propagated IO exceptions
   */
  protected ArchiveOutputStream createArchiveOutputStream(final File archiveFile) throws IOException {
    try {
      final ArchiveOutputStream archiveOutputStream =
          CommonsStreamFactory.createArchiveOutputStream(this, archiveFile);

      if (archiveOutputStream instanceof TarArchiveOutputStream) {
        ((TarArchiveOutputStream) archiveOutputStream)
            .setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
      }

      return archiveOutputStream;
    } catch (final ArchiveException e) {
      throw new IOException(e);
    }
  }

  /**
   * Asserts that the given File object is a readable file that can be used to extract from.
   *
   * @param archive the file to check
   * @throws FileNotFoundException if the file does not exist
   * @throws IllegalArgumentException if the file is a directory or not readable
   */
  protected void assertExtractSource(final File archive)
      throws FileNotFoundException, IllegalArgumentException {
    if (archive.isDirectory()) {
      throw new IllegalArgumentException("Can not extract " + archive + ". Source is a directory.");
    } else if (!archive.exists()) {
      throw new FileNotFoundException(archive.getPath());
    } else if (!archive.canRead()) {
      throw new IllegalArgumentException(
          "Can not extract " + archive + ". Can not read from source.");
    }
  }

  /**
   * Creates a new File in the given destination. The resulting name will always be
   * "archive"."fileExtension". If the archive name parameter already ends with the given file name
   * extension, it is not additionally appended.
   *
   * @param archive the name of the archive
   * @param extension the file extension (e.g. ".tar")
   * @param destination the parent path
   * @return the newly created file
   * @throws IOException if an I/O error occurred while creating the file
   */
  protected File createNewArchiveFile(String archive, final String extension, final File destination)
      throws IOException {
    if (!archive.endsWith(extension)) {
      archive += extension;
    }

    final File file = new File(destination, archive);
    file.createNewFile();

    return file;
  }

  /**
   * Recursion entry point for {@link #writeToArchive(File, File[], ArchiveOutputStream)}. <br>
   * Recursively writes all given source {@link File}s into the given {@link ArchiveOutputStream}.
   *
   * @param sources the files to write in to the archive
   * @param archive the archive to write into
   * @throws IOException when an I/O error occurs
   */
  protected void writeToArchive(final File[] sources, final ArchiveOutputStream archive) throws IOException {
    for (final File source : sources) {
      if (!source.exists()) {
        throw new FileNotFoundException(source.getPath());
      } else if (!source.canRead()) {
        throw new FileNotFoundException(source.getPath() + " (Permission denied)");
      }

      this.writeToArchive(source.getParentFile(), new File[] {source}, archive);
    }
  }

  /**
   * Recursively writes all given source {@link File}s into the given {@link ArchiveOutputStream}.
   * The paths of the sources in the archive will be relative to the given parent {@code File}.
   *
   * @param parent the parent file node for computing a relative path (see {@link
   *     IOUtils#relativePath(File, File)})
   * @param sources the files to write in to the archive
   * @param archive the archive to write into
   * @throws IOException when an I/O error occurs
   */
  protected void writeToArchive(final File parent, final File[] sources, final ArchiveOutputStream archive)
      throws IOException {
    for (final File source : sources) {
      final String relativePath = IOUtils.relativePath(parent, source);

      this.createArchiveEntry(source, relativePath, archive);

      if (source.isDirectory()) {
        this.writeToArchive(parent, source.listFiles(), archive);
      }
    }
  }

  /**
   * Creates a new {@link ArchiveEntry} in the given {@link ArchiveOutputStream}, and copies the
   * given {@link File} into the new entry.
   *
   * @param file the file to add to the archive
   * @param entryName the name of the archive entry
   * @param archive the archive to write to
   * @throws IOException when an I/O error occurs during FileInputStream creation or during copying
   */
  protected void createArchiveEntry(
      final File file, final String entryName, final ArchiveOutputStream archive)
      throws IOException {
    final ArchiveEntry entry = archive.createArchiveEntry(file, entryName);
    // TODO #23: read permission from file, write it to the ArchiveEntry
    archive.putArchiveEntry(entry);

    if (!entry.isDirectory()) {
      FileInputStream input = null;
      try {
        input = new FileInputStream(file);
        IOUtils.copy(input, archive);
      } finally {
        IOUtils.closeQuietly(input);
      }
    }

    archive.closeArchiveEntry();
  }
}
