package org.taHjaj.wo.hamaxagoga;

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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;

public class Params {
	private int maxFileSize;
	private List<String> xsds = new ArrayList<String>();
	private String rootElementName;
	private int minStringLength;
	private int maxStringLength = 100;
	private int minOccurs = 10;
	private int maxOccurs = 20;
	private int count = 1;
	private File destDir;
	private Random random = new Random();
	private Map< String, String> predefinedNamespacePrefixes = new HashMap<String, String>();
	private String lexicalPattern = "[a-zA-Z0-9é]";
	private String encoding = "UTF-8";
	private boolean validating = true;
	private boolean ignoringValidationErrors = false;
	private int leadingZeroes = 20;
	private int trailingZeroes = 20;
	private boolean fZeroes = true;
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString( this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public List<String> getXsds() {
		return xsds;
	}

	public void addXsd( final URI xsd) {
		xsds.add( xsd.toASCIIString());
	}

	public void addXsd( final String xsd) {
		xsds.add( xsd);
	}

	public String getRootElementName() {
		return rootElementName;
	}

	public void setRootElementName(String rootElementName) {
		this.rootElementName = rootElementName;
	}

	public int getMaxStringLength() {
		return maxStringLength;
	}

	public void setMaxStringLength(int maxLength) {
		this.maxStringLength = maxLength;
	}

	public int getMinStringLength() {
		return minStringLength;
	}

	public void setMinStringLength(int minLength) {
		this.minStringLength = minLength;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	public Map<String, String> getPredefinedNamespacePrefixes() {
		return predefinedNamespacePrefixes;
	}
	
	public void addPredefinedNamespacePrefix( final String URI, final String prefix) {
		predefinedNamespacePrefixes.put( URI, prefix);
	}

	public void setSeed( final long seed) {
	    random = new Random();
	    random.setSeed(seed);
	}

	public int getCount() {
	    return count;
	}

	public void setCount( int count) {
	    this.count = count;
	}

	public File getDestDir() {
	    return destDir;
	}

	public void setDestDir( File destDir) {
	    this.destDir = destDir;
	}

	public Random getRandom() {
	    return random;
	}
	
	public void setRandom( Random random) {
	    this.random = random;
	}

	public void setRandom( RandomGenerator randomGenerator) {
	    this.random = new RandomAdaptor( randomGenerator);
	}

	public String getLexicalPattern() {
		return lexicalPattern;
	}

	public void setLexicalPattern(String lexicalPattern) {
		this.lexicalPattern = lexicalPattern;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isIgnoringValidationErrors() {
		return ignoringValidationErrors;
	}

	public void setIgnoringValidationErrors(boolean ignoringValidationErrors) {
		this.ignoringValidationErrors = ignoringValidationErrors;
	}

	public boolean isValidating() {
		return validating;
	}

	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + count;
		result = PRIME * result + ((destDir == null) ? 0 : destDir.hashCode());
		result = PRIME * result + ((encoding == null) ? 0 : encoding.hashCode());
		result = PRIME * result + (ignoringValidationErrors ? 1231 : 1237);
		result = PRIME * result + ((lexicalPattern == null) ? 0 : lexicalPattern.hashCode());
		result = PRIME * result + maxFileSize;
		result = PRIME * result + maxOccurs;
		result = PRIME * result + maxStringLength;
		result = PRIME * result + minOccurs;
		result = PRIME * result + minStringLength;
		result = PRIME * result + ((predefinedNamespacePrefixes == null) ? 0 : predefinedNamespacePrefixes.hashCode());
		result = PRIME * result + ((random == null) ? 0 : random.hashCode());
		result = PRIME * result + ((rootElementName == null) ? 0 : rootElementName.hashCode());
		result = PRIME * result + (validating ? 1231 : 1237);
		result = PRIME * result + ((xsds == null) ? 0 : xsds.hashCode());
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
		final Params other = (Params) obj;
		if (count != other.count)
			return false;
		if (destDir == null) {
			if (other.destDir != null)
				return false;
		} else if (!destDir.equals(other.destDir))
			return false;
		if (encoding == null) {
			if (other.encoding != null)
				return false;
		} else if (!encoding.equals(other.encoding))
			return false;
		if (ignoringValidationErrors != other.ignoringValidationErrors)
			return false;
		if (lexicalPattern == null) {
			if (other.lexicalPattern != null)
				return false;
		} else if (!lexicalPattern.equals(other.lexicalPattern))
			return false;
		if (maxFileSize != other.maxFileSize)
			return false;
		if (maxOccurs != other.maxOccurs)
			return false;
		if (maxStringLength != other.maxStringLength)
			return false;
		if (minOccurs != other.minOccurs)
			return false;
		if (minStringLength != other.minStringLength)
			return false;
		if (predefinedNamespacePrefixes == null) {
			if (other.predefinedNamespacePrefixes != null)
				return false;
		} else if (!predefinedNamespacePrefixes.equals(other.predefinedNamespacePrefixes))
			return false;
		if (random == null) {
			if (other.random != null)
				return false;
		} else if (!random.equals(other.random))
			return false;
		if (rootElementName == null) {
			if (other.rootElementName != null)
				return false;
		} else if (!rootElementName.equals(other.rootElementName))
			return false;
		if (validating != other.validating)
			return false;
		if (xsds == null) {
			if (other.xsds != null)
				return false;
		} else if (!xsds.equals(other.xsds))
			return false;
		return true;
	}

	public int getLeadingZeroes() {
		return leadingZeroes;
	}

	public void setLeadingZeroes(int leadingZeroes) {
		this.leadingZeroes = leadingZeroes;
	}

	public int getTrailingZeroes() {
		return trailingZeroes;
	}

	public void setTrailingZeroes(int trailingZeroes) {
		this.trailingZeroes = trailingZeroes;
	}

	public boolean isZeroes() {
		return fZeroes;
	}

	public void setZeroes(boolean zeroes) {
		fZeroes = zeroes;
		if( !fZeroes) {
			leadingZeroes = 0;
			trailingZeroes = 0;
		}
	}
}
