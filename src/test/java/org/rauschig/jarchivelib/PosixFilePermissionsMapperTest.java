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

import org.junit.Test;
import org.rauschig.jarchivelib.FileModeMapper.PosixFilePermissionsMapper;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.GROUP_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static org.junit.Assert.assertEquals;

public class PosixFilePermissionsMapperTest {

    private PosixFilePermissionsMapper posixFilePermissionsMapper = new PosixFilePermissionsMapper();

    private Set<PosixFilePermission> setOf(PosixFilePermission... posixFilePermissions) {
        return new HashSet<>(Arrays.asList(posixFilePermissions));
    }

    @Test
    public void noPermissions() {
        assertEquals(posixFilePermissionsMapper.map(0000), setOf());
    }

    @Test
    public void allPermissions() {
        assertEquals(posixFilePermissionsMapper.map(0777), setOf(PosixFilePermission.values()));
    }

    @Test
    public void ownerPermissions() {
        assertEquals(posixFilePermissionsMapper.map(0400), setOf(OWNER_READ));
        assertEquals(posixFilePermissionsMapper.map(0200), setOf(OWNER_WRITE));
        assertEquals(posixFilePermissionsMapper.map(0100), setOf(OWNER_EXECUTE));

        assertEquals(posixFilePermissionsMapper.map(0700), setOf(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE));
        assertEquals(posixFilePermissionsMapper.map(0600), setOf(OWNER_WRITE, OWNER_READ));
        assertEquals(posixFilePermissionsMapper.map(0500), setOf(OWNER_EXECUTE, OWNER_READ));
        assertEquals(posixFilePermissionsMapper.map(0300), setOf(OWNER_EXECUTE, OWNER_WRITE));
    }

    @Test
    public void groupPermissions() {
        assertEquals(posixFilePermissionsMapper.map(0040), setOf(GROUP_READ));
        assertEquals(posixFilePermissionsMapper.map(0020), setOf(GROUP_WRITE));
        assertEquals(posixFilePermissionsMapper.map(0010), setOf(GROUP_EXECUTE));

        assertEquals(posixFilePermissionsMapper.map(0070), setOf(GROUP_READ, GROUP_WRITE, GROUP_EXECUTE));
        assertEquals(posixFilePermissionsMapper.map(0060), setOf(GROUP_WRITE, GROUP_READ));
        assertEquals(posixFilePermissionsMapper.map(0050), setOf(GROUP_EXECUTE, GROUP_READ));
        assertEquals(posixFilePermissionsMapper.map(0030), setOf(GROUP_EXECUTE, GROUP_WRITE));
    }

    @Test
    public void othersPermissions() {
        assertEquals(posixFilePermissionsMapper.map(0004), setOf(OTHERS_READ));
        assertEquals(posixFilePermissionsMapper.map(0002), setOf(OTHERS_WRITE));
        assertEquals(posixFilePermissionsMapper.map(0001), setOf(OTHERS_EXECUTE));

        assertEquals(posixFilePermissionsMapper.map(0007), setOf(OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE));
        assertEquals(posixFilePermissionsMapper.map(0006), setOf(OTHERS_WRITE, OTHERS_READ));
        assertEquals(posixFilePermissionsMapper.map(0005), setOf(OTHERS_EXECUTE, OTHERS_READ));
        assertEquals(posixFilePermissionsMapper.map(0003), setOf(OTHERS_EXECUTE, OTHERS_WRITE));
    }

    @Test
    public void permissionsSameForAll() {
        assertEquals(posixFilePermissionsMapper.map(0444), setOf(OTHERS_READ, GROUP_READ, OWNER_READ));
        assertEquals(posixFilePermissionsMapper.map(0222), setOf(OTHERS_WRITE, GROUP_WRITE, OWNER_WRITE));
        assertEquals(posixFilePermissionsMapper.map(0111), setOf(OTHERS_EXECUTE, GROUP_EXECUTE, OWNER_EXECUTE));
    }

    @Test
    public void permissionCombinations() {
        assertEquals(posixFilePermissionsMapper.map(0750),
                setOf(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ, GROUP_EXECUTE));
        assertEquals(posixFilePermissionsMapper.map(0753),
                setOf(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ, GROUP_EXECUTE, OTHERS_WRITE, OTHERS_EXECUTE));
        assertEquals(posixFilePermissionsMapper.map(0574),
                setOf(OWNER_READ, OWNER_EXECUTE, GROUP_READ, GROUP_WRITE, GROUP_EXECUTE, OTHERS_READ));
        assertEquals(posixFilePermissionsMapper.map(0544),
                setOf(OWNER_READ, OWNER_EXECUTE, GROUP_READ, OTHERS_READ));
        assertEquals(posixFilePermissionsMapper.map(0055),
                setOf(GROUP_READ, GROUP_EXECUTE, OTHERS_READ, OTHERS_EXECUTE));
    }
}