jarchivelib
===========

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.rauschig/jarchivelib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.rauschig/jarchivelib/)
[![Build Status](https://travis-ci.org/thrau/jarchivelib.svg?branch=master)](https://travis-ci.org/thrau/jarchivelib)
[![Coverage Status](https://coveralls.io/repos/thrau/jarchivelib/badge.svg)](https://coveralls.io/r/thrau/jarchivelib)

A simple archiving and compression library for Java that provides a thin and easy-to-use API layer on top of the
powerful and feature-rich [org.apache.commons.compress].

  [org.apache.commons.compress]: http://commons.apache.org/proper/commons-compress/

Usage
-----
### Using the ArchiverFactory
Create a new Archiver to handle zip archives

```java
Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
```


Create a new Archiver to handle tar archives with gzip compression

```java
Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
```


Alternatively you can use string representations of the archive and compression types.

```java
Archiver archiver = ArchiverFactory.createArchiver("zip");
```

The ArchiveFactory can also detect archive types based on file extensions and hand you the correct Archiver. This
example returns an Archiver instance that handles tar.gz files. (It would also recognize the `.tgz` extension)

```java
Archiver archiver = ArchiverFactory.createArchiver(new File("archive.tar.gz"));
```

### Using Archivers
#### Extract
To extract the zip archive `/home/jack/archive.zip` to `/home/jack/archive`:

```java
File archive = new File("/home/jack/archive.zip");
File destination = new File("/home/jack/archive");

Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);
archiver.extract(archive, destination);
```

#### Create
To create a new tar archive with gzip compression `archive.tar.gz` in `/home/jack/` containing the entire directory `/home/jack/archive`

```java
String archiveName = "archive";
File destination = new File("/home/jack");
File source = new File("/home/jack/archive");

Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
File archive = archiver.create(archiveName, destination, source);
```

notice that you can omit the filename extension in the archive name, as it will be appended by the archiver automatically if it is missing.


#### Stream

To access the contents of an archive as a Stream, rather than extracting them directly onto the filesystem

```java
ArchiveStream stream = archiver.stream(archive);
ArchiveEntry entry;

while((entry = stream.getNextEntry()) != null) {
    // access each archive entry individually using the stream
    // or extract it using entry.extract(destination)
    // or fetch meta-data using entry.getName(), entry.isDirectory(), ...
}
stream.close();
```

Dependencies
------------

* commons-compress(tm) 1.21


Compatibility
-------------

* Java 7, 8, 9, 10, 14
* Currently only tested for *nix file systems.

### OSGi

jarchivelib compiles to a bundle and is OSGi compatible

### jarchivelib 0.8.x and below

* Java 6 and 7


Known limitations
-----------------

* Permissions are not stored when creating archives
* There is no support for Windows permissions
* JAR files are treated like streamed zip files and can not restore permissions
