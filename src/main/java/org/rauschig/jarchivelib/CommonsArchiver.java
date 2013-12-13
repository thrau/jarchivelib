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

import static org.rauschig.jarchivelib.CommonsStreamFactory.createArchiveInputStream;
import static org.rauschig.jarchivelib.CommonsStreamFactory.createArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/**
 * Implementation of an {@link Archiver} that uses {@link ArchiveStreamFactory} to generate archive streams by a given
 * archiver name passed when creating the {@code GenericArchiver}. Thus, it can be used for all archive formats the
 * {@code org.apache.commons.compress} library supports.
 */
class CommonsArchiver implements Archiver {

    private final FileType fileType;

    CommonsArchiver(FileType fileType) {
        this.fileType = fileType;
    }

    CommonsArchiver(ArchiveFormat format) {
        this(FileType.get(format));
    }

    public FileType getFileType() {
        return fileType;
    }

    @Override
    public File create(String archive, File destination, File... sources) throws IOException {
        IOUtils.requireDirectory(destination);

        File archiveFile = createNewArchiveFile(archive, getFileType().getSuffix(), destination);

        try (ArchiveOutputStream outputStream = createArchiveOutputStream(this, archiveFile)) {

            writeToArchive(sources, outputStream);

            outputStream.flush();
        } catch (ArchiveException e) {
            throw new IOException(e);
        }

        return archiveFile;
    }

    @Override
    public void extract(File archive, File destination) throws IOException {
        if (archive.isDirectory()) {
            throw new IllegalArgumentException("Can not extract " + archive + ". Source is a directory.");
        } else if (!archive.exists()) {
            throw new FileNotFoundException(archive.getPath());
        } else if (!archive.canRead()) {
            throw new IllegalArgumentException("Can not extract " + archive + ". Can not read from source.");
        }

        IOUtils.requireDirectory(destination);

        try (ArchiveInputStream input = createArchiveInputStream(archive)) {

            ArchiveEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                File file = new File(destination, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    IOUtils.copy(input, file);
                }
            }

        } catch (ArchiveException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ArchiveStream stream(File archive) throws IOException {
        try {
            return new CommonsArchiveStream(createArchiveInputStream(archive));
        } catch (ArchiveException e) {
            throw new IOException(e);
        }
    }

    /**
     * Creates a new File in the given destination. The resulting name will always be "archive"."fileExtension". If the
     * archive name parameter already ends with the given file name extension, it is not additionally appended.
     *
     * @param archive     the name of the archive
     * @param extension   the file extension (e.g. ".tar")
     * @param destination the parent path
     * @return the newly created file
     * @throws IOException if an I/O error occurred while creating the file
     */
    protected File createNewArchiveFile(String archive, String extension, File destination) throws IOException {
        if (!archive.endsWith(extension)) {
            archive += extension;
        }

        File file = new File(destination, archive);
        file.createNewFile();

        return file;
    }

    /**
     * Recursion entry point for {@link #writeToArchive(File, File[], ArchiveOutputStream)}.
     * <p/>
     * Recursively writes all given source {@link File}s into the given {@link ArchiveOutputStream}.
     *
     * @param sources the files to write in to the archive
     * @param archive the archive to write into
     * @throws IOException when an I/O error occurs
     */
    protected void writeToArchive(File[] sources, ArchiveOutputStream archive) throws IOException {
        for (File source : sources) {
            if (!source.exists()) {
                throw new FileNotFoundException(source.getPath());
            } else if (!source.canRead()) {
                throw new FileNotFoundException(source.getPath() + " (Permission denied)");
            }

            if (source.isFile()) {
                writeToArchive(source.getParentFile(), new File[]{source}, archive);
            } else {
                writeToArchive(source, source.listFiles(), archive);
            }
        }
    }

    /**
     * Recursively writes all given source {@link File}s into the given {@link ArchiveOutputStream}. The paths of the
     * sources in the archive will be relative to the given parent {@code File}.
     *
     * @param parent  the parent file node for computing a relative path (see {@link IOUtils#relativePath(File, File)})
     * @param sources the files to write in to the archive
     * @param archive the archive to write into
     * @throws IOException when an I/O error occurs
     */
    protected void writeToArchive(File parent, File[] sources, ArchiveOutputStream archive) throws IOException {
        for (File source : sources) {
            String relativePath = IOUtils.relativePath(parent, source);

            createArchiveEntry(source, relativePath, archive);

            if (source.isDirectory()) {
                writeToArchive(parent, source.listFiles(), archive);
            }
        }
    }

    /**
     * Creates a new {@link ArchiveEntry} in the given {@link ArchiveOutputStream}, and copies the given {@link File}
     * into the new entry.
     *
     * @param file      the file to add to the archive
     * @param entryName the name of the archive entry
     * @param archive   the archive to write to
     * @throws IOException when an I/O error occurs during FileInputStream creation or during copying
     */
    protected void createArchiveEntry(File file, String entryName, ArchiveOutputStream archive) throws IOException {
        ArchiveEntry entry = archive.createArchiveEntry(file, entryName);
        archive.putArchiveEntry(entry);

        if (!entry.isDirectory()) {
            try (FileInputStream input = new FileInputStream(file)) {
                IOUtils.copy(input, archive);
            }
        }

        archive.closeArchiveEntry();
    }
}
