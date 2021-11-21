package org.taHjaj.wo.hamaxagoga.support;

/*
 * Copyright 2008 Michiel Kalkman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class XSElementDeclSupport extends XSSupport {
	private final XSElementDecl elementDecl;

	private Map<String,String> registeredPrefixes = new HashMap<String,String>(); // prefix --> namespace URI.
	
	private boolean fFinishComplexType = false;

	public XSElementDeclSupport(final XSElementDecl elementDecl) {
		this.elementDecl = elementDecl;
	}

	public void process(XMLSerializer serializer,
			final XMLGenerator instanceGenerator) throws SAXException,
			HamaxagogaException {
		if (fFinishComplexType) {
			processEnd(serializer, instanceGenerator, elementDecl.getName());
		} else {
			processStart(serializer, instanceGenerator);
		}
	}

	public void processStart(XMLSerializer serializer,
			final XMLGenerator instanceGenerator) throws SAXException,
			HamaxagogaException {
		final String localName = elementDecl.getName();
		
		final String namespaceURI = elementDecl.getNamespace();

		final String qName;

		if (StringUtils.isEmpty(namespaceURI)) {
			qName = localName;
		} else {
			final String prefix = instanceGenerator.generatePrefix(namespaceURI);
			qName = registerNamespaceOccurrence(serializer,
					localName, namespaceURI, prefix, instanceGenerator.getNamespaceURI2prefix());
		}

		final XSTypeDefinition typeDefinition = elementDecl.getTypeDefinition();
		final short typeCategorie = typeDefinition.getTypeCategory();

		switch (typeCategorie) {
		case XSTypeDefinition.SIMPLE_TYPE:
			final XSSimpleTypeDefinition simpleTypeDefinition = (XSSimpleTypeDefinition) elementDecl
					.getTypeDefinition();

			log.info("SERIALIZE: Start " + localName);
			serializer.startElement(namespaceURI, localName, qName, null);

			final String value;
			final Object object = elementDecl.getActualVC();
			if (object != null) {
				if (object instanceof String) {
					value = (String) elementDecl.getActualVC();
				} else {
					log.debug("ActualVC instanceOf "
							+ object.getClass().getName());
					value = object.toString();
				}
			} else {
				value = instanceGenerator
						.processSimpleTypeDefinition(simpleTypeDefinition);
			}

			toSerializer(serializer, value);

			processEnd(serializer, instanceGenerator, localName);

			log.info("SERIALIZE: End   " + localName);
			break;
		default: // case XSTypeDefinition.COMPLEX_TYPE:
			log.debug("COMPLEX_TYPE");

			final Attributes attributes = instanceGenerator
					.getAttributes(((XSComplexTypeDefinition) typeDefinition)
							.getAttributeUses());

			if (attributes != null && attributes.getLength() > 0) {

				for (int i = attributes.getLength() - 1; i >= 0; i--) {
					registerNamespaceOccurrence(serializer, attributes.getLocalName(i), 
							attributes.getURI(i), StringUtils.substringBefore(attributes.getQName(i),
					":"), instanceGenerator.getNamespaceURI2prefix());
				}

				log.debug("SERIALIZE: Start " + localName);
				serializer.startElement(namespaceURI, localName, qName,
						attributes);
			} else {
				log.debug("SERIALIZE: Start " + localName);
				serializer.startElement(namespaceURI, localName, qName, null);
			}

			// We have a Complex Type to process.
			instanceGenerator.getXmlSchemaObjects().push(
					new XSComplexTypeDefinitionSupport(
							(XSComplexTypeDefinition) typeDefinition));
			fFinishComplexType = true;

			break;
		}
	}

	private void processEnd( final XMLSerializer serializer,
			final XMLGenerator instanceGenerator, final String localName)
			throws SAXException, HamaxagogaException {
		{
			log.debug("SERIALIZE: END " + localName);
			serializer.endElement(localName);
		}

		deregisterNamespaceOccurrence(serializer, instanceGenerator.getNamespaceURI2prefix());

		// We're done with this element
		instanceGenerator.getXmlSchemaObjects().pop();
	}


	public void deregisterNamespaceOccurrence( final XMLSerializer serializer, final Map<String, String> namespaceURI2prefix)
			throws SAXException {
		
		for( final Map.Entry<String,String> prefix2namespaceURI : registeredPrefixes.entrySet()) {
			namespaceURI2prefix.remove( prefix2namespaceURI.getValue());
			serializer.endPrefixMapping( prefix2namespaceURI.getKey());
		}
	}

	public String registerNamespaceOccurrence(final XMLSerializer serializer,
			final String localName, final String namespaceURI,
			final String prefix, final Map<String, String> namespaceURI2prefix) throws SAXException {
		final String qName;

		if (StringUtils.isEmpty(namespaceURI)) {
			qName = localName;
		} else {
			// Possibilities:
			// 1. The namespace has already been used.
			// 2. The namespace has already been used.
			
			if( namespaceURI2prefix.containsKey( namespaceURI)) {
				final String registeredPrefix = namespaceURI2prefix.get( namespaceURI);
				if( StringUtils.isEmpty( registeredPrefix)) {
					qName = localName;
				} else {
					qName = registeredPrefix + ":" + localName;				
				}
			} else {
				// prefix is new.
				registeredPrefixes.put( prefix, namespaceURI);
				namespaceURI2prefix.put( namespaceURI, prefix);
				if (StringUtils.isEmpty(prefix)) {
					qName = localName;
					serializer.startPrefixMapping(null, namespaceURI);
				} else {
					qName = prefix + ":" + localName;
					serializer.startPrefixMapping(prefix, namespaceURI);
				}
			}
		}
		return qName;
	}

}
