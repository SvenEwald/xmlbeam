                 ------
                 XMLBeam
                 ------
                 Sven Ewald
                 ------
                  2012
                 ------


About

 This is a Java XML CRUD library and a very flexible alternative to data binding.
 It works by "projecting" parts of a XML DOM tree into Java objects and back.   

Motivation: 

 What's wrong with Java XML data binding?
 
 Data binding needs the structure of XML documents being mirrored to Java object structures.
 That means your Java API is determined by XML.
 
 XMLBeam helps to avoid API changes by utilizing a mapping layer of tiny XPath expressions.
 Don't worry, you won't need a deep knowledge of XPath.

XMLBeam Feature List

 The motivation behind XMLBeam is to provide stable Java APIs.
 But there is a lot more in it:

 * Tiny foot print, dependency free
 
   The XMLBeam jar weights about 165kb and does not have any runtime dependency.  
   So you won't get any transitive dependencies.

 * Glue code free framework
 
   You don't need to write more than the declaration of a projection, not even a Java class. 
   But you get a statically typed Java API and work with real Java objects that support the equals() and hashCode() methods and can even be serialized.  

 * Declarative document origins
 
   Optionally you can let XMLBeam read and write your XML data.
   A single URL annotation safes quite some code.
   Input is supported for XML in a String, resources, InputStream, File and HTTP(S) GET (with basic authorization).
   Output is supported for String, OutputStream, File and HTTP(S) POST (with basic authorization).
    
 * Dynamic projections
 
   Data binding is static. Projections may contain parameters and change during runtime which is a very powerful tool
   to write expressive but stable Java code. (See tutorial e03 where this feature is introduced.)
   
 * Bidirectional projections

   Changing document values and creating or deleting elements are just as easy as reading data.
   Declaring a setter in a projection makes an attribute, an element or a list of elements writable.

 * Projections to external documents
 
   You may unite the access to multiple documents in one projection interface.
   This is one key feature to provide a stable Java API. 
   Just add the URL annotation to a method declaration.
   The document will be fetched each time the method is called and the related XPath will be evaluated on the external document.
   (See example e09 for a real life use case.)  

 * Sub projections
 
   You can define sub projections to parts of XML documents. This would reflect a XML element into Java,
   or just group arbitrary values to a single Java structure. (See tutorial e01 for an example of grouping 
   data and e07 for element access example.)
   
 * Add behavior by adding Mixins
 
   A Mixin is a Java class that handles certain method calls for a projection. Kind of an interface with behavior.
 
   Projections can extend a certain interface (such as Comparable in tutorial e07).
   This way you can add behavior (e.g validation code) to projections. 
  
 * Painless DOM access
 
   You may at any time access and modify the DOM behind the scenes. You will find convenient access methods for documents, elements and attributes.
   Changes will reflect automatically in projections, because XMLBeam uses the DOM as back end. 
   
 * Easy name space handling
 
   Name spaces of elements and attributes are directly usable in projections by default. They don't leak into your Java API.

 * Simple customization
 
   Just inject your own XML factory to switch to other XML parsers, transformers or XPath implementations.
   Change the type conversion rules or even add custom XML data types.
   
 * API Mimicry
 
   XMLBeam is so flexible that it can even be used to mimicry other APIs.
   (See tutorial e08 where the DOM4J API is imitated.)
   
   
You can not use XMLBeam
 
 * If your XML documents doesn't fit into memory. (XMLBeam works with DOM parsers only.)

 * If you're on performance by hook or by crook. (XMLBeam surely is not the fastest way to handle XML.)
  
What XMLBeam does not do

 * Parsing XML. It uses the parser that's included within the JRE (since Java6). You may specify other parsers.
 
 * Building or transforming XML. It utilizes the JRE default DocumentBuilder and Transformers. Again you are able to change this easily.
 

