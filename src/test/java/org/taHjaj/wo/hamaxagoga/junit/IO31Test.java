package org.taHjaj.wo.hamaxagoga.junit;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

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
public class IO31Test extends AbstractTestCase {

	@Test
	public void testIO31() {
		final int count = 20;

		final String xsdFile = "/xsd/io31/io31.xsd";
		try {
			final URI uri = this.getClass().getResource(xsdFile).toURI();

			final Params params = new Params();
			params.addXsd( uri);
			params.setMaxFileSize( 10000);
			params.setSeed( 1L);
			params.setEncoding( "iso-8859-1");
			params.setRootElementName( "Bericht");
			params.setIgnoringValidationErrors(true);

			final String targetDirectory = getTmpDirPath( "hamaxagoga/io31/test");
			log.debug( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			log.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

	@Test
	public void test2_IO31() {
		final int count = 20;

		final String xsdFile = "/xsd/io31/io31.xsd";
		try {
			final URI uri = this.getClass().getResource(xsdFile).toURI();

			final Params params = new Params();
			List<String> xsds = new ArrayList<>();
			xsds.add(this.getClass().getResource(xsdFile).toExternalForm());
			params.setXsds( xsds);
			params.setMaxFileSize( 10000);
			params.setSeed( 1L);
			params.setEncoding( "iso-8859-1");
//			params.setRootElementName( "test");
			params.setIgnoringValidationErrors(true);

			final String targetDirectory = getTmpDirPath( "hamaxagoga/io31/test");
			log.debug( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			log.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
	@Test
	public void testIO31Plus() {
		final int count = 1;

		final String xsdFile = "/xsd/io31/IO31plus.xsd";
		try {
			final URI uri = this.getClass().getResource(xsdFile).toURI();

			final Params params = new Params();
			List<String> xsds = new ArrayList<>();
			xsds.add(this.getClass().getResource(xsdFile).toExternalForm());
			params.setXsds( xsds);
			params.setMaxFileSize( 1000000);
			params.setSeed( 1L);
			params.setMinOccurs(2);
			params.setMaxOccurs(5);
//			params.setEncoding( "iso-8859-1");
//			params.setRootElementName( "test");
			params.setIgnoringValidationErrors(false);

			final String targetDirectory = getTmpDirPath( "hamaxagoga/io31plus/test");
			log.info( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			log.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}
}
