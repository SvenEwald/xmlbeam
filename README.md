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
    
    // This is a getter for the attribute "type"
    @XBRead("/xml/example/content/@type")
    String getType();
    
    // This is a getter and a setter for the value of the element "content"
    @XBAuto("/xml/example/content")
    XBAutoValue<String> content();
}

Example example = new XBProjector().io().file("example.xml").read(Example.class);
String type = example.getType(); // "foo"
String content = example.content().get(); // "bar"
example.content().set("new value");
```

Or, direct access via XPath enabled collection types:
```Java
Map<String,String> map = new XBProjector().io().file("example.xml").readAsMapOf(String.class);
String type = map.get("/xml/example/content/@type");
String content = map.get("/xml/example/content");
map.put("/xml/example/content","new value");
```

Learn more on [xmlbeam.org](https://xmlbeam.org)
