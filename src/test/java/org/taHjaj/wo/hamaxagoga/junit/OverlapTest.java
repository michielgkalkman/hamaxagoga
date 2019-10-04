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

public class OverlapTest extends AbstractTestCase {
	private static final int REPEATS = 5;
	private static final Logger logger = Logger.getLogger( OverlapTest.class);

	public void testSimple2() {
		final int count = REPEATS;
		final String xsdFile1 = "/xsd/overlap/XLijst2a.xsd";
		URL url1 = this.getClass().getResource(xsdFile1);
		final String xsdFile2 = "/xsd/overlap/XLijst2b.xsd";
		URL url2 = this.getClass().getResource(xsdFile2);
		final String xsdFile3 = "/xsd/overlap/XLijst2c.xsd";
		URL url3 = this.getClass().getResource(xsdFile3);
		final Params params = new Params();
		try {
			params.addXsd( url3.toURI());
			params.addXsd( url2.toURI());
			params.addXsd( url1.toURI());
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "tmp2/overlap"), count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
