package org.rauschig.jarchivelib;

import java.io.File;

public class CompressorBzip2Test extends AbstractCompressorTest {

    @Override
    protected File getCompressedFile() {
        return new File(RESOURCES_DIR, "compress.txt.bz2");
    }

    @Override
    protected Compressor getCompressor() {
        return new CommonsCompressor(CompressionType.BZIP2);
    }
}
