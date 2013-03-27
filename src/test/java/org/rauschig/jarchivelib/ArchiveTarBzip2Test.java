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
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArchiveTarBzip2Test extends AbstractArchiverTest {

    private Archiver archiver;
    private File archive = new File(RESOURCES_DIR, "archive.tar.bzip2");

    @Before
    public void setUp() throws Exception {
        archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.BZIP2);
    }

    @After
    public void tearDown() throws Exception {
        archiver = null;
    }

    @Test
    public void extract_properlyExtractsArchive() throws Exception {
        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);

        assertExtractionWasSuccessful();
    }

    @Test
    public void create_recursiveDirectory_withFileExtension_properlyCreatesArchive() throws Exception {
        File archive = archiver.create("archive.tar.bzip2", ARCHIVE_CREATE_DIR, ARCHIVE_DIR);

        assertTrue(archive.exists());
        assertEquals("archive.tar.bzip2", archive.getName());

        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertExtractionWasSuccessful();
    }

    @Test
    public void create_recursiveDirectory_withoutFileExtension_properlyCreatesArchive() throws Exception {
        File archive = archiver.create("archive", ARCHIVE_CREATE_DIR, ARCHIVE_DIR);

        assertTrue(archive.exists());
        assertEquals("archive.tar.bzip2", archive.getName());

        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertExtractionWasSuccessful();
    }

}
