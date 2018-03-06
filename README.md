jackson-json-reference
==============

Parent:
[![Build Status](https://travis-ci.org/AndersDJohnson/jackson-json-reference.png)](https://travis-ci.org/AndersDJohnson/jackson-json-reference)
[![Codecov](https://img.shields.io/codecov/c/github/AndersDJohnson/jackson-json-reference.svg)](http://codecov.io/github/AndersDJohnson/jackson-json-reference)

* Core:&nbsp;[![Download Core](https://img.shields.io/maven-central/v/me.andrz.jackson/jackson-json-reference-core.svg) ][download]
* CLI:&nbsp;&nbsp;&nbsp;[![Download CLI](https://img.shields.io/maven-central/v/me.andrz.jackson/jackson-json-reference-cli.svg) ][download-cli]

[JSON Reference] implementation for Java, based on [Jackson]. Process references in JSON documents, such as in [JSON Schema]. Aims for but not limited to full [spec](#specs) compliance.

## Features

* Supports URLs & files.
* Relative & absolute reference URIs.
* Recursive expansion, with options for max depth and stop on circularity.
* Custom object mappers, allowing Jackson features like JSON comments, YAML, etc.
* Built-in support for YAML based on file extension detection.

## Specs

* [JSON Reference]&nbsp;([extended by JSON Schema][JSON Reference Extended])
* [JSON Pointer]
* [JSON Schema]&nbsp;([spec][JSON Schema Spec])

## Usage

### File
```java
File file = new File("src/test/resources/nest.json");
JsonNode node = (new JsonReferenceProcessor()).process(file);
```

### URL
```java
URL url = new URL("http://json-schema.org/schema");
JsonNode node = (new JsonReferenceProcessor()).process(url);
```

### Settings
```java
JsonReferenceProcessor processor = new JsonReferenceProcessor();

processor.setStopOnCircular(false); // default true

processor.setMaxDepth(2); // default 1, or set to -1 for no max depth

// Custom object mapper allowing comments.
processor.setMapperFactory(new ObjectMapperFactory() {
   @Override
   public ObjectMapper create(URL url) {
       //ObjectMapper objectMapper = DefaultObjectMapperFactory.instance.create(url);
       ObjectMapper objectMapper = new ObjectMapper();
       objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
       return objectMapper;
   }
});

JsonNode node = processor.process( /*...*/ );
```

### Output
```java
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("out.json"), node);
```


## Install

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>me.andrz.jackson</groupId>
        <artifactId>jackson-json-reference-core</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```

### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    compile 'me.andrz.jackson:jackson-json-reference-core:0.3.0'
}
```

### Manual

Download JAR(s) from Maven Central:
* Core:&nbsp;[![Download Core](https://img.shields.io/maven-central/v/me.andrz.jackson/jackson-json-reference-core.svg) ][download]
* CLI:&nbsp;&nbsp;&nbsp;[![Download CLI](https://img.shields.io/maven-central/v/me.andrz.jackson/jackson-json-reference-cli.svg) ][download-cli]

## License

See [LICENSE](LICENSE).

## Development

### Publishing

* http://central.sonatype.org/pages/ossrh-guide.html
* http://central.sonatype.org/pages/apache-maven.html#performing-a-release-deployment-with-the-maven-release-plugin
* https://oss.sonatype.org/#nexus-search;quick~jackson-json-reference
* https://search.maven.org/#search%7Cga%7C1%7Cjackson-json-reference

If you need to bump the version of all projects in the multi-project:

```
mvn versions:set -DnewVersion=2.50.1-SNAPSHOT
```

Then be sure your build is up to date:

```
mvn compile
```

Now, use the Release Plugin (http://maven.apache.org/maven-release/maven-release-plugin/usage.html):

```
mvn release:prepare -DdryRun=true
```

```
mvn release:prepare
```

If you mess up:

```
mvn release:clean
```

Else:

```
mvn release:perform
```

[Jackson]: https://github.com/FasterXML/jackson
[JSON Reference]: https://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03
[JSON Reference Extended]: https://tools.ietf.org/html/draft-zyp-json-schema-04#section-7.1
[JSON Pointer]: http://tools.ietf.org/html/rfc6901
[JSON Schema]: http://json-schema.org/
[JSON Schema Spec]: https://tools.ietf.org/html/draft-zyp-json-schema-04
[download]: https://repo1.maven.org/maven2/me/andrz/jackson/jackson-json-reference-core/0.3.0/jackson-json-reference-core-0.3.0.jar
[download-cli]: https://repo1.maven.org/maven2/me/andrz/jackson/jackson-json-reference-cli/0.3.0/jackson-json-reference-cli-0.3.0.jar
