package org.taHjaj.wo.hamaxagoga.support;

import org.apache.commons.lang3.NotImplementedException;
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
