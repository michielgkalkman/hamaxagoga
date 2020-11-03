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

import lombok.Data;
import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;

import java.io.File;
import java.net.URI;
import java.util.*;

@Data
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
	
	public void addXsd( final URI xsd) {
		xsds.add( xsd.toASCIIString());
	}

	public void addXsd( final String xsd) {
		xsds.add( xsd);
	}

	public void addPredefinedNamespacePrefix( final String URI, final String prefix) {
		predefinedNamespacePrefixes.put( URI, prefix);
	}

	public void setSeed( final long seed) {
	    random = new Random();
	    random.setSeed(seed);
	}

	public void setRandom( Random random) {
	    this.random = random;
	}

	public void setRandom( RandomGenerator randomGenerator) {
	    this.random = new RandomAdaptor( randomGenerator);
	}

	public void setZeroes(boolean zeroes) {
		fZeroes = zeroes;
		if( !fZeroes) {
			leadingZeroes = 0;
			trailingZeroes = 0;
		}
	}
}
