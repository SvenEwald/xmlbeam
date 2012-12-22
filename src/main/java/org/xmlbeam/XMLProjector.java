package org.xmlbeam;

import java.text.MessageFormat;

import java.lang.reflect.Modifier;

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

/**
 * <p>
 * Overview<br>
 * The class XMLProjector is a tool to create, read or write so called
 * "projections". Projections are Java interfaces associated to XML documents.
 * Projections may contain methods annotated with XPath selectors. These XPath
 * expressions define the subset of XML data which is "projected" to Java values
 * and objects.
 * </p>
 * <p>
 * Getters<br>
 * For getter methods (methods with a name-prefix "get", returning a value) the
 * XPath-selected nodes are converted to the method return type. This works with
 * all java primitive types, Strings, and lists or arrays containing primitives
 * or Strings.
 * </p>
 * <p>
 * Setters<br>
 * Setter methods (method with a name starting with "set", having a parameter)
 * can be defined to modify the content of the associated XML document. Not all
 * XPath capabilities define writable projections, so the syntax is limited to
 * selectors of elements and attributes. In contrast to Java Beans a setter
 * method may define a return value with the projection interface type. If this
 * return value is defined, the current projection instance is returned. This
 * allows the definition of projections according to the fluent interface
 * pattern (aka Builder Pattern).
 * </p>
 * <p>
 * Sub Projections<br>
 * For the purpose of accessing structured data elements in the XML document you
 * may define "sub projections" which are projections associated to elements
 * instead to documents. Sub projections can be used as return type of getters
 * and as parameters of setters. This works even in arrays or lists. Because of
 * the infamous Java type erasure you have to specify the component type of the
 * sub projection for a getter returning a list of sub projections. This type is
 * defined as second parameter "targetType" in the {@link Xpath} annotation.
 * </p>
 * <p>
 * Dynamic Projections<br>
 * XPath expressions are evaluated during runtime when the corresponding methods
 * are called. Its possible to use placeholder ("{0}, {1}, {2},... ) in the
 * expression that will be substituted with method parameters before the
 * expression is evaluated. Therefore getters and setters may have multiple
 * parameters which will be applied via a {@link MessageFormat} to build up the
 * final XPath expression. The first parameter of a setter will be used for
 * both, setting the document value and replacing the placeholder "{0}".
 * </p>
 * 
 * @author sven
 * 
 */
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
	 * Projector. This will be applied automatically to projections.
	 */
	interface Projection extends Serializable {

		Node getXMLNode();

		Class<?> getProjectionInterface();
	}

	/**
	 * Create a new projection using a given uri parameter. When the uri starts
	 * with the protocol identifier "resource://" the classloader of projection
	 * interface will be used to read the resource from the current class path.
	 * 
	 * @param uri
	 * @param clazz
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
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
		if (!isValidProjectionInterface(projectionInterface)) {
			throw new IllegalArgumentException("Parameter " + projectionInterface + " is not a public interface.");
		}
		return ((T) java.lang.reflect.Proxy.newProxyInstance(projectionInterface.getClassLoader(), new Class[] { projectionInterface, Projection.class, Serializable.class }, new ProjectionInvocationHandler(factoriesConfiguration, node, projectionInterface)));
	}

	/**
	 * @param projectionInterface
	 * @return true if param is a public interface.
	 */
	private <T> boolean isValidProjectionInterface(final Class<T> projectionInterface) {
		return (projectionInterface != null) && (projectionInterface.isInterface()) && ((projectionInterface.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC);
	};

	/**
	 * Use this method to obtain the DOM tree behind a projection. Changing the
	 * DOM of a projection is a valid action and may change the results of the
	 * projections methods.
	 * 
	 * @param projection
	 * @return Document holding projections data.
	 */
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

	/**
	 * Create a new projection using a {@link URI} annotation on this interface.
	 * When the uri starts with the protocol identifier "resource://" the
	 * classloader of projection interface will be used to read the resource
	 * from the current class path.
	 * 
	 * @param projectionInterface
	 *            a public interface.
	 * @return a new projection instance
	 * @throws IOException
	 */
	public <T> T readFromURIAnnotation(final Class<T> projectionInterface) throws IOException {
		org.xmlbeam.URI doc = projectionInterface.getAnnotation(org.xmlbeam.URI.class);
		if (doc == null) {
			throw new IllegalArgumentException("Class " + projectionInterface.getCanonicalName() + " must have the " + URI.class.getName() + " annotation linking to the document source.");
		}
		final Document document = DOMUtils.getXMLNodeFromURI(factoriesConfiguration.createDocumentBuilder(), doc.value(), projectionInterface);

		return projectXML(document, projectionInterface);
	}

	/**
	 * Create a new projection for en empty document. Use this to create new
	 * documents.
	 * 
	 * @param projection
	 * @return a new projection instance
	 */
	public <T> T createEmptyDocumentProjection(Class<T> projection) {
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
