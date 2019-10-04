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
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xml.serialize.XMLSerializer;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;

public class XSParticleSupport extends XSSupport {
    private final XSParticle particle;
    private int times;
    private final int min;
    private final int max;
    private int i;

    public XSParticleSupport( final XSParticle particle,
	    XMLGenerator instanceGenerator) {
	this.particle = particle;

	final Params params = instanceGenerator.getParams();

	if( ! instanceGenerator.isFQuitting() && particle.getMinOccurs() < params.getMinOccurs()) {
	    if( particle.getMaxOccursUnbounded()) {
		min = params.getMinOccurs();
	    } else if( params.getMinOccurs() > particle.getMaxOccurs()) {
		min = particle.getMaxOccurs();
	    } else {
		min = params.getMinOccurs();
	    }
	} else {
	    min = particle.getMinOccurs();
	}

	if( instanceGenerator.isFQuitting()) {
		max = min;
	} else if(	particle.getMaxOccursUnbounded()) {
	    if( params.getMaxOccurs() < min) {
		max = min;
	    } else {
		max = params.getMaxOccurs();
	    }
	} else {
	    if( params.getMaxOccurs() > particle.getMaxOccurs()) {
	    	max = particle.getMaxOccurs();
	    } else if( params.getMaxOccurs() < particle.getMinOccurs()) {
	    	max = particle.getMinOccurs();
	    } else {
	    	max = params.getMaxOccurs();
	    }
	}

	times = instanceGenerator.getRandom().nextInt( max - min + 1) + min;
    }

    @Override
    public void process( XMLSerializer serializer,
	    XMLGenerator instanceGenerator) throws HamaxagogaException {
	// http://www.w3.org/TR/xmlschema-1/#cParticles
	if( (i < min) || (i < times && !instanceGenerator.isFQuitting())) {
	    final XSTerm term = particle.getTerm();
    
	    instanceGenerator.getXmlSchemaObjects().push( 
		    new XSTermSupport( term));
	    
	    i++;
	} else {
	    // We're done
	    instanceGenerator.getXmlSchemaObjects().pop();
	}
    }
}
