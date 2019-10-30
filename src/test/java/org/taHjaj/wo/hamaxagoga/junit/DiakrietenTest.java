package org.taHjaj.wo.hamaxagoga.junit;

/*
 * Copyright 2008 Michiel Kalkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *       
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class DiakrietenTest extends AbstractTestCase {
	private static final int REPEATS = 5;
	private static final Logger logger = Logger.getLogger( DiakrietenTest.class);
	
	@Test
	public void testSimple() {
		final int count = REPEATS;
	
		try {
			final String xsdFile = "/xsd/mails.xml";
			final URI uri = this.getClass().getResource(xsdFile).toURI();
			
			final Params params = new Params();
			params.addXsd( uri);
			params.setMaxFileSize( 1000);
			params.setSeed( 1L);
			params.setLexicalPattern( "[a-zA-Z0-9À-ÿ]");
		
			final String targetDirectory = getTmpDirPath( "hamaxagoga/diakrieten");
			logger.debug( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
