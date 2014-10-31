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
import static org.rauschig.jarchivelib.CommonsStreamFactory.createCompressorInputStream;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;

/**
 * Decorates an {@link Archiver} with a {@link Compressor}, s.t. it is able to compress the archives it generates and
 * decompress the archives it extracts.
 */
class ArchiverCompressorDecorator implements Archiver {

    private CommonsArchiver archiver;
    private CommonsCompressor compressor;

    /**
     * Decorates the given Archiver with the given Compressor.
     * 
     * @param archiver the archiver to decorate
     * @param compressor the compressor used for compression
     */
    ArchiverCompressorDecorator(CommonsArchiver archiver, CommonsCompressor compressor) {
        this.archiver = archiver;
        this.compressor = compressor;
    }

    @Override
    public File create(String archive, File destination, File... sources) throws IOException {
        IOUtils.requireDirectory(destination);
        
        File temp = File.createTempFile("jAL" + destination.getName(), archiver.getFilenameExtension(), destination);
        File destinationArchive = null;
        try {
            temp = archiver.create(temp.getName(), temp.getParentFile(), sources);
            destinationArchive = new File(destination, getArchiveFileName(archive));

            compressor.compress(temp, destinationArchive);
        } finally {
            temp.delete();
        }

        return destinationArchive;
    }

    @Override
    public void extract(File archive, File destination) throws IOException {
        IOUtils.requireDirectory(destination);

        File temp = File.createTempFile("jAL" + archive.getName(), archiver.getFilenameExtension(), destination);
        try {
            compressor.decompress(archive, temp);
            archiver.extract(temp, destination);
        } finally {
            temp.delete();
        }
    }

    @Override
    public ArchiveStream stream(File archive) throws IOException {
        try {
            return new CommonsArchiveStream(createArchiveInputStream(archiver, createCompressorInputStream(archive)));
        } catch (ArchiveException e) {
            throw new IOException(e);
        } catch (CompressorException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getFilenameExtension() {
        return archiver.getFilenameExtension() + compressor.getFilenameExtension();
    }

    /**
     * Returns a file name from the given archive name. The file extension suffix will be appended according to what is
     * already present.
     * <p/>
     * E.g. if the compressor uses the file extension "gz", the archiver "tar", and passed argument is "archive.tar",
     * the returned value will be "archive.tar.gz".
     * 
     * @param archive the existing archive file name
     * @return the normalized archive file name including the correct file name extension
     */
    private String getArchiveFileName(String archive) {
        String fileExtension = getFilenameExtension();

        if (archive.endsWith(fileExtension)) {
            return archive;
        } else if (archive.endsWith(archiver.getFilenameExtension())) {
            return archive + compressor.getFilenameExtension();
        } else {
            return archive + fileExtension;
        }
    }

}
