package org.xmlbeam;


import java.lang.reflect.Method;

import java.util.LinkedList;
import java.util.List;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlbeam.config.Configuration;
import org.xmlbeam.config.DefaultConfiguration;
import org.xmlbeam.util.TypeConverter;

public class XMLProjector {

	//final Configuration configuration;
	private final Transformer transformer;
	private final DocumentBuilder documentBuilder;

	public XMLProjector() {
		Configuration configuration = new DefaultConfiguration();
		this.transformer = configuration.createTransformer();
		this.documentBuilder = configuration.createDocumentBuilder();
	}

	public XMLProjector(Configuration configuration) {
		this.transformer = configuration.createTransformer();
		this.documentBuilder = configuration.createDocumentBuilder();
	}

	/**
	 * Marker interface to determine if a Projection instance was created by a
	 * Projector.
	 */
	interface Projection {
		Node getXMLNode();
	}

	public <T> T readFromURI(final String uri, final Class<T> clazz) throws SAXException, IOException, ParserConfigurationException {
		Document document = getDocumentBuilder().parse(uri);
		return projectXML(document, clazz);
	}

	/**
	 * Creates a projection from XML to Java.
	 * 
	 * @param node
	 *            XML DOM Node. May be a document or just an element.
	 * @param projectionInterface
	 *            A Java interface to project the data on.
	 * @return a new instance of projectionInterface.
	 */
	@SuppressWarnings("unchecked")
	public <T> T projectXML(final Node node, final Class<T> projectionInterface) {
		if ((projectionInterface == null) || (!projectionInterface.isInterface())) {
			throw new IllegalArgumentException("Parameter " + projectionInterface + " is not an interface.");
		}
		return ((T) java.lang.reflect.Proxy.newProxyInstance(projectionInterface.getClassLoader(), new Class[] { projectionInterface, Projection.class }, new ProjectionInvocationHandler(this, node, projectionInterface)));
	};

	public Document getXMLSourceForProjection(final Object projection) {
		if (!(projection instanceof Projection)) {
			throw new IllegalArgumentException("Given projection " + projection + " was not created by me.");
		}
		Node node = ((Projection) projection).getXMLNode();
		if (Node.DOCUMENT_NODE == node.getNodeType()) {
			return (Document) node;
		}
		return node.getOwnerDocument();
	}

	protected Document getXMLNodeFromURI(final String uri, final Class<?> resourceAwareClass) throws IOException {
		try {
		if (uri.startsWith("resource://")) {
			return getDocumentBuilder().parse(resourceAwareClass.getResourceAsStream(uri.substring("resource://".length())));
		}
		Document document = getDocumentBuilder().parse(uri);
		if (document == null) {
			throw new IOException("Document could not be created form uri " + uri);
		}
		return document;
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T readFromURIAnnotation(final Class<T> projectionInterface) throws IOException {
		org.xmlbeam.URI doc = projectionInterface.getAnnotation(org.xmlbeam.URI.class);
		if (doc == null) {
			throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the xml.Doc annotation linking to the document source.");
		}
		final Document document = getXMLNodeFromURI(doc.value(), projectionInterface);

		return projectXML(document, projectionInterface);
	}

	List<?> evaluateAsList(final XPathExpression expression, final Node node, final Method method) throws XPathExpressionException {
		NodeList nodes = (NodeList) expression.evaluate(node, XPathConstants.NODESET);
		List<Object> linkedList = new LinkedList<Object>();
		Class<?> targetType;
		if (method.getReturnType().isArray()) {
			targetType = method.getReturnType().getComponentType();
		} else {
			targetType = method.getAnnotation(org.xmlbeam.Xpath.class).targetComponentType();
			if (Xpath.class.equals(targetType)) {
				throw new IllegalArgumentException("When using List as return type for method " + method + ", please specify the list content type in the " + Xpath.class.getSimpleName() + " annotaion. I can not determine it from the method signature.");
			}
		}

		TypeConverter<?> converter = TypeConverter.CONVERTERS.get(targetType);
		if (converter != null) {
			for (int i = 0; i < nodes.getLength(); ++i) {
				linkedList.add(converter.convert(nodes.item(i).getTextContent()));
			}
			return linkedList;
		}
		if (targetType.isInterface()) {
			for (int i = 0; i < nodes.getLength(); ++i) {
				Node n = nodes.item(i).cloneNode(true);
				linkedList.add(projectXML(n, method.getAnnotation(org.xmlbeam.Xpath.class).targetComponentType()));
			}
			return linkedList;
		}
		throw new IllegalArgumentException("Return type " + method.getAnnotation(org.xmlbeam.Xpath.class).targetComponentType() + " is not valid for list or array component type returning from method " + method + ". Try one of " + TypeConverter.CONVERTERS.keySet());
	}

	public <T> T createEmptyDocumentProjection(Class<T> projection)  {
		Document document = getDocumentBuilder().newDocument();
		return projectXML(document, projection);
	}

	public Transformer getTransformer() {
		return transformer;
	}
	
	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}
}
