package org.xmlbeam;


import java.io.IOException;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlbeam.config.DefaultFactoriesConfiguration;
import org.xmlbeam.config.FactoriesConfiguration;
import org.xmlbeam.util.DOMUtils;

public class XMLProjector {

	private final Transformer transformer;
	private final DocumentBuilder documentBuilder;
	private final FactoriesConfiguration factoriesConfiguration;
	public XMLProjector() {
		factoriesConfiguration = new DefaultFactoriesConfiguration();
		this.transformer = factoriesConfiguration.createTransformer();
		this.documentBuilder = factoriesConfiguration.createDocumentBuilder();
	}

	public XMLProjector(FactoriesConfiguration factoriesConfiguration) {
		this.transformer = factoriesConfiguration.createTransformer();
		this.documentBuilder = factoriesConfiguration.createDocumentBuilder();
		this.factoriesConfiguration = factoriesConfiguration;
	}

	/**
	 * Marker interface to determine if a Projection instance was created by a
	 * Projector.
	 */
	interface Projection extends Serializable {
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
		return ((T) java.lang.reflect.Proxy.newProxyInstance(projectionInterface.getClassLoader(), new Class[] { projectionInterface, Projection.class, Serializable.class }, new ProjectionInvocationHandler(factoriesConfiguration, node, projectionInterface)));
	};

	public Document getXMLDocForProjection(final Object projection) {
		if (!(projection instanceof Projection)) {
			throw new IllegalArgumentException("Given projection " + projection + " was not created by me.");
		}
		Node node = ((Projection) projection).getXMLNode();
		if (Node.DOCUMENT_NODE == node.getNodeType()) {
			return (Document) node;
		}
		return node.getOwnerDocument();
	}



	public <T> T readFromURIAnnotation(final Class<T> projectionInterface) throws IOException {
		org.xmlbeam.URI doc = projectionInterface.getAnnotation(org.xmlbeam.URI.class);
		if (doc == null) {
			throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the xml.Doc annotation linking to the document source.");
		}
		final Document document = DOMUtils.getXMLNodeFromURI(factoriesConfiguration.createDocumentBuilder(), doc.value(), projectionInterface);

		return projectXML(document, projectionInterface);
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
