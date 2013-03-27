package org.rauschig.jarchivelib;

import java.io.File;
import java.io.IOException;


/**
 * Decorates an {@link Archiver} with a {@link Compressor}, s.t. it is able to compress the archives it generates and
 * decompress the archives it extracts.
 */
class ArchiverCompressorDecorator implements Archiver {

    private GenericArchiver archiver;
    private GenericCompressor compressor;

    /**
     * Decorates the given Archiver with the given Compressor.
     * 
     * @param archiver the archiver to decorate
     * @param compressor the compressor used for compression
     */
    ArchiverCompressorDecorator(GenericArchiver archiver, GenericCompressor compressor) {
        this.archiver = archiver;
        this.compressor = compressor;
    }

    @Override
    public File create(String archive, File destination, File... sources) throws IOException {
        File temp = File.createTempFile(destination.getName(), archiver.getFileExtension(), destination);
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

        File temp = File.createTempFile(archive.getName(), archiver.getFileExtension(), destination);

        try {
            compressor.decompress(archive, temp);
            archiver.extract(temp, destination);
        } finally {
            temp.delete();
        }
    }

    /**
     * Returns a file name from the given archive name. The file extension suffix will be appended according to what is
     * already present.
     * <p>
     * E.g. if the compressor uses the file extension "gz", the archiver "tar", and passed argument is "archive.tar",
     * the returned value will be "archive.tar.gz".
     * 
     * @param archive
     * @return
     */
    private String getArchiveFileName(String archive) {
        String fileExtension = archiver.getFileExtension() + compressor.getFileExtension();

        if (archive.endsWith(fileExtension)) {
            return archive;
        } else if (archive.endsWith(archiver.getFileExtension())) {
            return archive + compressor.getFileExtension();
        } else {
            return archive + fileExtension;
        }
    }

}
