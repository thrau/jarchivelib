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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests whether permissions are extracted from archives that support them correctly. Just playing
 * around with JUnit's Parameterized feature.
 */
@RunWith(Parameterized.class)
public class ExtractPermissionsTest extends AbstractResourceTest {

  private final ArchiveFormat archiveFormat;
  private final String archiveFileName;
  private Archiver archiver;
  private File archive;
  public ExtractPermissionsTest(final ArchiveFormat archiveFormat, final String archiveFileName) {
    this.archiveFormat = archiveFormat;
    this.archiveFileName = archiveFileName;
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> parameters() {
    return Arrays.asList(
        new Object[][] {
          {ArchiveFormat.TAR, "archive.tar"},
          {ArchiveFormat.ZIP, "archive.zip"}
        });
  }

  @Before
  public void setUp() throws Exception {
    this.archiver = ArchiverFactory.createArchiver(this.archiveFormat);
    this.archive = new File(RESOURCES_DIR, this.archiveFileName);
  }

  @Test
  public void extract_restoresJavaFilePermissions() throws Exception {
    this.archiver.extract(this.archive, ARCHIVE_EXTRACT_DIR);
    this.assertJavaPermissions();
  }

  @Test
  public void extract_restoresUnixPermissions() throws Exception {
    this.archiver.extract(this.archive, ARCHIVE_EXTRACT_DIR);
    this.assertPosixPermissions();
  }

  @Test
  public void extract_stream_restoresUnixPermissions() throws Exception {
    this.extractWithStream();
    this.assertPosixPermissions();
  }

  @Test
  public void extract_stream_restoresJavaPermissions() throws Exception {
    this.extractWithStream();
    this.assertJavaPermissions();
  }

  private void extractWithStream() throws IOException {
    ArchiveStream stream = null;
    try {
      stream = this.archiver.stream(this.archive);
      ArchiveEntry entry;
      while ((entry = stream.getNextEntry()) != null) {
        entry.extract(ARCHIVE_EXTRACT_DIR);
      }
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  private void assertJavaPermissions() {
    this.assertPermissions(true, true, true, this.getExtractedFile("permissions/executable_file.txt"));
    this.assertPermissions(
        true, true, true, this.getExtractedFile("permissions/private_executable_file.txt"));
    this.assertPermissions(true, false, false, this.getExtractedFile("permissions/readonly_file.txt"));
    this.assertPermissions(true, true, true, this.getExtractedFile("permissions/private_folder"));
    this.assertPermissions(
        true, true, false, this.getExtractedFile("permissions/private_folder/private_file.txt"));
  }

  private void assertPosixPermissions() throws IOException {
    assertEquals(
        "rwxr-xr-x",
        this.getPosixPermissionsString(this.getExtractedFile("permissions/executable_file.txt")));
    assertEquals(
        "rwx------",
        this.getPosixPermissionsString(this.getExtractedFile("permissions/private_executable_file.txt")));
    assertEquals(
        "r--r--r--", this.getPosixPermissionsString(this.getExtractedFile("permissions/readonly_file.txt")));
    assertEquals(
        "rwx------", this.getPosixPermissionsString(this.getExtractedFile("permissions/private_folder")));
    assertEquals(
        "rw-------",
        this.getPosixPermissionsString(this.getExtractedFile("permissions/private_folder/private_file.txt")));
  }

  private void assertPermissions(final boolean r, final boolean w, final boolean x, final File file) {
    assertEquals(r, file.canRead());
    assertEquals(w, file.canWrite());
    assertEquals(x, file.canExecute());
  }

  private String getPosixPermissionsString(final File file) throws IOException {
    return PosixFilePermissions.toString(Files.getPosixFilePermissions(file.toPath()));
  }

  private File getExtractedFile(final String name) {
    return new File(ARCHIVE_EXTRACT_DIR, name);
  }
}
