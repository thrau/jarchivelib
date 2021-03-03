package org.rauschig.jarchivelib;

import java.io.File;
import java.io.IOException;

public class DebTest {

  public static void main(String[] args) throws IOException {
    File f = new File("vlc_2.1.2-2build2_amd64.deb");
    final String extension = ".deb";
    final Archiver archiver = ArchiverFactory.createArchiver("ar");
    archiver.extract(f, new File(System.getProperty("user.dir") + "/result"));
  }
}
