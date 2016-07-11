algorithmia-java
================

Java client for accessing Algorithmia's algorithm marketplace and data APIs.

<a href="http://www.javadoc.io/doc/com.algorithmia/algorithmia-client">Algorithmia Client Java Docs <i class="fa fa-external-link"></i></a>

[![Run Status](https://api.shippable.com/projects/557f23a8edd7f2c052184a2d/badge?branch=master)](https://app.shippable.com/projects/557f23a8edd7f2c052184a2d)

[![Latest Release](https://img.shields.io/maven-central/v/com.algorithmia/algorithmia-client.svg)](http://repo1.maven.org/maven2/com/algorithmia/algorithmia-client/)

## Getting started

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

Now you are ready to call algorithms.

## Calling Algorithms

The following examples of calling algorithms are organized by type of input/output which vary between algorithms.

Note: a single algorithm may have different input and output types, or accept multiple types of input, so consult the algorithm's description for usage examples specific to that algorithm.

### Text input/output

Call an algorithm with text input by simply passing a string into its `pipe` method.
If the algorithm output is text, call the `asString` method on the response.

```java
Algorithm algo = client.algo("algo://demo/Hello/0.1.1");
AlgoResponse result = algo.pipe("HAL 9000");
System.out.println(result.asString());
// -> Hello HAL 9000
```

### JSON input/output

Call an algorithm with JSON input by simply passing in a type that can be serialized to JSON,
including most plain old java objects and collection types.
If the algorithm output is JSON, call the `as` method on the response with a `TypeToken`
containing the type that it should be deserialized into:

```java
Algorithm algo = client.algo("algo://WebPredict/ListAnagrams/0.1.0");
List<String> words = Arrays.asList(("transformer", "terraforms", "retransform");
AlgoResponse result = algo.pipe(words);
// WebPredict/ListAnagrams returns an array of strings, so cast the result:
List<String> anagrams = result.as(new TypeToken<List<String>>(){});
// -> List("transformer", "retransform")
```

Alternatively, you may work with raw JSON input by calling `pipeJson`,
and raw JSON output by calling `asJsonString` on the response:

```java
String jsonWords = "[\"transformer\", \"terraforms\", \"retransform\"]"
AlgoResponse result2 = algo.pipeJson(jsonWords);
String anagrams = result2.asJsonString();
// -> "[\"transformer\", \"retransform\"]"

Double durationInSeconds = response.getMetadata().duration;
```


### Binary input/output

Call an algorithm with binary input by passing a `byte[]` into the `pipe` method.
If the algorithm response is binary data, then call the `as` method on the response with a `byte[]` `TypeToken`
to obtain the raw byte array.

```java
byte[] input = Files.readAllBytes(new File("/path/to/bender.jpg").toPath());
AlgoResponse result = client.algo("opencv/SmartThumbnail/0.1").pipe(input);
byte[] buffer = result.as(new TypeToken<byte[]>(){});
// -> [byte array]
```

### Error handling

API errors will result in the call to `pipe` throwing `APIException`.
Errors that occur durring algorithm execution will result in `AlgorithmException` when attempting to read the response.

```java
Algorithm algo = client.algo('util/whoopsWrongAlgo')
try {
    AlgoResponse result = algo.pipe('Hello, world!');
    String output = result.asString();
} catch (APIException ex) {
    System.out.println("API Exception: " ex.getMessage());
} catch (AlgorithmException ex) {
    System.out.println("Algorithm Exception: " ex.getMessage() + "\n" + ex.stacktrace);
}
```

### Request options

The client exposes options that can configure algorithm requests.
This includes support for changing the timeout or indicating that the API should include stdout in the response.:

```java
Algorithm algo = client.algo("algo://demo/Hello/0.1.1")
                         .setTimeout(1, TimeUnit.MINUTES)
                         .setStdout(true);
AlgoResponse result = algo.pipe("HAL 9000");
Double stdout = response.getMetadata().stdout;
```

Note: `setStdout(true)` is ignored if you do not have access to the algorithm source.

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

