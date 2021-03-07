package org.taHjaj.wo.hamaxagoga.junit;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

import org.apache.xerces.impl.xpath.regex.REUtil;
import org.apache.xerces.impl.xpath.regex.RegexGenerator;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RegexTest extends AbstractTestCase {
	@Test
	public void testDebugSimple() {
		doTestRegex("\\cC");
	}

	@Test
	public void testSimple() {
		doTestRegex("[�-�]");
		doTestRegex("[\\-+]?[0-9]+");
		doTestRegex("[0-9]{6}");
		// From http://www.w3.org/TR/xmlschema11-2/#dt-pattern
		// 'better-us-zipcode'
		doTestRegex("[0-9]{5}(-[0-9]{4})?");
		doTestRegex(".");
		doTestRegex("[^a-zA-Z]");
		doTestRegex("[ab]{2,4}");
		doTestRegex("[ab]{3,}");
		doTestRegex("[ab]{0,3}");
		doTestRegex("[ab]{0,0}");
		doTestRegex("[ab]*");
		doTestRegex("\\.\\\\\\?\\*\\+\\{\\}\\(\\)\\|\\[\\]");
		doTestRegex("[^a-ze-f]");
		doTestRegex("[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
		doTestRegex("[[:alpha:]]");
//		doTestRegex("\\cc+");
//		doTestRegex("\\i\\c*");
//	@TODO Regular expressing fails on this one, no idea what this states.
//	testRegex( "[\\i-[:]][\\c-[:]]*");
		doTestRegex("[^DT]*");
		doTestRegex("male|female");

//	Is this wrong ?
//	testRegex( "\\c+");

		doTestRegex("\\p{IsBasicLatin}");
		doTestRegex("\\p{IsArabic}");
		doTestRegex("\\p{Nl}");
		doTestRegex("\\P{L}");
		doTestRegex("\\p{Zs}");

		doTestRegex("\\s");
		doTestRegex("\\S");
		doTestRegex("\\d");

		doTestRegex("\\c@");
		doTestRegex("\\c\u0040");
//		doTestRegex("\\c");

//		doTestRegex("\\cc+");
	}

	private void doTestRegex(final String regex) {
		final RegexGenerator regexGenerator = new RegexGenerator(new Random(), regex);

		log.info("Generated strings from:" + regex);

		for (int i = 0; i < 25; i++) {
			run(regexGenerator);
		}
	}

	private void run(final RegexGenerator regexGenerator) {
		try {
			final String generatedString = regexGenerator.generateString();

			log.info("Generated string:" + generatedString);

			assertTrue(REUtil.matches(regexGenerator.getRegex(), generatedString));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}
}
