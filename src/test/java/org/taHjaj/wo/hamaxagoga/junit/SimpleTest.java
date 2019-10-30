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
import java.security.SecureRandom;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import static org.junit.jupiter.api.Assertions.fail;
public class SimpleTest extends AbstractTestCase {
	private static final int REPEATS = 5;
	private static final Logger logger = Logger.getLogger( SimpleTest.class);
	
	@Test
	public void testSimpleMail() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/mails.xml";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.setSeed( 7);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "tmpMails"), count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	
	@Test
	public void testSimple3() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/shiporder.xml";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "tmp3"), count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	
	public void testSimple3a() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/shiporder.xml";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.setRootElementName( "inttype");
			params.setLexicalPattern( "[abc]");
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "tmp3a"), count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	
	@Test
	public void testSimple4() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/shiporder1.xml";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.setEncoding( "iso-8859-1");
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "tmp4"), count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}	
	@Test
	public void testSimple6() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/base64Binary.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			final String tmpDir = getTmpDirPath( "tmp6");
			logger.debug( "Output goes to " + tmpDir);
			new RandomXMLGenerator().generate(
					params, tmpDir, count);
			logger.debug( "Output in " + tmpDir);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	@Test
	public void testSimple7() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/base64Hexadecimal.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			final String tmpDir = getTmpDirPath( "tmp7");
			logger.debug( "Output goes to " + tmpDir);
			new RandomXMLGenerator().generate(
					params, tmpDir, count);
			logger.debug( "Output in " + tmpDir);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	@Test
	public void testSimple8() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/patterns.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			final String tmpDir = getTmpDirPath( "tmp8");
			logger.debug( "Output goes to " + tmpDir);
			new RandomXMLGenerator().generate(
					params, tmpDir, count);
			logger.debug( "Output in " + tmpDir);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	@Test
	public void testSimple9() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/patterns.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.setRandom( new SecureRandom());
			final String tmpDir = getTmpDirPath( "tmp8");
			logger.debug( "Output goes to " + tmpDir);
			new RandomXMLGenerator().generate(
					params, tmpDir, count);
			logger.debug( "Output in " + tmpDir);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
