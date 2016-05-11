algorithmia-java
================

Java client for accessing Algorithmia's algorithm marketplace and data APIs.

<a href="http://www.javadoc.io/doc/com.algorithmia/algorithmia-client">Algorithmia Client Java Docs <i class="fa fa-external-link"></i></a>

[![Run Status](https://api.shippable.com/projects/557f23a8edd7f2c052184a2d/badge?branch=master)](https://app.shippable.com/projects/557f23a8edd7f2c052184a2d)

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
- API key may be omitted only when making calls from algorithms running on the Algorithmia cluster
- Using version range `[,1.1.0)` is recommended as it implies using the latest backward-compatible bugfixes.

## Calling Algorithms

Algorithms are called with the `pipe` method using
any input that can be serialized to JSON, or binary byte data.

```java
Algorithm addOne = client.algo("docs/JavaAddOne");
AlgoResponse response = addOne.pipe(41);
Integer result = response.as(new TypeToken<Integer>(){});
Double durationInSeconds = response.getMetadata().duration;
```

If you already have serialzied JSON, you can call call `pipeJson` instead:

```java
Algorithm foo = client.algo("")
String jsonWords = "[\"transformer\", \"terraforms\", \"retransform\"]"
AlgoResponse response = addOne.pipeJson(jsonWords)
```

You can also set options (query parameters in the API spec) on calls.  There are several approaches to do this, and they are all equivalent
```java
// Helper methods for specific parameters in the API spec:
Algorithm addOne = client.algo("docs/JavaAddOne")
                         .setTimeout(5, TimeUnit.MINUTES)
                         .setStdout(false);

AlgoResponse response = addOne.pipe(41);
```

### Casting results in Java


> For an algorithm that returns a string:

```java
stringResult.as(new TypeToken<String>(){});
```

> For an algorithm that returns an array of strings:

```java
stringArrayResult.as(new TypeToken<List<String>>(){});
```

> For an algorithm that returns a custom class, cast the result to that class:

```java
class CustomClass {
    int maxCount;
    List<String> items;
}
customClassResult.as(new TypeToken<CustomClass>(){});
```

> For debugging, it is often helpful to get the JSON String representation of the result:

```java
anyResult.asJsonString();
```

In order to cast the result to a specific type, call `.as()` with a TypeToken.
On the right pane, you'll find examples of how to do this to return a string, an array of strings, and a custom class.


## Working with Data

Manage your data stored within Algorithmia:

```java
// Create a directory "foo"
DataDirectory foo = client.dir("data://.my/foo");
foo.create();

// Create a directory with specific ACL
DataDirectory fooLimited = client.dir("data://.my/fooLimited");
fooLimited.create(DataAcl.PRIVATE);

// Or, update the directory's ACL after creation
fooLimited.updatePermissions(DataAcl.PRIVATE);

// View the directory's permissions
fooLimited.getPermissions().getReadPermissions() == DataAclType.PRIVATE

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

