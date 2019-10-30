package org.taHjaj.wo.hamaxagoga;

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
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GeneratorTask extends Task {
	private Params params = new Params();

	public GeneratorTask() {
		super();
	}

	public void setCount(final int count) {
		params.setCount(count);
	}

	public void setDestDir(final File destDir) {
		params.setDestDir(destDir);
	}

	public void setXsd(final String xsd) {
		final String[] xsds = StringUtils.split(xsd);
		for (final String someXsd : xsds) {
			params.addXsd(someXsd);
		}
	}

	public void execute() throws BuildException {
		if (params.getXsds().size() == 0) {
			throw new BuildException(
					"Hamaxagoga requires the xsd parameter to be set"
							+ " with XML schema references, separated by spaces");
		} else if (params.getDestDir() == null) {
			throw new BuildException(
					"Hamaxagoga requires the destDir parameter to be set"
							+ " with the location where generated xml files will go");
		} else {
			this.log("Generating XML file(s) from "
					+ params.getXsds().toString());

			final String destDirPath = params.getDestDir().getAbsolutePath();
			try {
				new RandomXMLGenerator().generate(params, destDirPath, params
						.getCount());
			} catch (Exception exception) {
				throw new BuildException(exception);
			}
		}
	}

	public void setRootElement(String rootElement) {
		params.setRootElementName(rootElement);
	}

	public void setMaxFileSize(int maxFileSize) {
		params.setMaxFileSize(maxFileSize);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public void setMaxOccurs(int maxOccurs) {
		params.setMaxOccurs(maxOccurs);
	}

	public void setMinOccurs(int minOccurs) {
		params.setMinOccurs(minOccurs);
	}

	public void setSeed(long seed) {
		params.setSeed(seed);
	}

	public void setMaxStringLength(int maxLength) {
		params.setMaxStringLength(maxLength);
	}

	public void setMinStringLength(int minLength) {
		params.setMinStringLength(minLength);
	}

	public void setZeroes( final boolean fZeroes) {
		params.setZeroes( fZeroes);
	}

	public void setNamespacePrefix(final String namespacePrefixes) {
		final String[] xsds = StringUtils.split(namespacePrefixes);
		if ((xsds.length & 1) == 1) {
			throw new BuildException(
					"Hamaxagoga requires the namespacePrefix parameter"
							+ " to consists of pairs of URI prefix combinations separated by spaces");
		}

		for (int index = 0; index < xsds.length; index += 2) {
			final String prefix = xsds[index];
			final String uri = xsds[index + 1];
			params.addPredefinedNamespacePrefix(uri, prefix);
		}
	}

	public void setDefaultNamespace(final String namespace) {
		params.addPredefinedNamespacePrefix(namespace, null);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((params == null) ? 0 : params.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GeneratorTask other = (GeneratorTask) obj;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		return true;
	}

	public void addPredefinedNamespacePrefix(String URI, String prefix) {
		params.addPredefinedNamespacePrefix(URI, prefix);
	}

	public void addXsd(String xsd) {
		params.addXsd(xsd);
	}

	public void addXsd(URI xsd) {
		params.addXsd(xsd);
	}

	public int getCount() {
		return params.getCount();
	}

	public File getDestDir() {
		return params.getDestDir();
	}

	public String getEncoding() {
		return params.getEncoding();
	}

	public String getLexicalPattern() {
		return params.getLexicalPattern();
	}

	public int getMaxFileSize() {
		return params.getMaxFileSize();
	}

	public int getMaxOccurs() {
		return params.getMaxOccurs();
	}

	public int getMaxStringLength() {
		return params.getMaxStringLength();
	}

	public int getMinOccurs() {
		return params.getMinOccurs();
	}

	public int getMinStringLength() {
		return params.getMinStringLength();
	}

	public Map<String, String> getPredefinedNamespacePrefixes() {
		return params.getPredefinedNamespacePrefixes();
	}

	public Random getRandom() {
		return params.getRandom();
	}

	public String getRootElementName() {
		return params.getRootElementName();
	}

	public List<String> getXsds() {
		return params.getXsds();
	}

	public boolean isIgnoringValidationErrors() {
		return params.isIgnoringValidationErrors();
	}

	public boolean isValidating() {
		return params.isValidating();
	}

	public void setEncoding(String encoding) {
		params.setEncoding(encoding);
	}

	public void setIgnoringValidationErrors(boolean ignoringValidationErrors) {
		params.setIgnoringValidationErrors(ignoringValidationErrors);
	}

	public void setLexicalPattern(String lexicalPattern) {
		params.setLexicalPattern(lexicalPattern);
	}

	public void setRandom(Random random) {
		params.setRandom(random);
	}

	public void setRandom(RandomGenerator randomGenerator) {
		params.setRandom(randomGenerator);
	}

	public void setRootElementName(String rootElementName) {
		params.setRootElementName(rootElementName);
	}

	public void setValidating(boolean validating) {
		params.setValidating(validating);
	}

}
