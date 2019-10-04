package org.taHjaj.wo.hamaxagoga.junit;

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

import java.net.URL;

import org.apache.log4j.Logger;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class ErrorTest extends AbstractTestCase {
	private static final int REPEATS = 5;
	private static final Logger logger = Logger.getLogger( ErrorTest.class);

	/**
	 * Test what happens if given XSD does not exist.
	 */
	public void testError1() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/errors/doesnotexist.xml";
		final Params params = new Params();
		try {
			params.addXsd( xsdFile);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "errorst"), count);
			fail( "expected an exceptions as XSD does not exist");
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage());
		}
	}

	/**
	 * Test what happens if given XSD does not have any root elements.
	 */
	public void testNoRoots() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/errors/no-roots.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "errors"), count);
			fail( "expected an exceptions as XSD does have any root elements");
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage());
		}
	}

	/**
	 * Test what happens if given XSD does not have the requested root element.
	 */
	public void testNotRequestedRoots() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/errors/not-requested-roots.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.setRootElementName( "doesNotExist");
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "errors"), count);
			fail( "expected an exceptions as XSD does have any root elements");
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage());
		}
	}
	
	/**
	 * Test what happens if a given file is not an XSD.
	 */
	public void testNotAnXSD() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/errors/not-an-xsd.xml";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "errors"), count);
			fail( "expected an exceptions as XSD does have any root elements");
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage());
		}
	}

}
