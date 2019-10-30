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

import java.net.URL;

import org.apache.log4j.Logger;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class ByteTest extends AbstractTestCase {
	private static final Logger logger = Logger.getLogger( ByteTest.class);

	public void testByte() {
		final int count = 3;
		final String xsdFile = "/xsd/simple/byte.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			final String integerTmpOutputDir = getTmpDirPath( "simpleByte");
			logger.debug( "Output files in " + integerTmpOutputDir);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "simpleByte"), count);
			logger.debug( "Output files in " + integerTmpOutputDir);
		} catch( final Exception exception) {
			logger.debug( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
