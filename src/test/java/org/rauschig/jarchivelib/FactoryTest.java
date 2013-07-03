package org.rauschig.jarchivelib;

import static org.junit.Assert.*;

import org.junit.Test;

public class FactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void createArchiver_withUnknownArchiveType_fails() throws Exception {
        ArchiverFactory.createArchiver("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createArchiver_withUnknownArchiveAndCompressionType_fails() throws Exception {
        ArchiverFactory.createArchiver("foo", "bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createArchiver_withUnknownCompressionType_fails() throws Exception {
        ArchiverFactory.createArchiver("tar", "bar");
    }

    @Test
    public void createArchiver_fromStringArchiveFormat_returnsCorrectArchiver() throws Exception {
        Archiver archiver = ArchiverFactory.createArchiver("tar");

        assertNotNull(archiver);
        assertEquals(CommonsArchiver.class, archiver.getClass());
    }

    @Test
    public void createArchiver_fromStringArchiveAndCompressionFormat_returnsCorrectArchiver() throws Exception {
        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");

        assertNotNull(archiver);
        assertEquals(ArchiverCompressorDecorator.class, archiver.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCompressor_withUnknownCompressionType_fails() throws Exception {
        CompressorFactory.createCompressor("foo");
    }

    @Test
    public void createCompressor_fromStringCompressionFormat_returnsCorrectCompressor() throws Exception {
        Compressor compressor = CompressorFactory.createCompressor("gz");

        assertNotNull(compressor);
        assertEquals(CommonsCompressor.class, compressor.getClass());
    }
}
