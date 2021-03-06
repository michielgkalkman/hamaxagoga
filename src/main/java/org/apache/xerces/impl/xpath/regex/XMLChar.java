package org.apache.xerces.impl.xpath.regex;

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

import java.util.Random;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.RandomStringUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class XMLChar {
	private static char[] validXmlChars;
	private static final int NR_CHARS = 65535;
	
	static {
		final StringBuffer stringBuffer = new StringBuffer( NR_CHARS);
		for( int i=0; i<NR_CHARS; i++) {
			if( org.apache.xerces.util.XMLChar.isValid( i)) {
				final char c = (char) i;
				if( !CharUtils.isAsciiControl( c) || (isLatin(c) || isMath( c))) {
//					if( org.apache.xerces.util.XMLChar.isContent(c) && (isLatin(c) || isMath( c))) {
						stringBuffer.append( (char)i);
//					}
				}
			}
		}

		// Temp using small number of characters
//		final StringBuffer stringBuffer = new StringBuffer( "abcdefghijklmnoq");


		validXmlChars = stringBuffer.toString().toCharArray();
		
		log.debug( validXmlChars.length + stringBuffer.toString());
	}
	
	public static String random( final Random random, final int i) {
		return RandomStringUtils.random( i,
				0, validXmlChars.length, true, true, validXmlChars, random);
	}
	
	public static boolean isLatin( char c) {
		return  Character.UnicodeBlock.of( c) == Character.UnicodeBlock.BASIC_LATIN
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.LATIN_1_SUPPLEMENT
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.LATIN_EXTENDED_A
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.LATIN_EXTENDED_B
		;
	}
	
	public static boolean isMath( char c) {
		return  Character.UnicodeBlock.of( c) == Character.UnicodeBlock.MATHEMATICAL_ALPHANUMERIC_SYMBOLS
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.MATHEMATICAL_OPERATORS
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A
		|| Character.UnicodeBlock.of( c) == Character.UnicodeBlock.MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B
		; // ≤≤≤
	}
}
