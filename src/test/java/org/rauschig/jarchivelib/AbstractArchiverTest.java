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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractArchiverTest extends AbstractResourceTest {

    private Archiver archiver;

    private File archive;

    @Before
    public void setUp() {
        archiver = getArchiver();
        archive = getArchive();
    }

    @After
    public void tearDown() {
        archiver = null;
        archive = null;
    }

    protected abstract Archiver getArchiver();

    protected abstract File getArchive();

    @Test
    public void extract_properlyExtractsArchive() throws Exception {
        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);

        assertExtractionWasSuccessful();
    }

    @Test
    public void extract_properlyExtractsArchiveStream() throws Exception {
        InputStream archiveAsStream = null;
        try {
            archiveAsStream = new FileInputStream(archive);
            archiver.extract(archiveAsStream, ARCHIVE_EXTRACT_DIR);
            assertExtractionWasSuccessful();
        } finally {
            IOUtils.closeQuietly(archiveAsStream);
        }
    }

    @Test
    public void create_recursiveDirectory_withFileExtension_properlyCreatesArchive() throws Exception {
        String archiveName = archive.getName();

        File createdArchive = archiver.create(archiveName, ARCHIVE_CREATE_DIR, ARCHIVE_DIR);

        assertTrue(createdArchive.exists());
        assertEquals(archiveName, createdArchive.getName());

        archiver.extract(createdArchive, ARCHIVE_EXTRACT_DIR);
        assertExtractionWasSuccessful();
    }

    @Test
    public void create_multipleSourceFiles_properlyCreatesArchive() throws Exception {
        String archiveName = archive.getName();

        File createdArchive = archiver.create(archiveName, ARCHIVE_CREATE_DIR, ARCHIVE_DIR.listFiles());

        assertTrue(createdArchive.exists());
        assertEquals(archiveName, createdArchive.getName());

        archiver.extract(createdArchive, ARCHIVE_EXTRACT_DIR);
        assertDirectoryStructureEquals(ARCHIVE_DIR, ARCHIVE_EXTRACT_DIR);
    }

    @Test
    public void create_recursiveDirectory_withoutFileExtension_properlyCreatesArchive() throws Exception {
        String archiveName = archive.getName();

        File archive = archiver.create("archive", ARCHIVE_CREATE_DIR, ARCHIVE_DIR);

        assertTrue(archive.exists());
        assertEquals(archiveName, archive.getName());

        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertExtractionWasSuccessful();
    }

    @Test(expected = FileNotFoundException.class)
    public void create_withNonExistingSource_fails() throws Exception {
        archiver.create("archive", ARCHIVE_CREATE_DIR, NON_EXISTING_FILE);
    }

    @Test(expected = FileNotFoundException.class)
    public void create_withNonReadableSource_fails() throws Exception {
        archiver.create("archive", ARCHIVE_CREATE_DIR, NON_READABLE_FILE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_withFileAsDestination_fails() throws Exception {
        archiver.create("archive", NON_READABLE_FILE, ARCHIVE_DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_withNonWritableDestination_fails() throws Exception {
        archiver.create("archive", NON_WRITABLE_DIR, ARCHIVE_DIR);
    }

    @Test(expected = FileNotFoundException.class)
    public void extract_withNonExistingSource_fails() throws Exception {
        archiver.extract(NON_EXISTING_FILE, ARCHIVE_EXTRACT_DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void extract_withNonReadableSource_fails() throws Exception {
        archiver.extract(NON_READABLE_FILE, ARCHIVE_EXTRACT_DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void extract_withFileAsDestination_fails() throws Exception {
        archiver.extract(archive, NON_READABLE_FILE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void extract_withNonWritableDestination_fails() throws Exception {
        archiver.extract(archive, NON_WRITABLE_DIR);
    }

    @Test
    public void stream_returnsCorrectEntries() throws IOException {
        ArchiveStream stream = null;
        try {
            stream = archiver.stream(archive);
            ArchiveEntry entry;
            List<String> entries = new ArrayList<String>();

            while ((entry = stream.getNextEntry()) != null) {
                entries.add(entry.getName().replaceAll("/$", "")); // remove trailing slashes for test compatibility
            }

            assertEquals(12, entries.size());
            assertTrue(entries.contains("file.txt"));
            assertTrue(entries.contains("looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong_filename.txt"));
            assertTrue(entries.contains("folder"));
            assertTrue(entries.contains("folder/folder_file.txt"));
            assertTrue(entries.contains("folder/subfolder/subfolder_file.txt"));
            assertTrue(entries.contains("folder/subfolder"));
            assertTrue(entries.contains("permissions"));
            assertTrue(entries.contains("permissions/executable_file.txt"));
            assertTrue(entries.contains("permissions/private_executable_file.txt"));
            assertTrue(entries.contains("permissions/readonly_file.txt"));
            assertTrue(entries.contains("permissions/private_folder"));
            assertTrue(entries.contains("permissions/private_folder/private_file.txt"));
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test
    public void entry_isDirectory_behavesCorrectly() throws Exception {
        ArchiveStream stream = null;
        try {
            stream = archiver.stream(archive);
            ArchiveEntry entry;

            while ((entry = stream.getNextEntry()) != null) {
                String name = entry.getName().replaceAll("/$", ""); // remove trailing slashes for test compatibility

                if (name.endsWith("folder") || name.endsWith("subfolder") || name.endsWith("permissions")
                        || name.endsWith("private_folder")) {
                    assertTrue(entry.getName() + " is a directory", entry.isDirectory());
                } else {
                    assertFalse(entry.getName() + " is not a directory", entry.isDirectory());
                }
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test
    public void entry_geSize_behavesCorrectly() throws Exception {
        ArchiveStream stream = null;
        try {
            stream = archiver.stream(archive);
            ArchiveEntry entry;

            while ((entry = stream.getNextEntry()) != null) {
                String name = entry.getName().replaceAll("/$", ""); // remove trailing slashes for test compatibility

                if (name.endsWith("folder") || name.endsWith("subfolder") || name.endsWith("permissions")
                        || name.endsWith("private_folder")) {
                    assertEquals(0, entry.getSize());
                } else {
                    assertNotEquals(0, entry.getSize());
                }
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test
    public void entry_getLastModifiedDate_behavesCorrectly() throws Exception {
        ArchiveStream stream = null;
        try {
            stream = archiver.stream(archive);
            ArchiveEntry entry;

            while ((entry = stream.getNextEntry()) != null) {
                assertNotNull(entry.getLastModifiedDate());
                assertTrue("modification date should be before now", new Date().after(entry.getLastModifiedDate()));
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test
    public void stream_extractEveryEntryWorks() throws Exception {
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

        assertExtractionWasSuccessful();
    }

    @Test(expected = IllegalStateException.class)
    public void stream_extractPassedEntry_throwsException() throws Exception {
        ArchiveStream stream = null;
        try {
            stream = archiver.stream(archive);
            ArchiveEntry entry = null;

            try {
                entry = stream.getNextEntry();
                stream.getNextEntry();
            } catch (IllegalStateException e) {
                fail("Illegal state exception caugth to early");
            }

            entry.extract(ARCHIVE_EXTRACT_DIR);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void stream_extractOnClosedStream_throwsException() throws Exception {
        ArchiveEntry entry = null;
        ArchiveStream stream = null;

        try {
            stream = archiver.stream(archive);
            entry = stream.getNextEntry();
        } catch (IllegalStateException e) {
            fail("Illegal state exception caugth too early");
        } finally {
            IOUtils.closeQuietly(stream);
        }

        entry.extract(ARCHIVE_EXTRACT_DIR);
    }

    protected static void assertExtractionWasSuccessful() throws Exception {
        assertDirectoryStructureEquals(ARCHIVE_DIR, ARCHIVE_EXTRACT_DIR);
        assertFilesEquals(ARCHIVE_DIR, ARCHIVE_EXTRACT_DIR);
    }

}
