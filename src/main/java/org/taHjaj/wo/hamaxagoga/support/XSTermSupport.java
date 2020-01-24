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
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSTerm;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class XSTermSupport extends XSSupport {
    private final XSTerm term;

    public XSTermSupport( final XSTerm term) {
	this.term = term;
    }

    @Override
    public void process( final XMLSerializer serializer,
	    final XMLGenerator instanceGenerator) {
	instanceGenerator.getXmlSchemaObjects().pop();

	switch( term.getType()) {
	case XSConstants.ELEMENT_DECLARATION: {
	    XSElementDecl elementDecl = (XSElementDecl) term;
	    instanceGenerator.getXmlSchemaObjects().push(
		    new XSElementDeclSupport( elementDecl));
	    break;
	}
	case XSConstants.MODEL_GROUP: {
	    XSModelGroup modelGroup = (XSModelGroup) term;

	    instanceGenerator.getXmlSchemaObjects().push(
		    new XSModelGroupSupport( modelGroup));
	    break;
	}
	case XSConstants.WILDCARD: {
	    log.debug( "WILDCARD.");
	    break;
	}
	default: {
	    log.error( "Huhm..");
	    throw new RuntimeException( "Not implemented yet.");
	}
	}
    }

}
