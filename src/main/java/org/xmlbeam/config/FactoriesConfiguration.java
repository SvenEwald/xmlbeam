package org.xmlbeam.config;

import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.xmlbeam.XMLProjector;

/**
 * A Configuration defines factories for the underlying Java XML
 * implementations. By implementing your on Configuration you may inject other
 * XML parsers or transformers in an {@link XMLProjector}. See
 * {@link DefaultFactoriesConfiguration} for further information.
 * 
 * @author <a href="https://github.com/SvenEwald">Sven Ewald</a>
 */
public interface FactoriesConfiguration extends Serializable {

	/**
	 * Factory method to provide a {@link TransfomerFactory}.
	 * 
	 * @return a new instance.
	 */
	TransformerFactory createTransformerFactory();

	/**
	 * Factory method to provide a {@link DocumentBuilderFactory}.
	 * 
	 * @return a new instance.
	 */
	DocumentBuilderFactory createDocumentBuilderFactory();
	
	/**
	 * Factory method to provide a {@link Transformer}.
	 * 
	 * @return a new instance.
	 */
	Transformer createTransformer();

	/**
	 * Factory method to provide a {@link DocumentBuilder}.
	 * 
	 * @return a new instance.
	 */
	DocumentBuilder createDocumentBuilder();

}
