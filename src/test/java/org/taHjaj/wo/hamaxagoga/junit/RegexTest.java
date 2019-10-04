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
 */

import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.xerces.impl.xpath.regex.REUtil;
import org.apache.xerces.impl.xpath.regex.RegexGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class RegexTest extends AbstractTestCase {
    private static final Logger logger = Logger.getLogger( RegexTest.class);

    @Override
    protected void setUp() throws Exception {
	// TODO Auto-generated method stub
	super.setUp();
    }

    public void testSimple() {
	doTestRegex( "[À-ÿ]");
	doTestRegex( "[\\-+]?[0-9]+");
	doTestRegex( "[0-9]{6}");
	// From http://www.w3.org/TR/xmlschema11-2/#dt-pattern
	// 'better-us-zipcode'
	doTestRegex( "[0-9]{5}(-[0-9]{4})?");
	doTestRegex( ".");
	doTestRegex( "[^a-zA-Z]");
	doTestRegex( "[ab]{2,4}");
	doTestRegex( "[ab]{3,}");
	doTestRegex( "[ab]{0,3}");
	doTestRegex( "[ab]{0,0}");
	doTestRegex( "[ab]*");
	doTestRegex( "\\.\\\\\\?\\*\\+\\{\\}\\(\\)\\|\\[\\]");
	doTestRegex( "[^a-ze-f]");
	doTestRegex( "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
	doTestRegex( "[[:alpha:]]");
	doTestRegex( "\\cc+");
	doTestRegex( "\\i\\c*");
//	@TODO Regular expressing fails on this one, no idea what this states.
//	testRegex( "[\\i-[:]][\\c-[:]]*");
	doTestRegex( "[^DT]*");
	doTestRegex( "male|female");

//	Is this wrong ?
//	testRegex( "\\c+");

	doTestRegex( "\\p{IsBasicLatin}");
	doTestRegex( "\\p{IsArabic}");
	doTestRegex( "\\p{Nl}");
	doTestRegex( "\\P{L}");
	doTestRegex( "\\p{Zs}");
	
	doTestRegex( "\\s");
	doTestRegex( "\\S");
	doTestRegex( "\\d");
	
	doTestRegex( "\\c@");
	doTestRegex( "\\c\u0040");
	doTestRegex( "\\c");
	
	doTestRegex( "\\cc+");
    }
   
    private void doTestRegex( final String regex) {
	final RegexGenerator regexGenerator = new RegexGenerator( new Random(),
		regex);

	logger.debug( "Generated strings from:" + regex);

	for( int i = 0; i < 25; i++) {
	    run( regexGenerator, regex);
	}
    }

    private void run( final RegexGenerator regexGenerator, final String regex) {
	try {
	    final String generatedString = regexGenerator.generateString();

	    logger.debug( "Generated string:" + generatedString);

	    assertTrue( "Generated string is invalid",
	    	REUtil.matches(  regexGenerator.getRegex(), generatedString));
	} catch( Exception e) {
	    logger.error( e.getLocalizedMessage());
	}
    }
}
