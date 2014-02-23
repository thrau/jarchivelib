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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileTypeTest {

    @Test
    public void get_archive_returnsCorrectFileType() throws Exception {
        FileType extension;

        extension = FileType.get("/path/to/file/file.tar");
        assertTrue(extension.isArchive());
        assertFalse(extension.isCompressed());

        assertNull(extension.getCompressionType());
        assertEquals(ArchiveFormat.TAR, extension.getArchiveFormat());
        assertEquals(".tar", extension.getSuffix());
    }

    @Test
    public void get_compressed_returnsCorrectFileType() throws Exception {
        FileType extension;

        extension = FileType.get("/path/to/file/file.gz");
        assertFalse(extension.isArchive());
        assertTrue(extension.isCompressed());

        assertEquals(CompressionType.GZIP, extension.getCompressionType());
        assertNull(extension.getArchiveFormat());
        assertEquals(".gz", extension.getSuffix());
    }

    @Test
    public void get_compressedArchive_returnsCorrectFileType() throws Exception {
        FileType extension;

        extension = FileType.get("/path/to/file/file.tar.gz");
        assertTrue(extension.isArchive());
        assertTrue(extension.isCompressed());

        assertEquals(CompressionType.GZIP, extension.getCompressionType());
        assertEquals(ArchiveFormat.TAR, extension.getArchiveFormat());
        assertEquals(".tar.gz", extension.getSuffix());
    }

    @Test
    public void get_unknownExtension_returnsUnknown() throws Exception {
        assertEquals(FileType.UNKNOWN, FileType.get("/path/to/file/file.foobar"));
    }
}
