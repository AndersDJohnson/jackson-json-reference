jackson-json-reference
==============

Based on specs:
 * [JSON Reference](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03)
 * [JavaScript Object Notation (JSON) Pointer](http://tools.ietf.org/html/rfc6901)


# Usage

## File
```java
File file = new File("src/test/resources/nest.json");
JsonNode node = (new JsonReference()).process(file);
```

## URL
```java
URL url = new URL("src/test/resources/nest.json");
JsonNode node = (new JsonReference()).process(url);
```

## Settings
```java
ObjectMapper mapper = new ObjectMapper();

JsonReference ref = new JsonReference(mapper);
ref.setStopOnCircular(false); // default true
ref.setMaxDepth(2); // default 1

JsonNode node = ref.process( /*...*/ );
```

## Output
```java
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("out.json"), node);
```
