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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream of an archive. Can be used to retrieve each individual {@link ArchiveEntry}.
 * <br>
 * The {@link #getNextEntry()} method is used to reset the input stream ready for reading the data from the next entry.
 */
public abstract class ArchiveStream extends InputStream implements Closeable {

    private ArchiveEntry currentEntry;

    private boolean closed;

    /**
     * Returns the {@link ArchiveEntry} the stream currently points to.
     * 
     * @return the current {@link ArchiveEntry}
     */
    public ArchiveEntry getCurrentEntry() {
        return currentEntry;
    }

    /**
     * Moves the pointer of the stream to the next {@link ArchiveEntry} and returns it.
     * 
     * @return the next archive entry.
     * @throws IOException propagated I/O exception
     */
    public ArchiveEntry getNextEntry() throws IOException {
        currentEntry = createNextEntry();
        return currentEntry;
    }

    /**
     * Abstract method to create the next {@link ArchiveEntry} for the {@link ArchiveStream} implementation.
     * 
     * @return the next archive entry
     * @throws IOException propagated I/O exception
     */
    protected abstract ArchiveEntry createNextEntry() throws IOException;

    @Override
    public void close() throws IOException {
        closed = true;
    }

    /**
     * Checks whether the current stream has been closed
     * 
     * @return true if the stream has been closed
     */
    public boolean isClosed() {
        return closed;
    }

}
