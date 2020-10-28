package org.apache.xerces.impl.xpath.regex;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.xerces.impl.xpath.regex.Token.ClosureToken;
import org.apache.xerces.impl.xpath.regex.Token.ConcatToken;
import org.apache.xerces.impl.xpath.regex.Token.ParenToken;
import org.apache.xerces.impl.xpath.regex.Token.StringToken;
import org.apache.xerces.impl.xpath.regex.Token.UnionToken;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RegexGenerator {
    private static final int MAX_RANGE = 5;
    private final RegularExpression regularExpression;
    private final Random random;

    private static Map<String, String> regexConversions = new HashMap<String, String>();
    
    static {
	regexConversions.put( "\\s", "[\u0020\\t\\u000A\\r]");
	regexConversions.put( "\\S", "[^\u0020\\t\\u000A\\r]");
	// @TODO
	// See http://www.w3.org/TR/xml11/#IDAKUDS
	// to see how \c should be replaced.
	regexConversions.put( "\\c", "[\\:_\\.0-9a-zA-Z]");
    }
    
    public RegexGenerator( final Random random, final String regex) {
		super();

		String regex2 = regex;

//		for (Map.Entry<String, String> entry : regexConversions.entrySet()) {
//			regex2 = StringUtils.replace(regex2, entry.getKey(), entry
//					.getValue());
//		}

		regularExpression = new RegularExpression(regex2);
		this.random = random;
	}

    public String getRegex() {
	return regularExpression.getPattern();
    }
    
    public String generateXMLString() {
	final String result;
	
	result = generateString();
	
	final String xmlResult;
	
	if( isISOControl( result)) {
	    xmlResult = toXML( result);
	} else {
	    xmlResult = result;
	}
	
	return xmlResult;
    }

    public String generateString() {
	final String result;
	final StringBuilder stringBuilder = new StringBuilder();

	final Token token = regularExpression.tokentree;

	process( stringBuilder, token);

	result = stringBuilder.toString();
	return result;
    }

    private boolean isISOControl( final String string) {
	boolean fIsISOControl = false;
	for( int index=string.length()-1; ! fIsISOControl && index>=0; index--) {
	    fIsISOControl = Character.isISOControl(   
		    string.charAt( index));
	}
	return fIsISOControl;
    }
    
    private String toXML( final String string) {
	final StringBuilder stringBuilder = new StringBuilder( string.length() + 100);
	
	for( int index=0; index<string.length(); index++) {
	    final char c = string.charAt( index);
	    if( Character.isISOControl( c)) {
		stringBuilder.append( "&#")
		.append( Integer.toHexString( c))
		.append( ";");
	    } else {
		stringBuilder.append( c);
	    }
	}

	return stringBuilder.toString();
    }
    
    private final Map< Token, Integer> totalRangesSize = new HashMap<Token, Integer>();  
    
    void process( final StringBuilder stringBuilder, final Token token) {
	switch( token.type) {
	case Token.CHAR: {
	    stringBuilder.append( (char) token.getChar());
	    break;
	}
	case Token.UNION: {
	    final UnionToken unionToken = (UnionToken) token;
	    final int size = unionToken.children.size();
	    final int choice = random.nextInt( size);

	    final Token childToken = unionToken.getChild( choice);

	    process( stringBuilder, childToken);

	    break;
	}
	case Token.CONCAT: {
	    if( token instanceof ConcatToken) {
		final ConcatToken concatToken = (ConcatToken) token;

		process( stringBuilder, concatToken.child);
		process( stringBuilder, concatToken.child2);
	    } else {
		final UnionToken unionToken = (UnionToken) token;
		final int size = unionToken.children.size();
		for( int i = 0; i < size; i++) {
		    final Token childToken = unionToken.getChild( i);
		    process( stringBuilder, childToken);
		}

		break;
	    }
	    break;
	}
	case Token.CLOSURE: {
	    final ClosureToken closureToken = (ClosureToken) token;

	    final int min;
	    if( closureToken.min == -1) {
		min = 0;
	    } else {
		min = closureToken.min;
	    }

	    final int max;
	    if( closureToken.max == -1) {
		max = MAX_RANGE;
	    } else {
		max = closureToken.max;
	    }

	    final int range = (max - min) + 1;

	    final int choice = min + random.nextInt( range);

	    for( int i = 0; i < choice; i++) {
		process( stringBuilder, closureToken.child);
	    }
	    break;
	}
	case Token.RANGE: {
	    final RangeToken rangeToken = (RangeToken) token;

	    final int nrCharactersInRanges;
	    
	    if( totalRangesSize.containsKey( rangeToken)) {
		nrCharactersInRanges = totalRangesSize.get( rangeToken);
	    } else {
		nrCharactersInRanges = determineNrCharactersInRanges( rangeToken);
		totalRangesSize.put( rangeToken, nrCharactersInRanges);
	    }

	    final int choice = random.nextInt( nrCharactersInRanges);

	    int codePoint = 0;
	    {
		boolean fFound = false;
		int remainingRange = choice;
		for( int i = 0; i < rangeToken.ranges.length; i += 2) {
		    final int lower = rangeToken.ranges[i];
		    final int upper = rangeToken.ranges[i + 1];
		    final int currentRange = upper - lower + 1;

		    if( currentRange > remainingRange) {
			codePoint = lower + remainingRange;
			fFound = true;
			break;
		    }

		    remainingRange -= currentRange;
		}

		if( fFound == false) {
		    throw new RuntimeException();
		}
	    }
	    
	    stringBuilder.append( Character.toChars(  codePoint));

	    break;
	}
	case Token.NRANGE: {
	    final RangeToken rangeToken = (RangeToken) token;

	    int count = 0;
	    {
		for( int i = 0; i < rangeToken.ranges.length; i += 2) {
		    final int lower = rangeToken.ranges[i];
		    final int upper = rangeToken.ranges[i + 1];
		    count += upper;
		    count -= lower;
		    count++;
		}
	    }

	    int choice = random.nextInt( count);
		int chosenCharacter = 0;
		count = 0;
		for( int i = 0; i < rangeToken.ranges.length; i += 2) {
			final int lower = rangeToken.ranges[i];
			final int upper = rangeToken.ranges[i + 1];
			for( int j = lower; j <= upper; j++ ) {
				if( count == choice) {
					chosenCharacter = j;
				}
				count++;
			}
		}

		if( chosenCharacter == 0) {
			chosenCharacter = (int) 'a';
		}

		stringBuilder.append( (char) chosenCharacter);

//		{
//		boolean fFound = false;
//		{
//		    int lower = 0;
//		    final int upper = rangeToken.ranges[0];
//
//		    if( choice >= lower && choice < upper) {
//			fFound = true;
//		    }
//		}
//
//		for( int i = 0; !fFound && i < rangeToken.ranges.length; i += 2) {
//		    final int lower = rangeToken.ranges[i];
//		    final int upper = rangeToken.ranges[i + 1];
//		    final int currentRange = upper - lower + 1;
//
//		    if( choice < lower) {
//			fFound = true;
//		    } else {
//			choice += currentRange;
//		    }
//		}

		// No ranges left, so choice should now contain the correct
		// character.
//	    }
//
//	    stringBuilder.append( (char) choice);

	    break;
	}
	case Token.PAREN: {
	    final ParenToken parenToken = (ParenToken) token;

	    process( stringBuilder, parenToken.child);
	    break;
	}
	case Token.EMPTY: {
	    // hehe.
	    break;
	}
	case Token.STRING: {
	    final StringToken stringToken = (StringToken) token;
	    stringBuilder.append( stringToken.getString());
	    break;
	}
	case Token.DOT: {
	    stringBuilder.append( XMLChar.random( random, 1));
	    break;
	}
	default: {
	    final String logMessage = "Token with type " + token.type
		    + " not implemented yet.";
	    log.debug( logMessage);
	    throw new RuntimeException( logMessage);
	}
	}
    }

    private int determineNrCharactersInRanges( final RangeToken rangeToken) {
	int nrCharactersInRanges;
	nrCharactersInRanges = 0;
	{
	for( int i = 0; i < rangeToken.ranges.length; i += 2) {
	    final int lower = rangeToken.ranges[i];
	    final int upper = rangeToken.ranges[i + 1];
	    nrCharactersInRanges += upper;
	    nrCharactersInRanges -= lower;
	    nrCharactersInRanges++;
	}
	}
	return nrCharactersInRanges;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(  this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
