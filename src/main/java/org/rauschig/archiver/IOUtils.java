package org.rauschig.archiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {
    private IOUtils() {

    }

    /**
     * Copies the content of an InputStream into a destination File.
     * 
     * @param source the InputStream to copy
     * @param destination the target File
     * @throws IOException if an error occurs
     */
    public static void copy(InputStream source, File destination) throws IOException {
        try (OutputStream output = new FileOutputStream(destination)) {
            copy(source, output);
        }
    }

    /**
     * Copies the content of a InputStream into an OutputStream. Uses a default buffer size of 8024 bytes.
     * 
     * @param input the InputStream to copy
     * @param output the target Stream
     * @throws IOException if an error occurs
     */
    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 8024);
    }

    /**
     * Copies the content of a InputStream into an OutputStream
     * 
     * @param input the InputStream to copy
     * @param output the target Stream
     * @param buffersize the buffer size to use
     * @throws IOException if an error occurs
     */
    public static long copy(final InputStream input, final OutputStream output, int buffersize) throws IOException {
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        long count = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Computes the path name of a file node relative to a given root node.
     * <p>
     * If the root is {@code /home/cdlflex/custom-ahy} and the given node is
     * {@code /home/cdlflex/custom-ahy/assembly/pom.xml}, the returned path name will be {@code assembly/pom.xml}.
     * 
     * 
     * @param root the parent node
     * @param node the file node to compute the relative path for
     * @return the path of {@code node} relative to {@code root}
     * @throws IOException
     */
    public static String relativePath(File root, File node) throws IOException {
        String rootPath = root.getCanonicalPath();
        String nodePath = node.getCanonicalPath();

        return nodePath.substring(rootPath.length() + 1);
    }

    /**
     * Makes sure that the given {@link File} is either a writable directory, or that it does not exist and a directory
     * can be created at its path.
     * <p>
     * Will throw an exception if the given {@link File} is actually an existing file, or the directory is not writable
     * 
     * @param destination
     * @throws IOException
     * @throws IllegalArgumentException if the destination is an existing file, or the directory is not writable
     */
    public static void requireDirectory(File destination) throws IOException {
        if (destination.isFile()) {
            throw new IllegalArgumentException("Given destination exists and is a file");
        } else if (!destination.exists()) {
            destination.mkdirs();
        }
        if (!destination.canWrite()) {
            throw new IllegalArgumentException("Can not write to destination " + destination);
        }
    }

}
