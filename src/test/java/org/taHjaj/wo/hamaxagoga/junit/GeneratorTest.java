package org.taHjaj.wo.hamaxagoga.junit;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.generator.Facet;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class GeneratorTest extends AbstractTestCase {
	private static final Logger logger = Logger.getLogger( GeneratorTest.class);
	private Mockery mockery = new Mockery();

	public void testDoubleGenerator() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(new Double(0.3)));
			}
		});

		params.setRandom(random);
		final XMLGenerator generator = new XMLGenerator(params);

		final Facet facet = new Facet(null);

		facet.setMinExclusive("9");
		facet.setMaxExclusive("9999");

		final int fractionDigits = 1;
		final String doubleValue = generator.getDoubleValue(facet, fractionDigits);
		
		assertEquals( "3006.4", doubleValue);
	}

	public void testDoubleGenerator2() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(new Double(0.03)));
			}
		});

		params.setRandom(random);
		final XMLGenerator generator = new XMLGenerator(params);

		final Facet facet = new Facet(null);

		facet.setMinExclusive("9");
		facet.setMaxExclusive("9999");

		final int fractionDigits = 1;
		final String doubleValue = generator.getDoubleValue(facet, fractionDigits);
		
		assertEquals( "309.6", doubleValue);
	}

	public void testDoubleGenerator3() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(new Double(0.97)));
			}
		});

		params.setRandom(random);
		final XMLGenerator generator = new XMLGenerator(params);

		final Facet facet = new Facet(null);

		facet.setMinExclusive("9");
		facet.setMaxExclusive("9999");

		final int fractionDigits = 1;
		final String doubleValue = generator.getDoubleValue(facet, fractionDigits);
		
		assertEquals( "9698.4", doubleValue);
	}

	public void testDoubleGenerator4() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(new Double(0.97)));
			}
		});

		params.setRandom(random);
		final XMLGenerator generator = new XMLGenerator(params);

		final Facet facet = new Facet(null);

		facet.setMinExclusive("9");
		facet.setMaxExclusive("9999");
		facet.setTotalDigits( 5);
		
		final int fractionDigits = 1;
		final String doubleValue = generator.getDoubleValue(facet, fractionDigits);
		
		assertEquals( "9698.4", doubleValue);
	}

	public void testDoubleGenerator5() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(new Double(0.97)));
			}
		});

		params.setRandom(random);
		final XMLGenerator generator = new XMLGenerator(params);

		final Facet facet = new Facet(null);

		facet.setMinInclusive("4");
		facet.setMaxInclusive("5");
		
		final int fractionDigits = 1;
		final String doubleValue = generator.getDoubleValue(facet, fractionDigits);
		
		assertEquals( "5.0", doubleValue);
	}

	public void testDecimalGenerator() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(new Double(0.97)));
			}
		});

		params.setRandom(random);
		final XMLGenerator generator = new XMLGenerator(params);

		final Facet facet = new Facet(null);

		facet.setTotalDigits(6);
		facet.setFractionDigits(3);
		
		final String doubleValue = generator.getDecimalValue( facet);
		
		assertEquals( "9399.06", doubleValue);
	}
}
