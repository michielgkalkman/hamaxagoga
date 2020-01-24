package org.taHjaj.wo.hamaxagoga.junit;

import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SPMLTest extends AbstractTestCase {
	@Test
	public void testSPML() {
		final int count = 20;
		try {
			final URI uri = new URI( "http://docs.oasis-open.org/provision/spml-2.0-cd-01/xsd/pstc_spmlv2_core.xsd");
			
			final Params params = new Params();
			params.addXsd( uri);
			params.setMaxFileSize( 1000);
			params.setMaxOccurs(4);
			params.setSeed( 1L);
			params.setRootElementName( "addRequest");
		
			final String targetDirectory = getTmpDirPath( "hamaxagoga/spml");
			log.debug( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			log.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

}
