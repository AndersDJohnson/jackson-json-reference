jackson-json-reference
==============

JSON Reference implementation. Process references in JSON documents. Aims for but not limited to full spec compliance.

* Supports URLs & files.
* Relative & absolute reference URIs.
* Recursive expansion, with options for max depth and stop on circularity.

Specs:

* [JSON Reference](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03)
* [JSON Pointer](http://tools.ietf.org/html/rfc6901)


# Usage

## File
```java
File file = new File("src/test/resources/nest.json");
JsonNode node = (new JsonReferenceProcessor()).process(file);
```

## URL
```java
URL url = new URL("http://json-schema.org/schema");
JsonNode node = (new JsonReferenceProcessor()).process(url);
```

## Settings
```java
ObjectMapper mapper = new ObjectMapper();

JsonReference processor = new JsonReferenceProcessor(mapper);
processor.setStopOnCircular(false); // default true
processor.setMaxDepth(2); // default 1

JsonNode node = processor.process( /*...*/ );
```

## Output
```java
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("out.json"), node);
```

# License

See `LICENSE`.
