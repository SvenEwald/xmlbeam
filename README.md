About

 This is a Java XML library with an extraordinary expressive API. 
 By using XPath for read and write operations, many operations take only one line of Java code.   
 This is how it looks:

```XML
<xml>
   <example>
      <content type="foo" >bar</content>
   </example>
</xml>
```

Access XML content in an object oriented way:
```Java
public interface Example {
 
    @XBRead("/xml/example/content")
    String getContent();
 
    @XBRead("/xml/example/content/@type")
    String getType();
}

Example example = new XBProjector().io().file("example.xml").read(Example.class);
String content = example.getContent();
String type = example.getType();
```

Or, direct access via XPath enabled collection types:
```Java
Map<String,String> map = new XBProjector().io().file("example.xml").readAsMapOf(String.class);
String content = map.get("/xml/example/content");
String type = map.get("/xml/example/content/@type");
```

Learn more on [xmlbeam.org](https://xmlbeam.org)