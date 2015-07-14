algorithmia-java
================

Java client for accessing Algorithmia's algorithm marketplace and data APIs.

For API documentation, see the [JavaDocs](https://algorithmia.com/docs/lang/java)

[![Build Status](https://api.shippable.com/projects/557f23a8edd7f2c052184a2d/badge/master)](https://app.shippable.com/projects/557f23a8edd7f2c052184a2d)

[![Latest Release](https://img.shields.io/maven-central/v/com.algorithmia/algorithmia-client.svg)](http://repo1.maven.org/maven2/com/algorithmia/algorithmia-client/)

# Getting started

The Algorithmia java client is published to Maven central and can be added as a dependency via:

```xml
<dependency>
  <groupId>com.algorithmia</groupId>
  <artifactId>algorithmia-client</artifactId>
  <version>[,1.1.0)</version>
</dependency>
```

Instantiate a client using your API Key:

```java
AlgorithmiaClient client = Algorithmia.client(apiKey);
```

Notes:
- API key may be ommitted only when making calls from algorithms running on the Algorithmia cluster
- Using version range `[,1.1.0)` is recommended as it implies using the latest backward-compatible bugfixes.

# Calling Algorithms

Algorithms are called with the `pipe` method:

```java
Algorithm addOne = client.algo("docs/JavaAddOne");
AlgoResponse response = addOne.pipe(72);
Integer result = response.as(new TypeToken<Integer>(){});
Double durationInSeconds = response.getMetadata().duration;
```

# Working with Data

Manage your data stored within Algorithmia:

```java
// Create a directory "foo"
DataDirectory foo = client.dir("data://.my/foo");
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
```

