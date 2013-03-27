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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public abstract class AbstractArchiverTest {

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

    @Before
    public synchronized void createDirs() {
        if (!ARCHIVE_EXTRACT_DIR.exists()) {
            ARCHIVE_EXTRACT_DIR.mkdirs();
        }
        if (!ARCHIVE_CREATE_DIR.exists()) {
            ARCHIVE_CREATE_DIR.mkdirs();
        }
    }

    @After
    public synchronized void deleteDirs() throws IOException {
        if (ARCHIVE_EXTRACT_DIR.exists()) {
            FileUtils.deleteDirectory(ARCHIVE_EXTRACT_DIR);
        }
        if (ARCHIVE_CREATE_DIR.exists()) {
            FileUtils.deleteDirectory(ARCHIVE_CREATE_DIR);
        }
    }

    protected static void assertExtractionWasSuccessful() throws Exception {
        assertDirectoryStructureEquals(ARCHIVE_DIR, ARCHIVE_EXTRACT_DIR);
        assertFilesEquals(ARCHIVE_DIR, ARCHIVE_EXTRACT_DIR);
    }

    protected static void assertDirectoryStructureEquals(File expected, File actual) throws IOException {
        String[] expecteds = flatRelativeArray(expected);
        String[] actuals = flatRelativeArray(actual);

        String msg = String.format("Directory structures of %s and %s do not match.", expected, actual);
        Assert.assertArrayEquals(msg, expecteds, actuals);
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

    public static String[] flatArray(File root) throws IOException {
        List<String> flatList = flatList(root);

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

    public static String[] flatRelativeArray(File root) throws IOException {
        List<String> flatList = flatRelativeList(root);

        return flatList.toArray(new String[flatList.size()]);
    }

    public static void assertFileContentEquals(File expected, File actual) throws IOException {
        String actualString = new Scanner(actual).useDelimiter("\\Z").next();
        String expectedString = new Scanner(expected).useDelimiter("\\Z").next();

        String msg = String.format("File contents of %s and %s differ.", expected, actual);
        Assert.assertEquals(msg, expectedString, actualString);
    }

}
