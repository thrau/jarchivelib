/**
 * Copyright 2013 Thomas Rausch
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rauschig.jarchivelib;

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

import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.rauschig.jarchivelib.FileModeMapper.PosixFilePermissionsMapper;

public class PosixFilePermissionsMapperTest {

  private final PosixFilePermissionsMapper posixFilePermissionsMapper = new PosixFilePermissionsMapper();

  private Set<PosixFilePermission> setOf(final PosixFilePermission... posixFilePermissions) {
    return new HashSet<>(Arrays.asList(posixFilePermissions));
  }

  @Test
  public void noPermissions() {
    assertEquals(this.posixFilePermissionsMapper.map(0000), this.setOf());
  }

  @Test
  public void allPermissions() {
    assertEquals(this.posixFilePermissionsMapper.map(0777), this.setOf(PosixFilePermission.values()));
  }

  @Test
  public void ownerPermissions() {
    assertEquals(this.posixFilePermissionsMapper.map(0400), this.setOf(OWNER_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0200), this.setOf(OWNER_WRITE));
    assertEquals(this.posixFilePermissionsMapper.map(0100), this.setOf(OWNER_EXECUTE));

    assertEquals(
        this.posixFilePermissionsMapper.map(0700), this.setOf(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE));
    assertEquals(this.posixFilePermissionsMapper.map(0600), this.setOf(OWNER_WRITE, OWNER_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0500), this.setOf(OWNER_EXECUTE, OWNER_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0300), this.setOf(OWNER_EXECUTE, OWNER_WRITE));
  }

  @Test
  public void groupPermissions() {
    assertEquals(this.posixFilePermissionsMapper.map(0040), this.setOf(GROUP_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0020), this.setOf(GROUP_WRITE));
    assertEquals(this.posixFilePermissionsMapper.map(0010), this.setOf(GROUP_EXECUTE));

    assertEquals(
        this.posixFilePermissionsMapper.map(0070), this.setOf(GROUP_READ, GROUP_WRITE, GROUP_EXECUTE));
    assertEquals(this.posixFilePermissionsMapper.map(0060), this.setOf(GROUP_WRITE, GROUP_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0050), this.setOf(GROUP_EXECUTE, GROUP_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0030), this.setOf(GROUP_EXECUTE, GROUP_WRITE));
  }

  @Test
  public void othersPermissions() {
    assertEquals(this.posixFilePermissionsMapper.map(0004), this.setOf(OTHERS_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0002), this.setOf(OTHERS_WRITE));
    assertEquals(this.posixFilePermissionsMapper.map(0001), this.setOf(OTHERS_EXECUTE));

    assertEquals(
        this.posixFilePermissionsMapper.map(0007),
        this.setOf(OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE));
    assertEquals(this.posixFilePermissionsMapper.map(0006), this.setOf(OTHERS_WRITE, OTHERS_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0005), this.setOf(OTHERS_EXECUTE, OTHERS_READ));
    assertEquals(this.posixFilePermissionsMapper.map(0003), this.setOf(OTHERS_EXECUTE, OTHERS_WRITE));
  }

  @Test
  public void permissionsSameForAll() {
    assertEquals(
        this.posixFilePermissionsMapper.map(0444), this.setOf(OTHERS_READ, GROUP_READ, OWNER_READ));
    assertEquals(
        this.posixFilePermissionsMapper.map(0222), this.setOf(OTHERS_WRITE, GROUP_WRITE, OWNER_WRITE));
    assertEquals(
        this.posixFilePermissionsMapper.map(0111),
        this.setOf(OTHERS_EXECUTE, GROUP_EXECUTE, OWNER_EXECUTE));
  }

  @Test
  public void permissionCombinations() {
    assertEquals(
        this.posixFilePermissionsMapper.map(0750),
        this.setOf(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ, GROUP_EXECUTE));
    assertEquals(
        this.posixFilePermissionsMapper.map(0753),
        this.setOf(
            OWNER_READ,
            OWNER_WRITE,
            OWNER_EXECUTE,
            GROUP_READ,
            GROUP_EXECUTE,
            OTHERS_WRITE,
            OTHERS_EXECUTE));
    assertEquals(
        this.posixFilePermissionsMapper.map(0574),
        this.setOf(OWNER_READ, OWNER_EXECUTE, GROUP_READ, GROUP_WRITE, GROUP_EXECUTE, OTHERS_READ));
    assertEquals(
        this.posixFilePermissionsMapper.map(0544),
        this.setOf(OWNER_READ, OWNER_EXECUTE, GROUP_READ, OTHERS_READ));
    assertEquals(
        this.posixFilePermissionsMapper.map(0055),
        this.setOf(GROUP_READ, GROUP_EXECUTE, OTHERS_READ, OTHERS_EXECUTE));
  }
}
