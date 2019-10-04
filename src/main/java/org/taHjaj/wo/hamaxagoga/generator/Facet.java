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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.taHjaj.wo.hamaxagoga.HamaxagogaException;

public class Facet {
	public static enum WhiteSpaceType { preserve, replace, collapse }
	
	private int minLength;
	private int maxLength;
	private int fractionDigits;
	private String maxExclusive;
	private String maxInclusive;
	private String minExclusive;
	private String minInclusive;
	private int totalDigits;
	private WhiteSpaceType whiteSpaceType;
	private int length;	
	
	public Facet( final XSObjectList objectList) throws HamaxagogaException {
		if (objectList != null && objectList.getLength() > 0) {
			processFacets( objectList);
		}
	}

	private void processFacets( final XSObjectList objectList) {
	    for (int i = 0; i < objectList.getLength(); i++) {
	    	final XSFacet facet = (XSFacet) objectList.item(i);
	    	processFacet( facet);
	    }
	}

	private void processFacet( final XSFacet facet) {
	    switch (facet.getFacetKind()) {
	    	case XSSimpleTypeDefinition.FACET_MINLENGTH: {
	    		minLength = Integer.parseInt(facet.getLexicalFacetValue());
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_MAXLENGTH: {
	    		maxLength = Integer.parseInt(facet.getLexicalFacetValue());
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_WHITESPACE: {
	    		// http://www.w3.org/TR/xmlschema-2#dt-whiteSpace
	    		if( "preserve".equals( facet.getLexicalFacetValue())) {
	    			whiteSpaceType = WhiteSpaceType.preserve;
	    		} else if( "replace".equals( facet.getLexicalFacetValue())) {
	    			whiteSpaceType = WhiteSpaceType.replace;
	    		} else if( "collapse".equals( facet.getLexicalFacetValue())) {
	    			whiteSpaceType = WhiteSpaceType.collapse;
	    		} else {
	    			throw new HamaxagogaException("Illegal value for whitespace facet.");
	    		}
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_ENUMERATION: { // 2048
	    		throw new HamaxagogaException("Facet "
	    				+ facet.getFacetKind() + " is not implemented");
	    	}
	    	case XSSimpleTypeDefinition.FACET_FRACTIONDIGITS: {
	    		fractionDigits = Integer.valueOf( facet.getLexicalFacetValue());
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_LENGTH: { // 1
	    		length = Integer.valueOf( facet.getLexicalFacetValue());
	    		minLength = length;
	    		maxLength = length;
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE: { // 64
	    		maxExclusive = facet.getLexicalFacetValue();
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_MAXINCLUSIVE: { // 32
	    		maxInclusive = facet.getLexicalFacetValue();
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_MINEXCLUSIVE: { // 128
	    		minExclusive = facet.getLexicalFacetValue();
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_MININCLUSIVE: { // 256
	    		minInclusive = facet.getLexicalFacetValue();
	    		break;
	    	}
	    	case XSSimpleTypeDefinition.FACET_NONE: { // 0
	    		throw new HamaxagogaException("Facet "
	    				+ facet.getFacetKind() + " is not implemented");
	    	}
	    	case XSSimpleTypeDefinition.FACET_PATTERN: { // 8
	    		throw new HamaxagogaException("Facet "
	    				+ facet.getFacetKind() + " is not implemented");
	    	}
	    	case XSSimpleTypeDefinition.FACET_TOTALDIGITS: { // 512
	    		totalDigits = Integer.valueOf( facet.getLexicalFacetValue());
	    		break;
	    	}
	    	default: {
	    		throw new HamaxagogaException("Facet "
	    				+ facet.getFacetKind() + " is not implemented");
	    	}
	    }
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString( this,ToStringStyle.MULTI_LINE_STYLE);
	}

	public int getFractionDigits() {
		return fractionDigits;
	}

	public int getLength() {
		return length;
	}

	public String getMaxExclusive() {
		return maxExclusive;
	}

	public String getMaxInclusive() {
		return maxInclusive;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public String getMinExclusive() {
		return minExclusive;
	}

	public String getMinInclusive() {
		return minInclusive;
	}

	public int getMinLength() {
		return minLength;
	}

	public int getTotalDigits() {
		return totalDigits;
	}

	public WhiteSpaceType getWhiteSpaceType() {
		return whiteSpaceType;
	}

	public void setFractionDigits(int fractionDigits) {
		this.fractionDigits = fractionDigits;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setMaxExclusive(String maxExclusive) {
		this.maxExclusive = maxExclusive;
	}

	public void setMaxInclusive(String maxInclusive) {
		this.maxInclusive = maxInclusive;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setMinExclusive(String minExclusive) {
		this.minExclusive = minExclusive;
	}

	public void setMinInclusive(String minInclusive) {
		this.minInclusive = minInclusive;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public void setTotalDigits(int totalDigits) {
		this.totalDigits = totalDigits;
	}

	public void setWhiteSpaceType(WhiteSpaceType whiteSpaceType) {
		this.whiteSpaceType = whiteSpaceType;
	}
}
