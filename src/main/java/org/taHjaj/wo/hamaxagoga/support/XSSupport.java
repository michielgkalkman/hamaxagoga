package org.taHjaj.wo.hamaxagoga.support;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.xml.sax.SAXException;

public abstract class XSSupport {
    // @return true-->we're not through processing, false throw this XSSupport away
    // away.
    public abstract void process( final XMLSerializer serializer,
	    final XMLGenerator instanceGenerator) throws SAXException, HamaxagogaException;

    protected void toSerializer( final XMLSerializer serializer,
	    final String value) throws SAXException {
	serializer.characters( value.toCharArray(), 0, value.length());
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString( this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
