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

import java.io.File;
import org.apache.commons.compress.archivers.StreamingNotSupportedException;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Archiver7zTest */
public class Archiver7zTest extends AbstractArchiverTest {
  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Override
  protected Archiver getArchiver() {
    return ArchiverFactory.createArchiver(ArchiveFormat.SEVEN_Z);
  }

  @Override
  protected File getArchive() {
    return new File(RESOURCES_DIR, "archive.7z");
  }

  @Test
  public void extract_properlyExtractsArchiveStream() throws Exception {
    // 7z does not allow streaming
    expectedException.expectCause(
        CoreMatchers.<Throwable>instanceOf(StreamingNotSupportedException.class));
    super.extract_properlyExtractsArchiveStream();
  }
}
