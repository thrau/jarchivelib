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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests whether permissions are extracted from archives that support them correctly. Just playing around with JUnit's
 * Parameterized feature.
 */
@RunWith(Parameterized.class)
public class ExtractPermissionsTest extends AbstractResourceTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            { ArchiveFormat.TAR, "archive.tar" },
            { ArchiveFormat.ZIP, "archive.zip" }
        });
    }

    private ArchiveFormat archiveFormat;
    private String archiveFileName;

    private Archiver archiver;
    private File archive;

    public ExtractPermissionsTest(ArchiveFormat archiveFormat, String archiveFileName) {
        this.archiveFormat = archiveFormat;
        this.archiveFileName = archiveFileName;
    }

    @Before
    public void setUp() throws Exception {
        archiver = ArchiverFactory.createArchiver(archiveFormat);
        archive = new File(RESOURCES_DIR, archiveFileName);
    }

    @Test
    public void extract_restoresJavaFilePermissions() throws Exception {
        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertJavaPermissions();
    }

    @Test
    public void extract_restoresUnixPermissions() throws Exception {
        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertUnixPermissions();
    }

    @Test
    public void extract_stream_restoresUnixPermissions() throws Exception {
        extractWithStream();
        assertUnixPermissions();
    }

    @Test
    public void extract_stream_restoresJavaPermissions() throws Exception {
        extractWithStream();
        assertJavaPermissions();
    }

    private void extractWithStream() throws IOException {
        ArchiveStream stream = null;
        try {
            stream = archiver.stream(archive);
            ArchiveEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                entry.extract(ARCHIVE_EXTRACT_DIR);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private void assertJavaPermissions() {
        assertPermissions(true, true, true, getExtractedFile("permissions/executable_file.txt"));
        assertPermissions(true, true, true, getExtractedFile("permissions/private_executable_file.txt"));
        assertPermissions(true, false, false, getExtractedFile("permissions/readonly_file.txt"));
        assertPermissions(true, true, true, getExtractedFile("permissions/private_folder"));
        assertPermissions(true, true, false, getExtractedFile("permissions/private_folder/private_file.txt"));
    }

    private void assertUnixPermissions() throws IOException {
        assertEquals("755", getUnixModeOctal(getExtractedFile("permissions/executable_file.txt")));
        assertEquals("700", getUnixModeOctal(getExtractedFile("permissions/private_executable_file.txt")));
        assertEquals("444", getUnixModeOctal(getExtractedFile("permissions/readonly_file.txt")));
        assertEquals("700", getUnixModeOctal(getExtractedFile("permissions/private_folder")));
        assertEquals("600", getUnixModeOctal(getExtractedFile("permissions/private_folder/private_file.txt")));
    }

    private void assertPermissions(boolean r, boolean w, boolean x, File file) {
        assertEquals(r, file.canRead());
        assertEquals(w, file.canWrite());
        assertEquals(x, file.canExecute());
    }

    private String getUnixModeOctal(File file) throws IOException {
        String[] cmd = { "stat", "-c", "%a", file.getAbsolutePath() };
        Process exec = Runtime.getRuntime().exec(cmd);

        try {
            exec.waitFor();
        } catch (InterruptedException e) {
            return null;
        }

        return new Scanner(exec.getInputStream()).nextLine().trim();
    }

    private File getExtractedFile(String name) {
        return new File(ARCHIVE_EXTRACT_DIR, name);
    }

}
