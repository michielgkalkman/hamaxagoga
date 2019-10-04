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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
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
