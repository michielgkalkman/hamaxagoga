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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.xml.sax.SAXException;

public class XSModelGroupSupport extends XSSupport {
    private final XSModelGroup modelGroup;
    private List<XSParticle> particles = null;
    private int i = 0;

    public XSModelGroupSupport( final XSModelGroup modelGroup) {
	this.modelGroup = modelGroup;
    }

    @Override
    public void process( XMLSerializer serializer,
	    XMLGenerator instanceGenerator) throws SAXException, HamaxagogaException {
	// TODO Auto-generated method stub
	switch( modelGroup.getCompositor()) {
	case XSModelGroup.COMPOSITOR_SEQUENCE: {
	    XSObjectList objectList = modelGroup.getParticles();
	    if( i < objectList.getLength()) {
		XSParticleDecl particleDecl = (XSParticleDecl) objectList
			.item( i);

		instanceGenerator.getXmlSchemaObjects()
			.push(
				new XSParticleSupport( particleDecl,
					instanceGenerator));
		i++;
	    } else {
		// we're done
		instanceGenerator.getXmlSchemaObjects().pop();
	    }
	    break;
	}
	case XSModelGroup.COMPOSITOR_CHOICE: {
	    final XSObjectList objectList = modelGroup.getParticles();

	    final int choice = instanceGenerator.getRandom().nextInt(
		    objectList.getLength());

	    final XSParticleDecl particleDecl = (XSParticleDecl) objectList
		    .item( choice);

	    // we're done
	    instanceGenerator.getXmlSchemaObjects().pop();
	    instanceGenerator.getXmlSchemaObjects().push(
		    new XSParticleSupport( particleDecl, instanceGenerator));

	    break;
	}
	case XSModelGroup.COMPOSITOR_ALL: {
	    if( particles == null) {
		particles = new ArrayList<XSParticle>();
		XSObjectList objectList = modelGroup.getParticles();

		for( int i = 0; i < objectList.getLength(); i++) {
		    final XSParticle particle = (XSParticle) objectList
			    .item( i);
		    if( particle.getMinOccurs() == 0) {
			if( instanceGenerator.getRandom().nextBoolean()) {
			    particles.add( particle);
			}
		    } else {
			// Particle must occur.
			particles.add( particle);
		    }
		}

		Collections.shuffle( particles, instanceGenerator.getRandom());
	    }

	    if( i >= particles.size()) {
		// we're done.
		instanceGenerator.getXmlSchemaObjects().pop();
	    } else {
		final XSParticle particle = particles.get( i);
		final XSTerm term = particle.getTerm();

		if( particle.getMinOccurs() > 0
			|| !instanceGenerator.isFQuitting()) {
		    instanceGenerator.getXmlSchemaObjects().push(
			    new XSTermSupport( term));
		}
		i++;
	    }
	    break;
	}
	default: {
	    throw new RuntimeException( "Not possible.");
	}
	}
    }
}
