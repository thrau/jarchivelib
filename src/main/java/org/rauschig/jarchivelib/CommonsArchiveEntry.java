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
import java.util.Date;

/**
 * Implementation of an {@link ArchiveEntry} that wraps the commons compress version of the same type.
 */
class CommonsArchiveEntry implements ArchiveEntry {

    /**
     * The wrapped {@code ArchiveEntry} entry.
     */
    private org.apache.commons.compress.archivers.ArchiveEntry entry;

    /**
     * The {@link ArchiveStream} this entry belongs to.
     */
    private ArchiveStream stream;

    CommonsArchiveEntry(ArchiveStream stream, org.apache.commons.compress.archivers.ArchiveEntry entry) {
        this.stream = stream;
        this.entry = entry;
    }

    @Override
    public String getName() {
        assertState();
        return entry.getName();
    }

   @Override
	public long getSize() {
		assert entry != null : "Entry cannot be null";
		assert entry.getSize() >= 0 : "Entry size cannot be negative";
		assertState();
		return entry.getSize();
	}

    @Override
    public Date getLastModifiedDate() {
        assertState();
        return entry.getLastModifiedDate();
    }

    @Override
    public boolean isDirectory() {
        assertState();
        return entry.isDirectory();
    }

    @Override
    public File extract(File destination) throws IOException, IllegalStateException, IllegalArgumentException {
        assertState();
        IOUtils.requireDirectory(destination);

        File file = new File(destination, entry.getName());

        if (entry.isDirectory()) {
            file.mkdirs();
        } else {
            file.getParentFile().mkdirs();
            IOUtils.copy(stream, file);
        }

        FileModeMapper.map(entry, file);

        return file;
    }

    private void assertState() {
        if (stream.isClosed()) {
            throw new IllegalStateException("Stream has already been closed");
        }
        if (this != stream.getCurrentEntry()) {
            throw new IllegalStateException("Illegal stream pointer");
        }
    }

}
