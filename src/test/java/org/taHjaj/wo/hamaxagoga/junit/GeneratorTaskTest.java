package org.taHjaj.wo.hamaxagoga.junit;

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
 */
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.GeneratorTask;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class GeneratorTaskTest extends AbstractTestCase {

	@Test
	public void testSimple() {
		try {
			final GeneratorTask generatorTask = new GeneratorTask();

			generatorTask.execute();

			fail("The generator task must fail if the xsd parameter has not been set");
		} catch (final BuildException buildException) {
		}
	}

	@Test
	public void testSimple2() {
		try {
			final GeneratorTask generatorTask = new GeneratorTask();

			generatorTask.setProject(new Project());

			final String xsd = "/xsd/errors/doesnotexist.xml";
			generatorTask.setXsd(xsd);

			generatorTask.execute();

			fail("The generator task must fail if the destDir parameter has not been set");
		} catch (final BuildException buildException) {
		}
	}

	@Test
	public void testSimple3() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/shiporder.xml";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));

		generatorTask.execute();
	}

	@Test
	public void testSimple4() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/shiporder.xml";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator2");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));
		generatorTask.setMaxOccurs(10);

		generatorTask.execute();
	}

	@Test
	public void testSimple5() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/shiporder.xml";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator3");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));
		generatorTask.setMaxOccurs(10);
		generatorTask.setSeed(10);

		generatorTask.execute();
	}

	@Test
	public void testSimple6() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/shiporder.xml";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator4");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));
		generatorTask.setMaxOccurs(10);
		generatorTask.setSeed(10);
		generatorTask.setRootElement("orderidtype");
		generatorTask.execute();
	}

	@Test
	public void testSimple7() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/shiporder.xml";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator5");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));
		generatorTask.setMaxOccurs(10);
		generatorTask.setSeed(10);
		generatorTask.setCount(10);
		generatorTask.setRootElement("shiporder");
		generatorTask.setMaxStringLength(25);
		generatorTask.setMinStringLength(14);
		generatorTask.execute();
	}

	@Test
	public void testSimple8() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/namespaces/sequence.xsd";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator6");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));
		generatorTask.setMinOccurs(2);
		generatorTask.setMaxFileSize(4096);
		generatorTask.setSeed(10);
		generatorTask.setMaxStringLength(25);
		generatorTask.setMinStringLength(14);
		generatorTask.setDefaultNamespace("http://hamaxagoga");
		generatorTask.execute();
	}

	@Test
	public void testSimple9() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		final String xsdFile = "/xsd/namespaces/sequence.xsd";
		final URL url = this.getClass().getResource(xsdFile);
		generatorTask.setXsd(url.toURI().toASCIIString());

		final String tmpDir = getTmpDirPath("generator7");
		log.debug("Output to " + tmpDir);

		generatorTask.setDestDir(new File(tmpDir));
		generatorTask.setMinOccurs(2);
		generatorTask.setMaxFileSize(4096);
		generatorTask.setSeed(10);
		generatorTask.setMaxStringLength(25);
		generatorTask.setMinStringLength(14);
		generatorTask.setDefaultNamespace("test");
		generatorTask.setNamespacePrefix("abc http://hamaxagoga");
		generatorTask.execute();
	}

	@Test
	public void testNoValidation() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		try {
			final String xsdFile = "/xsd/shiporder.xml";
			final URL url = this.getClass().getResource(xsdFile);
			generatorTask.setXsd(url.toURI().toASCIIString());

			generatorTask.setCount(1);

			final String tmpDir = getTmpDirPath("noValidation");
			log.debug("Output to " + tmpDir);

			generatorTask.setDestDir(new File(tmpDir));

			generatorTask.setValidating(false);

			generatorTask.execute();
		} catch (final BuildException buildException) {
		}
	}

	@Test
	public void testMultipleXSDs() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		try {
			final String xsdFile = "/xsd/shiporder.xml";
			final URL url = this.getClass().getResource(xsdFile);
			generatorTask.addXsd(url.toURI().toASCIIString());

			generatorTask.setCount(1);

			final String tmpDir = getTmpDirPath("noValidation");
			log.debug("Output to " + tmpDir);

			generatorTask.setDestDir(new File(tmpDir));

			generatorTask.setValidating(false);

			generatorTask.execute();
		} catch (final BuildException buildException) {
		}
	}

	@Test
	public void testLexicalPatterns() throws URISyntaxException {
		final GeneratorTask generatorTask = new GeneratorTask();

		generatorTask.setProject(new Project());

		try {
			final String xsdFile = "/xsd/shiporder.xml";
			final URL url = this.getClass().getResource(xsdFile);
			generatorTask.setXsd(url.toURI().toASCIIString());
			generatorTask.setLexicalPattern( "[xyz]");
			
			generatorTask.setCount(1);
			generatorTask.setRootElement( "stringtype");
			
			final String tmpDir = getTmpDirPath("lexicalPattern");
			log.debug("Output to " + tmpDir);

			generatorTask.setDestDir(new File(tmpDir));

			generatorTask.execute();
		} catch (final BuildException buildException) {
		}
	}
}
