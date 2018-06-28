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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArchiverZipTest extends AbstractArchiverTest {

    /**
     * Contains 2 files. safe.txt that is a safe file located at the root of the target directory and unsafe.txt that
     * attempts to traverse the tree all the way to / and down to tmp. This should be placed at target/tmp/unsafe.txt
     * when extracted
     */
    private static final String ZIP_TRAVERSAL_FILE_1 = "zip_traversal.zip";

    /**
     * Contains 2 files. safe.txt that is a safe file located at the root of the target directory and unsafe.txt that
     * attempts to traverse the tree outside the target directory but not high enough to make it to /.
     * This should be placed at target/unsafe.txt when extracted
     */
    private static final String ZIP_TRAVERSAL_FILE_2 = "zip_traversal_2.zip";

    @Override
    protected Archiver getArchiver() {
        return ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
    }

    @Override
    protected File getArchive() {
        return new File(RESOURCES_DIR, "archive.zip");
    }

    @Test
    public void zip_traversal_test_entry_extraction() throws Exception {
        archiveExtractorHelper(ZIP_TRAVERSAL_FILE_1);
        assertZipTraversal();
    }

    @Test
    public void zip_traversal_test_archiver_extraction() throws Exception {
        File archive = new File(RESOURCES_DIR, ZIP_TRAVERSAL_FILE_1);
        getArchiver().extract(archive, ARCHIVE_EXTRACT_DIR);
        assertZipTraversal();
    }

    @Test
    public void zip_traversal_test_entry_extraction_target_directory_as_root() throws Exception {
        archiveExtractorHelper(ZIP_TRAVERSAL_FILE_2);
        assertTargetDirectoryAsRoot();
    }

    @Test
    public void zip_traversal_test_archiver_extraction_target_directory_as_root() throws Exception {
        File archive = new File(RESOURCES_DIR, ZIP_TRAVERSAL_FILE_2);
        getArchiver().extract(archive, ARCHIVE_EXTRACT_DIR);
        assertTargetDirectoryAsRoot();
    }

    private void archiveExtractorHelper(final String fileName) throws IOException {
        File archive = new File(RESOURCES_DIR, fileName);
        ArchiveStream stream = null;
        try {
            stream = getArchiver().stream(archive);
            ArchiveEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                entry.extract(ARCHIVE_EXTRACT_DIR);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private void assertZipTraversal () throws Exception {
        HashSet<String> extractedItems = new HashSet<String>(Arrays.asList(flatRelativeArray(ARCHIVE_EXTRACT_DIR)));
        assertEquals(3, extractedItems.size());
        assertTrue(extractedItems.contains("safe.txt"));
        assertTrue(extractedItems.contains("tmp"));
        assertTrue(extractedItems.contains("tmp/unsafe.txt"));
        assertFalse("This unsafe file should not exist as it is outside the target directory.",
            new File("/tmp/unsafe.txt").exists());
    }

    private void assertTargetDirectoryAsRoot() throws Exception {
        HashSet<String> extractedItems = new HashSet<String>(Arrays.asList(flatRelativeArray(ARCHIVE_EXTRACT_DIR)));
        assertEquals(2, extractedItems.size());
        assertTrue(extractedItems.contains("safe.txt"));
        assertTrue(extractedItems.contains("unsafe.txt"));
    }
}
