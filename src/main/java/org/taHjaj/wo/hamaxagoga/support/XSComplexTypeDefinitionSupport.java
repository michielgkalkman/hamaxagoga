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
import org.apache.commons.lang.NotImplementedException;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSParticle;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.xml.sax.SAXException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class XSComplexTypeDefinitionSupport extends XSSupport {
    private final XSComplexTypeDefinition complexTypeDefinition;

    public XSComplexTypeDefinitionSupport(
	    final XSComplexTypeDefinition complexTypeDefinition) {
	this.complexTypeDefinition = complexTypeDefinition;
    }

    @Override
    public void process( final XMLSerializer serializer,
	    final XMLGenerator instanceGenerator) throws SAXException {
	final short contentType = complexTypeDefinition.getContentType();
	switch( contentType) {
	case XSComplexTypeDefinition.CONTENTTYPE_ELEMENT: {
	    log.debug( "CONTENTTYPE_ELEMENT");

	    final XSParticle particle = complexTypeDefinition.getParticle();

	    instanceGenerator.getXmlSchemaObjects().pop();
	    instanceGenerator.getXmlSchemaObjects().push(
		    new XSParticleSupport( particle, instanceGenerator));

	    break;
	}
	case XSComplexTypeDefinition.CONTENTTYPE_EMPTY: {
	    log.debug( "CONTENTTYPE_EMPTY");

	    // We're done.
	    instanceGenerator.getXmlSchemaObjects().pop();
	    // Zoiets als <xxx/> misschien met attributen
	    break;
	}
	case XSComplexTypeDefinition.CONTENTTYPE_MIXED: {
	    log.debug( "CONTENTTYPE_MIXED");
	    // Mixed type.
	    // @TODO Create real mixed content
	    final XSParticle particle = complexTypeDefinition.getParticle();

	    instanceGenerator.getXmlSchemaObjects().pop();
	    instanceGenerator.getXmlSchemaObjects().push(
		    new XSParticleSupport( particle, instanceGenerator));

	    break;
	}
	case XSComplexTypeDefinition.CONTENTTYPE_SIMPLE: {
	    log.debug( "CONTENTTYPE_SIMPLE");

	    instanceGenerator.processSimpleContent( serializer,
		    complexTypeDefinition);

	    // We're done.
	    instanceGenerator.getXmlSchemaObjects().pop();
	    break;
	}
	default: {
	    throw new NotImplementedException( "Unknown type " + contentType
		    + " not implemented yet.");
	}
	}

    }
}
