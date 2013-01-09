                 ------
                 XMLBeam
                 ------
                 Sven Ewald
                 ------
                  2012
                 ------


About

 This is a Java library to project parts of a XML DOM tree into Java objects as an alternative to data binding.  

 (Don't bind what can be beamed!)

Motivation (What's wrong with data binding?):

 This library is an alternative to XML binding libraries which convert XML entities into java objects and back.
 There is quite a list of these libraries, frequently used examples are: JAXB, XStream, XMLBeans, ...
 They have in common, that the structures of XML documents is mirrored to Java object structures.
 That means your Java API is determined by XML. Changing the XML schema leads to code changes.
 XMLBeam helps to avoid this by utilizing a mapping layer of tiny XPath expressions.
 Don't worry, you won't need a deep knowledge of XPath.
 The motivation behind XMLBeam was to provide stable Java APIs. But there is a lot more to get:

XMLBeam Feature List

 * Tiny food print, dependency free.
 
   The XMLBeam jar does not have any runtime dependency so you won't get any transitive dependencies. 
   It relies on the use of reflection only. 

 * Glue code free framework.
 
   You don't need to write more than the declaration of a projection. Not even a POJO is needed. 
   But you get a statically typed Java API with real Java objects that can even be serialized.  

 * Declarative document origins.
 
   Optionally you can let XMLBeam read and write your XML data. A single URL annotation safes quite some code. 
    
 * Dynamic projections.
 
   Data binding is static. Projections may contain parameters and change during runtime. This is a very powerful tool
   to write expressive but stable Java code. (See tutorial e03 where this feature is introduced.)
   
 * Bidirectional projections.

   Changing document values and creating or deleting elements is possible just as easy as reading data.
   Declaring a setter with subset of the XPath syntax makes an attribute, an element or a list of elements writable.   

 * Projections to external documents.
 
   Getters and setters in a projection do not need to be projected to the same document. You may unite the access to
   multiple documents in one projection interface. This is one key feature to provide a stable Java API. 
   Just add the URL annotation to a method declaration instead of the interface.
   The document will be fetched on each method call and the related XPath will be evaluated on the external document instead.
   See example e09 for this applied to a real life use case.  

 * Sub projections.
 
   You may define sub projections to parts of XML documents. This way you may reflect a XML structure somehow into java,
   or just group arbitrary elements and attributes to a single Java structure. (See tutorial e01 for an example of grouping 
   data and e07 for element access example.)
   
 * Add behavior by adding Mixins (A Mixin is a Java class that handles certain method calls for a projection.)
 
   If you want your projections to implement a certain interface (such as Comparable in tutorial e07), or you like to
   add validation code or such, there is a way to do that.
  
 * Painless DOM access.
 
   You may at any time access and modify the DOM behind the scenes (find convenient access methods in XMLProjector.java).
   Because XMLBeam uses the DOM as backend, changes will reflect automatically in projections.
   
 * Easy name space handling.
 
   Name spaces of elements and attributes are directly usable in projections by default. They don't leak into your Java API.
   
 * API Mimicry.
 
   XMLBeam is so flexible that it can be used to mimicry other APIs. See tutorial e08 where the DOM4J API is imitated. 
   
You don't need XMLBeam if:

 * You have a static XML schema that won't change. (Hopefully it won't hurt to much if it happens nevertheless...)
 
 * Your XML documents won't fit into memory. (XMLBeam works with DOM parsers only)

 * Performance by hook or by crook. (XMLBeam surly is not the fastest way to handle XML)
  
What XMBLeam does not:

 * Parse XML. It will use the parser that comes with the JRE (since Java6) as a default. You may specify other parsers.
 
 * Build or Transform XML. It will use the JRE default DocumentBuilder and Transformers. Again you are able to change this easily.

 * Being thread safe. Access to different XML Documents and different XMLProjectors are thread safe. But concurrent access
   to a single XML document is not promised.
  
What next?

 * More Documentation.

 * API refinements.

 * Version 1.0 Release. 

 * Implement option to externalize projection declaration to runtime configuration files.
 
 * Add projection interface generator as an alternative to hand crafted XPath expressions.
 
