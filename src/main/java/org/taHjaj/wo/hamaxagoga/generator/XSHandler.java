package org.taHjaj.wo.hamaxagoga.generator;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XSHandler extends DefaultHandler {
	private static final Logger logger = Logger.getLogger( XSHandler.class);
	private boolean valid = true;
	private final int maxErrors;
	private int nrErrors = 0;
	private List<String> parseErrors = new ArrayList<String>();
	
	public XSHandler() {
		super();
		maxErrors = 3;
	}
	
	public XSHandler( final int maxErrors) {
		super();
		this.maxErrors = maxErrors;
	}
	
	@Override
	public void error( final SAXParseException parseException) {
		if( nrErrors < maxErrors) {
			nrErrors++;
			final String errorMsg = "line:" + parseException.getLineNumber()
			+ ", col:" + parseException.getColumnNumber()
			+ ":" + parseException.getLocalizedMessage();
			parseErrors.add( errorMsg);
			logger.error( errorMsg, parseException);
		}
		valid = false;
	}

	@Override
	public void fatalError( final SAXParseException parseException) {
		if( nrErrors < maxErrors) {
			nrErrors++;
			final String errorMsg = "line:" + parseException.getLineNumber()
			+ ", col:" + parseException.getColumnNumber()
			+ ":" + parseException.getLocalizedMessage();
			parseErrors.add( errorMsg);
			logger.error( errorMsg, parseException);
		}
		valid = false;
	}

	@Override
	public void warning( final SAXParseException parseException) {
		final String errorMsg = "line:" + parseException.getLineNumber()
		+ ", col:" + parseException.getColumnNumber()
		+ ":" + parseException.getLocalizedMessage();
		logger.error( errorMsg, parseException);
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public List<String> getParseErrors() {
		return parseErrors;
	}

	public void setParseErrors(List<String> parseErrors) {
		this.parseErrors = parseErrors;
	}
}
