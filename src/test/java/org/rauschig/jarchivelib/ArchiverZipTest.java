package org.rauschig.jarchivelib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArchiverZipTest extends AbstractArchiverTest {

    private Archiver archiver;
    private File archive = new File(RESOURCES_DIR, "archive.zip");

    @Before
    public void setUp() throws Exception {
        archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
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
        File archive = archiver.create("archive.zip", ARCHIVE_CREATE_DIR, ARCHIVE_DIR);

        assertTrue(archive.exists());
        assertEquals("archive.zip", archive.getName());

        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertExtractionWasSuccessful();
    }

    @Test
    public void create_recursiveDirectory_withoutFileExtension_properlyCreatesArchive() throws Exception {
        File archive = archiver.create("archive", ARCHIVE_CREATE_DIR, ARCHIVE_DIR);

        assertTrue(archive.exists());
        assertEquals("archive.zip", archive.getName());

        archiver.extract(archive, ARCHIVE_EXTRACT_DIR);
        assertExtractionWasSuccessful();
    }

}
