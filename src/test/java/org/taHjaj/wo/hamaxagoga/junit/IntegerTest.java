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
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;
import static org.junit.jupiter.api.Assertions.fail;

public class IntegerTest extends AbstractTestCase {
	private static final int REPEATS = 50;
	private static final Logger logger = Logger.getLogger( IntegerTest.class);

	@Test
	public void xtestInteger3a() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/integers.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		params.setRootElementName( "integer4");
		try {
			params.addXsd( url.toURI());
			final String integerTmpOutputDir = getTmpDirPath( "integers");
			logger.debug( "Output files in " + integerTmpOutputDir);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "integers"), count);
			logger.debug( "Output files in " + integerTmpOutputDir);
		} catch( final Exception exception) {
			logger.debug( exception.getLocalizedMessage(), exception);
		}
	}

	@Test
	public void xtestNonNegativeInteger() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/integers.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		params.setRootElementName( "nonnegativeinteger");
		try {
			params.addXsd( url.toURI());
			final String integerTmpOutputDir = getTmpDirPath( "nonnegativeinteger");
			logger.debug( "Output files in " + integerTmpOutputDir);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "integers"), count);
			logger.debug( "Output files in " + integerTmpOutputDir);
		} catch( final Exception exception) {
			logger.debug( exception.getLocalizedMessage(), exception);
		}
	}

	@Test
	public void testTotalDigits() {
		final int count = 3;
		final String xsdFile = "/xsd/integers.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		params.setRootElementName( "totalDigits");
		try {
			params.addXsd( url.toURI());
			final String integerTmpOutputDir = getTmpDirPath( "totalDigits");
			logger.debug( "Output files in " + integerTmpOutputDir);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "totalDigits"), count);
			logger.debug( "Output files in " + integerTmpOutputDir);
		} catch( final Exception exception) {
			logger.debug( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

//	public void testTotalDigits1() {
//		final int count = 3;
//		final String xsdFile = "/xsd/integers.xsd";
//		URL url = this.getClass().getResource(xsdFile);
//		final Params params = new Params();
//		params.setRootElementName( "totalDigits1");
//		try {
//			params.addXsd( url.toURI());
//			final String integerTmpOutputDir = getTmpDirPath( "totalDigits1");
//			logger.debug( "Output files in " + integerTmpOutputDir);
//			new RandomXMLGenerator().generate(
//					params, getTmpDirPath( "totalDigits1"), count, true);
//			logger.debug( "Output files in " + integerTmpOutputDir);
//		} catch( final Exception exception) {
//			logger.debug( exception.getLocalizedMessage(), exception);
//			fail( exception.getLocalizedMessage());
//		}
//	}
}
