package org.taHjaj.wo.hamaxagoga.junit;

import lombok.extern.log4j.Log4j2;
import org.apache.xerces.impl.xpath.regex.RegexGenerator;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2
public class MultiRestrictionTest extends AbstractTestCase {
	private static final int REPEATS = 5;
	
	@Test
	public void testMultirestrictions() {
		final int count = REPEATS;
		final String xsdFile = "/xsd/multirestriction.xsd";
		URL url = this.getClass().getResource(xsdFile);
		final Params params = new Params();
		try {
			params.addXsd( url.toURI());
			params.setSeed( 7);
			new RandomXMLGenerator().generate(
					params, getTmpDirPath( "multirestrictions"), count);
		} catch( final Exception exception) {
			log.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

	@Test
	public void testMultirestrictions2() {
		final Random random = new SecureRandom();
		RegexGenerator regexGenerator = new RegexGenerator( random, "(a*|b)*");

		regexGenerator.generateString();
	}
}
