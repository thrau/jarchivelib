package org.rauschig.archiver;

import java.io.File;
import java.io.IOException;

/**
 * An Archiver facades a specific archiving library, allowing for simple archiving of files and directories, and
 * extraction of archives.
 * <p>
 * Some archivers might use an additional {@link Compressor} to compress and decompress their respective archive files.
 */
public interface Archiver {

    /**
     * Creates an archive from the given source files or directories, and saves it into the given destination.
     * <p>
     * If the archive parameter has no file extension (e.g. "archive" instead of "archive.zip"), the concrete archiver
     * implementation should append it according to its file format (.zip, .tar, .tar.gz, ...).
     * 
     * @param archive the name of the archive to create
     * @param destination the destination directory where to place the created archive
     * @param sources the input files or directories to archive
     * @return the newly created archive file
     * @throws IOException propagated I/O errors by {@code java.io}
     */
    File create(String archive, File destination, File... sources) throws IOException;

    /**
     * Extracts the given archive file into the given destination directory.
     * <p>
     * The destination is expected to be a writable directory.
     * 
     * @param archive the archive file to extract
     * @param destination the directory to which to extract the files
     * @throws IOException propagated I/O errors by {@code java.io}
     */
    void extract(File archive, File destination) throws IOException;
}
