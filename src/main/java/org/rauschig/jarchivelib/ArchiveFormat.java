package org.rauschig.jarchivelib;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;

/**
 * Denotes an archive format such as zip or tar.
 */
public enum ArchiveFormat {

    /**
     * Constant used to identify the AR archive format.
     */
    AR(ArchiveStreamFactory.AR),
    /**
     * Constant used to identify the CPIO archive format.
     */
    CPIO(ArchiveStreamFactory.CPIO),
    /**
     * Constant used to identify the Unix DUMP archive format.
     */
    DUMP(ArchiveStreamFactory.DUMP),
    /**
     * Constant used to identify the JAR archive format.
     */
    JAR(ArchiveStreamFactory.JAR),
    /**
     * Constant used to identify the TAR archive format.
     */
    TAR(ArchiveStreamFactory.TAR),
    /**
     * Constant used to identify the ZIP archive format.
     */
    ZIP(ArchiveStreamFactory.ZIP);

    /**
     * The name by which the compression algorithm is identified by
     */
    private final String name;

    private ArchiveFormat(String name) {
        this.name = name;
    }

    /**
     * Returns the name by which the archive format is identified by.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if the given archive format is known to the factory, false otherwise
     */
    public static boolean isValidArchiveFormat(String archiveFormat) {
        for (ArchiveFormat format : values()) {
            if (archiveFormat.equalsIgnoreCase(format.getName())) {
                return true;
            }
        }

        return false;
    }
}
