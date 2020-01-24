package org.taHjaj.wo.hamaxagoga.junit;

import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import lombok.extern.log4j.Log4j2;
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
 * 
 * @author Michiel Kalkman
 *
 * Oasis standards can be found from http://www.oasis-open.org/specs/index.php.
 */
@Log4j2
public class StufTest extends AbstractTestCase {
//	public void testStuf() {
//		final int count = 25;
//
//		final String xsdFile = "/xsd/stuf.0204/stuf0204.xsd";
//		try {
//			final URI uri = this.getClass().getResource(xsdFile).toURI();
//			
//			final Params params = new Params();
//			params.addXsd( uri);
//			params.setMaxFileSize( 10000);
//			params.setSeed( 1L);
//			params.setEncoding( "iso-8859-1");
//		
//			final String targetDirectory = getTmpDirPath( "hamaxagoga/stuf0204");
//			log.debug( "Target directory: " + targetDirectory);
//			new RandomXMLGenerator().generate(
//					params, targetDirectory, count, true);
//		} catch( final Exception exception) {
//			log.error( exception.getLocalizedMessage(), exception);
//			fail( exception.getLocalizedMessage());
//		}
//	}

//	public void testBg() {
//		final int count = 25;
//
//		final String xsdFile = "/xsd/stuf.0204/bg0204.xsd";
//		try {
//			final URI uri = this.getClass().getResource(xsdFile).toURI();
//			
//			final Params params = new Params();
//			params.addXsd( uri);
//			params.setMaxFileSize( 10000);
//			params.setSeed( 1L);
//			params.setEncoding( "iso-8859-1");
//		
//			final String targetDirectory = getTmpDirPath( "hamaxagoga/bg0204/Bg");
//			log.debug( "Target directory: " + targetDirectory);
//			new RandomXMLGenerator().generate(
//					params, targetDirectory, count);
//		} catch( final Exception exception) {
//			log.error( exception.getLocalizedMessage(), exception);
//			fail( exception.getLocalizedMessage());
//		}
//	}
//
//	public void testBg2() {
//		final int count = 25;
//
//		final String xsdFile = "/xsd/stuf.0204/bg0204.xsd";
//		try {
//			final URI uri = this.getClass().getResource(xsdFile).toURI();
//			
//			final Params params = new Params();
//			params.addXsd( uri);
//			params.setMaxFileSize( 10000);
//			params.setSeed( 1L);
//			params.setEncoding( "iso-8859-1");
//			params.setIgnoringValidationErrors( true);
//		
//			final String targetDirectory = getTmpDirPath( "hamaxagoga/bg0204/Bg2");
//			log.debug( "Target directory: " + targetDirectory);
//			new RandomXMLGenerator().generate(
//					params, targetDirectory, count);
//		} catch( final Exception exception) {
//			log.error( exception.getLocalizedMessage(), exception);
//			fail( exception.getLocalizedMessage());
//		}
//	}
//
//	public void testBg3() {
//		final int count = 25;
//
//		final String xsdFile = "/xsd/stuf.0204/bg0204.xsd";
//		try {
//			final URI uri = this.getClass().getResource(xsdFile).toURI();
//			
//			final Params params = new Params();
//			params.addXsd( uri);
//			params.setMaxFileSize( 10000);
//			params.setSeed( 1L);
//			params.setEncoding( "iso-8859-1");
//			params.setIgnoringValidationErrors( true);
//		
//			final String targetDirectory = getTmpDirPath( "hamaxagoga/bg0204/Bg3");
//			log.debug( "Target directory: " + targetDirectory);
//			new RandomXMLGenerator().generateWithParallelValidation( 
//					params, targetDirectory, count);
//		} catch( final Exception exception) {
//			log.error( exception.getLocalizedMessage(), exception);
//			fail( exception.getLocalizedMessage());
//		}
//	}
//
//	public void testje() {
//		final int count = 2;
//
//		final String xsdFile = "/xsd/stuf.0204/test.xsd";
//		try {
//			final URI uri = this.getClass().getResource(xsdFile).toURI();
//			
//			final Params params = new Params();
//			params.addXsd( uri);
//			params.setMaxFileSize( 10000);
//			params.setSeed( 1L);
//			params.setEncoding( "iso-8859-1");
//			params.setRootElementName( "tests");
//		
//			final String targetDirectory = getTmpDirPath( "hamaxagoga/bg0204");
//			log.debug( "Target directory: " + targetDirectory);
//			new RandomXMLGenerator().generate(
//					params, targetDirectory, count);
//		} catch( final Exception exception) {
//			log.error( exception.getLocalizedMessage(), exception);
//			fail( exception.getLocalizedMessage());
//		}
//	}

	@Test
	public void testje2() {
		final int count = 20;

		final String xsdFile = "/xsd/stuf.0204/test.xsd";
		try {
			final URI uri = this.getClass().getResource(xsdFile).toURI();
			
			final Params params = new Params();
			params.addXsd( uri);
			params.setMaxFileSize( 10000);
			params.setSeed( 1L);
			params.setEncoding( "iso-8859-1");
			params.setRootElementName( "test");
			params.setIgnoringValidationErrors(true);
		
			final String targetDirectory = getTmpDirPath( "hamaxagoga/bg0204/test");
			log.debug( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			log.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
