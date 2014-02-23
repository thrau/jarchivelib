package org.rauschig.jarchivelib;

import java.io.File;

public class CompressorGzipTest extends AbstractCompressorTest {

    @Override
    protected File getCompressedFile() {
        return new File(RESOURCES_DIR, "compress.txt.gz");
    }

    @Override
    protected Compressor getCompressor() {
        return new CommonsCompressor(CompressionType.GZIP);
    }
}
