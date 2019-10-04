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
 * 
 * @author Michiel Kalkman
 */

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class SimpleXSDsTest extends AbstractTestCase {
	private static final Logger logger = Logger.getLogger(SimpleTest.class);

	private static final int REPEATS = 5;

	public void testSimpleXSDs() throws URISyntaxException {
		final FileFilter fileFilter = FileFilterUtils.suffixFileFilter("xsd");
		final int count = REPEATS;
		final URL url = this.getClass().getResource("/xsd/simple");
		if (url != null) {
			final File xsdSimpleDirectory = new File(url.toURI());
			final File[] xsdFiles = xsdSimpleDirectory.listFiles(fileFilter);

			for (final File xsdFile : xsdFiles) {
				final File tmpDir = new File( getTmpDir("simple/tmp"),
						xsdFile.getName());
				final Params params = new Params();
				params.addXsd(xsdFile.toURI());

				final XSModel xsmodel;
				try {
					xsmodel = XMLGenerator.getXsModel(params);
				} catch ( final Exception exception) {
					throw new HamaxagogaException( exception);
				}
				
				final XSNamedMap namedMap = xsmodel.getComponents(XSConstants.ELEMENT_DECLARATION);
				
				final int nrRootElements = namedMap.getLength();
				if (nrRootElements == 0) {
					throw new HamaxagogaException(
							"There is no root element available in the provided XML schemas"
									+ " to create XML instances from");
				}

				for( int rootElementIndex=0; rootElementIndex<nrRootElements; rootElementIndex++) {
					final XSElementDecl elementDecl = (XSElementDecl) namedMap
										.item( rootElementIndex);
					final String rootName = elementDecl.getName();
					
					final File targetDir = new File( tmpDir, rootName);
					
					logger.debug(">>> Processing " + xsdFile.getAbsolutePath()
							+ " Output to " + targetDir);
					
					for (int i = 0; i < REPEATS; i++) {
						try {
							new RandomXMLGenerator().generate(params, targetDir,
									count);
						} catch (final Exception exception) {
							logger.error(exception.getLocalizedMessage(),
											exception);
							fail(exception.getLocalizedMessage());
						}
					}
				}
			}
		}
	}
}
