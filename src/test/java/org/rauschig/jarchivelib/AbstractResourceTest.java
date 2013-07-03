package org.rauschig.jarchivelib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class AbstractResourceTest {

    public static final File RESOURCES_DIR = new File("src/test/resources");

    public static final File ARCHIVE_CREATE_DIR = new File(RESOURCES_DIR, "created");
    public static final File ARCHIVE_EXTRACT_DIR = new File(RESOURCES_DIR, "extracted");

    /**
     * Contains the following files:
     * <ul>
     * <li>src/test/resources/archives/archive/folder</li>
     * <li>src/test/resources/archives/archive/folder/folder_file.txt</li>
     * <li>src/test/resources/archives/archive/folder/subfolder</li>
     * <li>src/test/resources/archives/archive/folder/subfolder/subfolder_file.txt</li>
     * <li>src/test/resources/archives/archive/file.txt</li>
     * </ul>
     * 
     * Used both as reference to compare whether extraction was successful, and used as source for compression tests.
     */
    public static final File ARCHIVE_DIR = new File(RESOURCES_DIR, "archive");

    public static final File NON_READABLE_FILE = new File(RESOURCES_DIR, "non_readable_file.txt");

    @Before
    public synchronized void createResources() {
        if (!ARCHIVE_EXTRACT_DIR.exists()) {
            ARCHIVE_EXTRACT_DIR.mkdirs();
        }
        if (!ARCHIVE_CREATE_DIR.exists()) {
            ARCHIVE_CREATE_DIR.mkdirs();
        }

        NON_READABLE_FILE.setReadable(false);
    }

    @After
    public synchronized void dropResources() throws IOException {
        if (ARCHIVE_EXTRACT_DIR.exists()) {
            FileUtils.deleteDirectory(ARCHIVE_EXTRACT_DIR);
        }
        if (ARCHIVE_CREATE_DIR.exists()) {
            FileUtils.deleteDirectory(ARCHIVE_CREATE_DIR);
        }

        NON_READABLE_FILE.setReadable(true);
    }

    protected static void assertDirectoryStructureEquals(File expected, File actual) throws IOException {
        String[] expecteds = flatRelativeArray(expected);
        String[] actuals = flatRelativeArray(actual);

        String msg = String.format("Directory structures of %s and %s do not match.", expected, actual);
        Assert.assertArrayEquals(msg, expecteds, actuals);
    }

    public static void assertFileContentEquals(File expected, File actual) throws IOException {
        String actualString = new Scanner(actual).useDelimiter("\\Z").next();
        String expectedString = new Scanner(expected).useDelimiter("\\Z").next();

        String msg = String.format("File contents of %s and %s differ.", expected, actual);
        Assert.assertEquals(msg, expectedString, actualString);
    }

    protected static void assertFilesEquals(File expectedDir, File actualDir) throws Exception {
        String[] expecteds = flatArray(expectedDir);
        String[] actuals = flatArray(actualDir);

        // check whether hashes of files match
        for (int i = 0; i < expecteds.length; i++) {
            File expected = new File(expecteds[i]);
            File actual = new File(actuals[i]);

            Assert.assertEquals(expected.getName(), actual.getName());

            if (expected.isFile()) {
                assertFileContentEquals(expected, actual);
            }
        }
    }

    public static String[] flatArray(File root) throws IOException {
        List<String> flatList = flatList(root);

        return flatList.toArray(new String[flatList.size()]);
    }

    public static List<String> flatList(File root) throws IOException {
        List<String> list = new ArrayList<String>();

        File[] nodes = root.listFiles();

        for (File node : nodes) {
            list.add(node.getPath());

            if (node.isDirectory()) {
                list.addAll(flatList(node));
            }
        }

        return list;
    }

    public static String[] flatRelativeArray(File root) throws IOException {
        List<String> flatList = flatRelativeList(root);

        return flatList.toArray(new String[flatList.size()]);
    }

    public static List<String> flatRelativeList(File root) throws IOException {
        return flatRelativeList(root, root);
    }

    public static List<String> flatRelativeList(File root, File current) throws IOException {
        List<String> list = new ArrayList<String>();

        String prefix = root.getCanonicalPath();
        File[] nodes = current.getCanonicalFile().listFiles();

        for (File node : nodes) {
            list.add(node.getPath().substring(prefix.length() + 1));

            if (node.isDirectory()) {
                list.addAll(flatRelativeList(root, node));
            }
        }

        return list;
    }

}
