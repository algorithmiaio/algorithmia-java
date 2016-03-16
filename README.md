algorithmia-java
================

Java client for accessing Algorithmia's algorithm marketplace and data APIs.

For API documentation, see the [JavaDocs](https://algorithmia.com/docs/lang/java)

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

# Calling Algorithms

Algorithms are called with the `pipe` method:

```java
Algorithm addOne = client.algo("docs/JavaAddOne");
AlgoResponse response = addOne.pipe(41);
Integer result = response.as(new TypeToken<Integer>(){});
Double durationInSeconds = response.getMetadata().duration;
```

You can also set options (query parameters in the API spec) on calls.  There are several approaches to do this, and they are all equivalent
```java
// Helper methods for specific parameters in the API spec:
Algorithm addOne = client.algo("docs/JavaAddOne")
                         .setTimeout(5, TimeUnit.MINUTES)
                         .setStdout(false)
                         .setOutputType(AlgorithmOutputType.RAW);

// Or, set query parameter string directly:
Algorithm addOne = client.algo("docs/JavaAddOne")
                         .setOption("timeout", "300")
                         .setOption("stdout","false")
                         .setOption("output","raw");
                         
// Or, pass in a Map of options:
HashMap<String, String> options = new HashMap<>();
options.put("timeout", "300");
options.put("stdout", "false");
options.put("output", "raw");
Algorithm addOne = client.algo("docs/JavaAddOne").setOptions(options);

// These are all equivalant and do not impact the way an algorithm is called:
AlgoResponse response = addOne.pipe(41);
```

Algorithms called with anything other than the default AlgorithmOutputType have special responses:
```java
// AlgorithmOutputType.RAW - does not contain metadata and result is always a string
Algorithm rawAddOne = client.algo("docs/JavaAddOne").setOutputType(AlgorithmOutputType.RAW);
AlgoResponse response = rawAddOne.pipe(41);
response.getRawOutput(); // "41"
// Calling any other method on this response object will throw an exception

// AlgorithmOutputType.VOID - performs an asynchronous request and algorithm output is unaccessible
Algorithm voidAddOne = client.algo("docs/JavaAddOne").setOutputType(AlgorithmOutputType.VOID);
AlgoResponse response = voidAddOne.pipe(41);
AlgoAsyncResponse asyncResponse = response.getAsyncResponse();
asyncResponse.getAsyncProtocol(); // "void"
asyncResponse.getRequestId();     // "req-abcd-efgh" 
```


# Working with Data

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

