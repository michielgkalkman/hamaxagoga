package org.taHjaj.wo.hamaxagoga.junit;

import java.net.URI;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.RandomXMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

import static org.junit.jupiter.api.Assertions.fail;

public class SPMLTest extends AbstractTestCase {
	private static final Logger logger = Logger.getLogger( OASISTest.class);

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
			logger.debug( "Target directory: " + targetDirectory);
			new RandomXMLGenerator().generate(
					params, targetDirectory, count);
		} catch( final Exception exception) {
			logger.error( exception.getLocalizedMessage(), exception);
			fail( exception.getLocalizedMessage());
		}
	}

}
