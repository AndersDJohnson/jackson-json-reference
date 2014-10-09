jackson-json-reference
==============

Based on specs:
 * [JSON Reference](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03)
 * [JavaScript Object Notation (JSON) Pointer](http://tools.ietf.org/html/rfc6901)

```java
ObjectMapper mapper = new ObjectMapper();

JsonNode node = mapper.readTree(new File("src/test/resources/nest.json"));

JsonContext context = new JsonContext();
context.setNode(node);
context.setPath("src/test/resources");
JsonReference.process(context);

mapper.writeValue(new File("out.json"), node);
```
