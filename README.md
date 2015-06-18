algorithmia-java
================

Java client for accessing Algorithmia's algorithm marketplace and data APIs.

[![Build Status](https://img.shields.io/shippable/557f23a8edd7f2c052184a2d/master.svg)](https://app.shippable.com/projects/557f23a8edd7f2c052184a2d)

TODO: badge for maven central version

TODO: link to java docs

# Getting started

algorithmia-java is published to Maven Central. To add as a dependency:

<dependency>
  <groupId>com.algorithmia</groupId>
  <artifactId>algorithmia-client</artifactId>
  <version>0.9.0</version>
</dependency>

Instantiate a client using your API Key:

````java
Algorithmia algorithmia = new Algorithmia(apiKey);
````

Note: API key may be ommitted only when making calls from algorithms running on the Algorithmia cluster

# Calling Algorithms

Algorithms are called with the `pipe` method:

````java
Algorithm addOne = algorithmia.algo("docs/JavaAddOne");
AlgoResponse response = addOne.pipe(72);
int result = response.as(new TypeToken<int>(){});
Double durationInSeconds = response.getMetadata().duration;
````

# Working with Data

Manage your data stored within Algorithmia:

````java
// Create a directory "foo"
DataDirectory foo = algorithmia.dir("/.my/foo");
foo.create();

// Upload files to "foo" directory
foo.file("sample.txt").put("sample text contents");
foo.file("binary_file").put(new byte[] { (byte)0xe0, 0x4f, (byte)0xd0, 0x20 });
foo.putFile(new File("/path/to/myfile"));

// List files in "foo"
for(DataFile file : foo.getFileIter()) {
    System.out.println(file.toString() " at URL: " + file.url());
}

// Get contents of files
String sampleText = foo.file("sample.txt").getString();
byte[] binaryContent = foo.file("binary_file").getBytes();
File tempFile = foo.file("myfile").getFile();

// Delete files and directories
foo.file("sample.txt").delete();
foo.delete(true); // true implies force deleting the directory and its contents
````

