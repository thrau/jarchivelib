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
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Wraps the two commons-compress factory types {@link CompressorFactory} and {@link ArchiveStreamFactory} into a
 * singleton factory.
 */
final class CommonsStreamFactory {

    private CommonsStreamFactory() {

    }

    private static CompressorStreamFactory compressorStreamFactory;
    private static ArchiveStreamFactory archiveStreamFactory;

    static {
        archiveStreamFactory = new ArchiveStreamFactory();
        compressorStreamFactory = new CompressorStreamFactory();
    }

    /**
     * @see {@link ArchiveStreamFactory#createArchiveInputStream(String, InputStream)}
     */
    static ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in) throws ArchiveException {
        return archiveStreamFactory.createArchiveInputStream(archiverName, in);
    }

    /**
     * @see {@link ArchiveStreamFactory#createArchiveInputStream(String, InputStream)}
     */
    static ArchiveInputStream createArchiveInputStream(ArchiveFormat archiveFormat, InputStream in)
            throws ArchiveException {
        return createArchiveInputStream(archiveFormat.getName(), in);
    }

    /**
     * @see {@link ArchiveStreamFactory#createArchiveInputStream(String, InputStream)}
     */
    static ArchiveInputStream createArchiveInputStream(Archiver archiver, InputStream in)
            throws ArchiveException {
        return createArchiveInputStream(archiver.getArchiveFormat(), in);
    }

    /**
     * @see {@link ArchiveStreamFactory#createArchiveInputStream(InputStream)};
     */
    static ArchiveInputStream createArchiveInputStream(InputStream in) throws ArchiveException {
        return archiveStreamFactory.createArchiveInputStream(new BufferedInputStream(in));
    }

    /**
     * Uses the {@link ArchiveStreamFactory} to create a new {@link ArchiveInputStream} for the given archive file.
     *
     * @param archive the archive file
     * @return a new {@link ArchiveInputStream} for the given archive file
     * @throws IOException propagated IOException when creating the FileInputStream.
     * @throws ArchiveException if the archiver name is not known
     */
    static ArchiveInputStream createArchiveInputStream(File archive) throws IOException, ArchiveException {
        return createArchiveInputStream(new BufferedInputStream(new FileInputStream(archive)));
    }

    /**
     * @see {@link ArchiveStreamFactory#createArchiveOutputStream(String, OutputStream)};
     */
    static ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out)
            throws ArchiveException {
        return archiveStreamFactory.createArchiveOutputStream(archiverName, out);
    }

    static ArchiveOutputStream createArchiveOutputStream(ArchiveFormat format, File archive) throws IOException,
            ArchiveException {
        return createArchiveOutputStream(format.getName(), new FileOutputStream(archive));
    }

    /**
     * Uses the {@link ArchiveStreamFactory} and the name of the given archiver to create a new
     * {@link ArchiveOutputStream} for the given archive {@link File}.
     *
     * @param archiver the invoking archiver
     * @param archive the archive file to create the {@link ArchiveOutputStream} for
     * @return a new {@link ArchiveOutputStream}
     * @throws IOException propagated IOExceptions when creating the FileOutputStream.
     * @throws ArchiveException if the archiver name is not known
     */
    static ArchiveOutputStream createArchiveOutputStream(CommonsArchiver archiver, File archive) throws IOException,
            ArchiveException {
        return createArchiveOutputStream(archiver.getArchiveFormat(), archive);
    }

    /**
     * Uses the {@link CompressorStreamFactory} to create a new {@link CompressorInputStream} for the given source
     * {@link File}.
     *
     * @param source the file to create the {@link CompressorInputStream} for
     * @return a new {@link CompressorInputStream}
     * @throws IOException if an I/O error occurs
     * @throws CompressorException if the compressor name is not known
     */
    static CompressorInputStream createCompressorInputStream(File source) throws IOException, CompressorException {
        return createCompressorInputStream(new BufferedInputStream(new FileInputStream(source)));
    }

    /**
     * Uses the {@link CompressorStreamFactory} to create a new {@link CompressorInputStream} for the compression type
     * and wraps the given source {@link File} with it.
     *
     * @param source the file to create the {@link CompressorInputStream} for
     * @return a new {@link CompressorInputStream}
     * @throws IOException if an I/O error occurs
     * @throws CompressorException if the compressor name is not known
     */
    static CompressorInputStream createCompressorInputStream(CompressionType type, File source) throws IOException,
            CompressorException {
        return createCompressorInputStream(type, new BufferedInputStream(new FileInputStream(source)));
    }

    /**
     * @see {@link CompressorStreamFactory#createCompressorInputStream(String, java.io.InputStream)}
     */
    static CompressorInputStream createCompressorInputStream(CompressionType compressionType, InputStream in)
            throws CompressorException {
        return compressorStreamFactory.createCompressorInputStream(compressionType.getName(), in);
    }

    /**
     * @see {@link CompressorStreamFactory#createCompressorInputStream(InputStream)};
     */
    static CompressorInputStream createCompressorInputStream(InputStream in) throws CompressorException {
        return compressorStreamFactory.createCompressorInputStream(in);
    }

    static CompressorOutputStream createCompressorOutputStream(CompressionType compressionType, File destination)
            throws IOException, CompressorException {
        return createCompressorOutputStream(compressionType.getName(), new FileOutputStream(destination));
    }

    /**
     * Uses the {@link CompressorStreamFactory} and the name of the given compressor to create a new
     * {@link CompressorOutputStream} for the given destination {@link File}.
     *
     * @param compressor the invoking compressor
     * @param destination the file to create the {@link CompressorOutputStream} for
     * @return a new {@link CompressorOutputStream}
     * @throws IOException if an I/O error occurs
     * @throws CompressorException if the compressor name is not known
     */
    static CompressorOutputStream createCompressorOutputStream(CommonsCompressor compressor, File destination)
            throws IOException, CompressorException {
        return createCompressorOutputStream(compressor.getCompressionType(), destination);
    }

    /**
     * @see {@link CompressorStreamFactory#createCompressorOutputStream(String, OutputStream)};
     */
    static CompressorOutputStream createCompressorOutputStream(String compressorName, OutputStream out)
            throws CompressorException {
        return compressorStreamFactory.createCompressorOutputStream(compressorName, out);
    }

}
