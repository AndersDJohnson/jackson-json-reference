jackson-json-reference
==============

```java
JsonNode node = JsonReference.process("{}");
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(new File("out.json"), node);
```
