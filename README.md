jackson-json-reference
==============

Based on specs:
 * [JSON Reference](http://tools.ietf.org/html/draft-pbryan-zyp-json-ref-03)
 * [JavaScript Object Notation (JSON) Pointer](http://tools.ietf.org/html/rfc6901)


```java
File file = new File("src/test/resources/nest.json");

JsonContext context = JsonContext.fromFile(file);
JsonReference.process(context);
JsonNode node = context.getNode();

ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("out.json"), node);
```
