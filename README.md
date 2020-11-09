algorithmia-java
================

Java client for accessing Algorithmia's algorithm marketplace and data APIs.

<a href="http://www.javadoc.io/doc/com.algorithmia/algorithmia-client">Algorithmia Client Java Docs <i class="fa fa-external-link"></i></a>

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
String jsonWords = "[\"transformer\", \"terraforms\", \"retransform\"]";
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
    System.out.println("Algorithm Exception: " ex.getMessage());
    System.out.println(ex.stacktrace);
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

The Algorithmia Java client also provides a way to manage both Algorithmia hosted data
and data from Dropbox or S3 accounts that you've connected to you Algorithmia account.

This client provides a `DataFile` type (generally created by `client.file(uri)`)
and a `DataDir` type (generally created by `client.dir(uri)`) that provide
methods for managing your data.

### Create directories

Create directories by instantiating a `DataDirectory` object and calling `create()`:

```java
DataDirectory robots = client.dir("data://.my/robots");
robots.create();

DataDirectory dbxRobots = client.dir("dropbox://robots");
dbxRobots.create();
```

### Upload files to a directory

Upload files by calling `put` on a `DataFile` object, or by calling `putFile` on a `DataDirectory` object.

```java
DataDirectory robots = client.dir("data://.my/robots");

// Upload local file
robots.putFile(new File("/path/to/Optimus_Prime.png"));
// Write a text file
robots.file("Optimus_Prime.txt").put("Leader of the Autobots");
// Write a binary file
robots.file("Optimus_Prime.key").put(new byte[] { (byte)0xe0, 0x4f, (byte)0xd0, 0x20 });
```

### Download contents of file

Download files by calling `getString`, `getBytes`, or `getFile` on a DataFile object:

```java
DataDirectory robots = client.dir("data://.my/robots");

// Download file and get the file handle
File t800File = robots.file("T-800.png").getFile();

// Get the file's contents as a string
String t800Text = robots.file("T-800.txt").getString();

// Get the file's contents as a byte array
byte[] t800Bytes = robots.file("T-800.png").getBytes();
```

### Delete files and directories

Delete files and directories by calling `delete` on their respective `DataFile` or `DataDirectory` object.
`DataDirectories` take an optional `force` parameter that indicates whether the directory should be deleted
if it contains files or other directories.

```java
client.file("data://.my/robots/C-3PO.txt").delete();
client.dir("data://.my/robots").delete(false);
```

### List directory contents

Iterate over the contents of a directory using the iterator returned by calling `files`, or `dirs` on a `DataDirectory` object:

```java
// List top level directories
DataDirectory myRoot = client.dir("data://.my");
for(DataDirectory dir : myRoot.dirs()) {
    System.out.println("Directory " + dir + " at URL " + dir.url());
}

// List files in the 'robots' directory
DataDirectory robots = client.dir("data://.my/robots");
for(DataFile file : robots.files()) {
    System.out.println("File " + file + " at URL: " + file.url());
}
```

### Manage directory permissions

Directory permissions may be set when creating a directory, or may be updated on already existing directories.

```java
DataDirectory fooLimited = client.dir("data://.my/fooLimited");

// Create the directory as private
fooLimited.create(DataAcl.PRIVATE);

// Update a directory to be public
fooLimited.updatePermissions(DataAcl.PUBLIC);

// Check a directory's permissions
if (fooLimited.getPermissions().getReadPermissions() == DataAclType.PRIVATE) {
    System.out.println("fooLimited is private");
}
```

### Java Algo development category API's

| Name  | Parameters | Example |
| :----- | :---------- | :------- |
| Create Algorithm | String userName - Your Algorithmia user name.<br>String requestString - JSON payload for the Algorithm you wish to create. | `Algorithm newAlgorithm = Algorithmia.client(key).createAlgo(userName, requestString);` |
| Get Algorithm | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm. | `Algorithm algorithm = Algorithmia.client(key).getAlgo(userName, algoName);` |
| Compile Algorithm | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm. | `Algorithm algorithm = Algorithmia.client(key).compileAlgo(userName, algoName);` |
| Publish Algorithm | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm.<br>String requestString - JSON payload for the Algorithm you wish to publish. | `Algorithm newAlgorithm = Algorithmia.client(key).publishAlgo(userName, algoName, requestString);` |
| List Algorithm Versions | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm.<br>Boolean callable - Whether to return only public or private algorithm versions.<br>Integer limit - Items per page.<br>Boolean published - Whether to return only versions that have been published.<br>String marker - Marker for pagination. | `AlgorithmVersionsList algoList = Algorithmia.client(key).listAlgoVersions(userName, algoName, callable, limit, published, marker)` |
| Update Algorithm | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm.<br>String requestString - JSON payload for the Algorithm you wish to create. | `Algorithm newAlgorithm = Algorithmia.client(key).updateAlgo(userName, algoName, requestString);` |
| Execute Algorithm | String algoName - The name address of the algorithm. | `Algorithm algo = client.algo("algo://demo/Hello/0.1.1");`<br>`AlgoResponse result = algo.pipe("HAL 9000");` |
| Get Algorithm Build Logs | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm.<br>String buildId - The id of the build to retrieve logs. | `BuildLogs buildLogs = Algorithmia.client(key).getAlgoBuildLogs(userName, algoName, buildId)` |
| Create Directory | String path - Path to a data directory. | `DataDirectory robots = client.dir("data://.my/robots");`<br>`robots.create();` |
| List Directory Contents | String path - Path to a data directory. | `DataDirectory myRoot = client.dir("data://.my");`<br>`for(DataDirectory dir : myRoot.dirs()) { System.out.println("Directory " + dir + " at URL " + dir.url()); }` |
| Update Directory | File file - A file to put into this data directory. | `DataDirectory robots = client.dir("data://.my/robots");`<br>`robots.putFile(new File("/path/to/Optimus_Prime.png"));` |
| Delete Directory | boolean forceDelete  - Forces deletion of the directory if it contains files. | `client.dir("data://.my/robots").delete(false);` |
| Upload File | File file - file the file to upload data from. | `robots.putFile(new File("/path/to/Optimus_Prime.png"));` |
| Verify File Existence | - | `if(file.exists()) { file.delete(); } ` |
| Download File | - | `File t800File = robots.file("T-800.png").getFile();` |
| Report Insights | String input - JSON payload key-value pairs | `AlgorithmiaInsights insightsResponse = Algorithmia.client(defaultKey).reportInsights(input);` |

### Java CICD Automation and Admin Automation category API's

| Name  | Parameters | Example |
| :----- | :---------- | :------- |
| List Algorithm Builds | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm.<br>Integer limit - Items per page.<br>String marker - Marker for pagination. | `AlgorithmBuildsList algoList = Algorithmia.client(defaultKey).listAlgoBuilds(userName, algoName, ?, ?);` |
| Get Algorithm Build | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm.<br>String buildId - The id of the build to retrieve. | `Algorithm.Build returnedBuild = Algorithmia.client(defaultKey).getAlgoBuild(userName, algoName, buildId);` |
| Delete Algorithm | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm. | `HttpResponse response = Algorithmia.client(defaultKey).deleteAlgo(userName, algoName);` |
| Get Algorithm SCM status | String userName - Your Algorithmia user name.<br>String algoName - The name address of the algorithm. | `AlgorithmSCMStatus scmStatus = Algorithmia.client(defaultKey).getAlgoSCMStatus(userName, algoName);` |
| Get SCM | String scmId - The id of scm to retrive | `Algorithm.SCM scm = Algorithmia.client(defaultKey).getSCM(scmId);` |
| List Cluster SCM’s | - | `AlgorithmSCMsList algorithmSCMsList = Algorithmia.client(defaultKey).listSCMs();` |
