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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    private ArchiveStreamFactory streamFactory = new ArchiveStreamFactory();

    private final String archiverName;
    private final String fileExtension;

    CommonsArchiver(String archiverName) {
        this.archiverName = archiverName.toLowerCase();
        this.fileExtension = "." + archiverName.toLowerCase();
    }

    /**
     * Returns the name of the archiver.
     * 
     * @return the archiver name
     * @see ArchiverFactory
     */
    public String getArchiverName() {
        return archiverName;
    }

    /**
     * Returns the file extension, which is equal to "." + {@link #getArchiverName()}.
     * 
     * @return the filename extension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public File create(String archive, File destination, File... sources) throws IOException {
        IOUtils.requireDirectory(destination);

        File archiveFile = createNewArchiveFile(archive, fileExtension, destination);

        try (ArchiveOutputStream outputStream = createArchiveOutputStream(archiveFile)) {

            writeToArchive(sources, outputStream);

            outputStream.flush();
        } catch (ArchiveException e) {
            throw new IOException(e);
        }

        return archiveFile;
    }

    @Override
    public void extract(File archive, File destination) throws IOException {
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

    /**
     * Uses the {@link #streamFactory} and the {@link #archiverName} to create a new {@link ArchiveOutputStream} for the
     * given archive {@link File}.
     * 
     * @param archive the archive file to create the {@link ArchiveOutputStream} for
     * @return a new {@link ArchiveOutputStream}
     * @throws IOException propagated IOExceptions when creating the FileOutputStream.
     * @throws ArchiveException if the archiver name is not known
     */
    protected ArchiveOutputStream createArchiveOutputStream(File archive) throws IOException, ArchiveException {
        return streamFactory.createArchiveOutputStream(archiverName, new FileOutputStream(archive));
    }

    /**
     * Uses the {@link #streamFactory} to create a new {@link ArchiveInputStream} for the given archive file.
     * 
     * @param archive the archive file
     * @return a new {@link ArchiveInputStream} for the given archive file
     * @throws IOException propagated IOException when creating the FileInputStream.
     * @throws ArchiveException if the archiver name is not known
     */
    protected ArchiveInputStream createArchiveInputStream(File archive) throws IOException, ArchiveException {
        return streamFactory.createArchiveInputStream(new BufferedInputStream(new FileInputStream(archive)));
    }

    /**
     * Creates a new File in the given destination. The resulting name will always be "archive"."fileExtension". If the
     * archive name parameter already ends with the given file name extension, it is not additionally appended.
     * 
     * @param archive the name of the archive
     * @param extension the file extension (e.g. ".tar")
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
     * <p>
     * Recursively writes all given source {@link File}s into the given {@link ArchiveOutputStream}.
     * 
     * @param sources the files to write in to the archive
     * @param archive the archive to write into
     * @throws IOException when an I/O error occurs
     */
    protected void writeToArchive(File[] sources, ArchiveOutputStream archive) throws IOException {
        for (File source : sources) {
            if (source.isFile()) {
                writeToArchive(source.getParentFile(), new File[] { source }, archive);
            } else {
                writeToArchive(source, source.listFiles(), archive);
            }
        }
    }

    /**
     * Recursively writes all given source {@link File}s into the given {@link ArchiveOutputStream}. The paths of the
     * sources in the archive will be relative to the given parent {@code File}.
     * 
     * @param parent the parent file node for computing a relative path (see {@link #relativePath(File, File)})
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
     * @param file the file to add to the archive
     * @param entryName the name of the archive entry
     * @param archive the archive to write to
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
