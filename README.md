                 ------
                 XMLBeam
                 ------
                 Sven Ewald
                 ------
                  2012
                 ------


About

 This is a Java library to project parts of a XML DOM tree into Java objects as an alternative to data binding.  

 Just remember: <Don't bind what can be beamed!>

Motivation: 

 What's wrong with Java XML data binding?

 This library is an alternative to Java XML binding libraries which convert XML entities into java objects and back.
 There is quite a list of these libraries, frequently used examples are: JAXB, XStream, XMLBeans, ...
 
 They have in common, that the structures of XML documents is mirrored to Java object structures.
 That means your Java API is determined by XML. 
 Changing the XML schema may lead to code changes.
 
 XMLBeam helps to avoid this by utilizing a mapping layer of tiny XPath expressions.
 Don't worry, you won't need a deep knowledge of XPath.

XMLBeam Feature List

 The motivation behind XMLBeam is to provide stable Java APIs.
 But there is a lot more in it:

 * <<Tiny foot print, dependency free>>
 
   The XMLBeam jar weights under 50kb and does not have any runtime dependency.  
   It relies reflection only.
   So you won't get any transitive dependencies.

 * <<Glue code free framework>>
 
   You don't need to write more than the declaration of a projection, not even a Java class. 
   But you get a statically typed Java API with real Java objects that can even be serialized.  

 * <<Declarative document origins>>
 
   Optionally you can let XMLBeam read and write your XML data. A single URL annotation safes quite some code. 
    
 * <<Dynamic projections>>
 
   Data binding is static. Projections may contain parameters and change during runtime which is a very powerful tool
   to write expressive but stable Java code. (See tutorial e03 where this feature is introduced.)
   
 * <<Bidirectional projections>>

   Changing document values and creating or deleting elements are just as easy as reading data.
   Declaring a setter in a projection makes an attribute, an element or a list of elements writable.

 * <<Projections to external documents>>
 
   You may unite the access to multiple documents in one projection interface.
   This is one key feature to provide a stable Java API. 
   Just add the URL annotation to a method declaration.
   The document will be fetched each time the method is called and the related XPath will be evaluated on the external document.
   (See example e09 for a real life use case.)  

 * <<Sub projections>>
 
   You can define sub projections to parts of XML documents. This would reflect a XML element into Java,
   or just group arbitrary values to a single Java structure. (See tutorial e01 for an example of grouping 
   data and e07 for element access example.)
   
 * <<Add behavior by adding Mixins>>
 
   A Mixin is a Java class that handles certain method calls for a projection. Kind of an interface with behavior.
 
   Projections can extend a certain interface (such as Comparable in tutorial e07).
   This way you can add behavior (e.g validation code) to projections. 
  
 * <<Painless DOM access>>
 
   You may at any time access and modify the DOM behind the scenes. You will find convenient access methods in XBProjector.java.
   Changes will reflect automatically in projections, because XMLBeam uses the DOM as backend. 
   
 * <<Easy name space handling>>
 
   Name spaces of elements and attributes are directly usable in projections by default. They don't leak into your Java API.
   
 * <<API Mimicry>>
 
   XMLBeam is so flexible that it can even be used to mimicry other APIs.
   (See tutorial e08 where the DOM4J API is imitated.) 
   
You can not use XMLBeam
 
 * If your XML documents doesn't fit into memory. (XMLBeam works with DOM parsers only.)

 * If you're on performance by hook or by crook. (XMLBeam surely is not the fastest way to handle XML.)
  
What XMLBeam does not do

 * Parsing XML. It uses the parser that's included within the JRE (since Java6). You may specify other parsers.
 
 * Building or transforming XML. It utilizes the JRE default DocumentBuilder and Transformers. Again you are able to change this easily.

 * Being thread safe. Access to different XML documents and different XBProjectors are thread safe. But concurrent access
   to a single XML document is not promised.
  
What next?

 * More Documentation.

 * API refinements.

 * Version 1.0 Release. 

 * Implement an option to externalize projection declaration to runtime configuration files.
 
 * Add projection interface generator as an alternative to hand crafted XPath expressions.
 
