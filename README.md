Archiver
========
A simple facade for [org.apache.commons.compress]

  [org.apache.commons.compress]: http://commons.apache.org/proper/commons-compress/

Usage
-----
### Using the ArchiverFactory
Create a new Archiver to handle zip archives

    :::java
    Archiver archiver = ArchiverFactory.createArchiver(ArchiverFactory.ZIP);


Create a new Archiver to handle tar archives with gzip compression

    :::java
    Archiver archiver = ArchiverFactory.createArchiver(ArchiverFactory.ZIP, ArchiverFactory.GZIP);

### Using Archivers
#### Extract
To extract the zip archive `/home/jack/archive.zip` to `/home/jack/archive`:

    :::java
    File archive = new File("/home/jack/archive.zip");
    File destination = new File("/home/jack/archive");
    
    Archiver archiver = ArchiverFactory.createArchiver(ArchiverFactory.ZIP);
    archiver.extract(archive, destination);

#### Create
To create a new tar archive with gzip compression `archive.tar.gz` in `/home/jack/` containing the entire directory `/home/jack/archive`

    :::java
    String archiveName = "archive";
    File destination = new File("/home/jack/");
    File source = new File("/home/jack/archive");
    
    Archiver archiver = ArchiverFactory.createArchiver(ArchiverFactory.ZIP, ArchiverFactory.GZIP);
    File archive = archiver.create(archiveName, destination, source);

notice that you can omit the filename extension in the archive name, as it will be appended by the archiver automatically if it is missing.