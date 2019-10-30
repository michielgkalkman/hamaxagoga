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
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class NamespaceTest extends AbstractTestCase {
	private static final int REPEATS = 5;
	private static final Logger logger = Logger.getLogger( NamespaceTest.class);

	/**
	 * Test what happens if a given namespace is to be mapped to a prefix.
	 */
	@Test
	public void testNamespaces() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/namespaces/sequence.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			final String tmpOutputDir = getTmpDirPath( "namespaces");
			logger.debug( "Output files in " + tmpOutputDir);
			new RandomXMLGenerator().generate(
					params, tmpOutputDir, count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

	/**
	 * Test what happens if a given namespace is to be mapped to a prefix.
	 */
	@Test
	public void testNamespaces2() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/namespaces/sequence.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.addPredefinedNamespacePrefix( "http://hamaxagoga", "prefix");
			final String tmpOutputDir = getTmpDirPath( "namespaces2");
			logger.debug( "Output files in " + tmpOutputDir);
			new RandomXMLGenerator().generate(
					params, tmpOutputDir, count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

	/**
	 * Test what happens if a given namespace is to be mapped to a prefix.
	 */
	@Test
	public void testNamespaces3() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/namespaces/sequence.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.addPredefinedNamespacePrefix( "http://hamaxagoga", "");
			final String tmpOutputDir = getTmpDirPath( "namespaces3");
			logger.debug( "Output files in " + tmpOutputDir);
			new RandomXMLGenerator().generate(
					params, tmpOutputDir, count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
