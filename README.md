jackson-json-reference
==============

Based on specs:
 * [JSON Reference](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03)
 * [JavaScript Object Notation (JSON) Pointer](http://tools.ietf.org/html/rfc6901)


# Usage

## File
```java
File file = new File("src/test/resources/nest.json");
JsonNode node = JsonReference.process(file);
```

## URL
```java
URL url = new URL("src/test/resources/nest.json");
JsonNode node = JsonReference.process(url);
```

## Output
```java
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("out.json"), node);
```
