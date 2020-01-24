package org.taHjaj.wo.hamaxagoga.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.math3.random.RandomGenerator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;
import org.taHjaj.wo.hamaxagoga.Params;
import org.taHjaj.wo.hamaxagoga.generator.Facet;
import org.taHjaj.wo.hamaxagoga.generator.XMLGenerator;
import org.taHjaj.wo.hamaxagoga.junit.support.AbstractTestCase;

public class GeneratorTest extends AbstractTestCase {
	private Mockery mockery = new Mockery();

	@Test
	public void testDoubleGenerator() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(0.3));
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

	@Test
	public void testDoubleGenerator2() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(0.03));
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

	@Test
	public void testDoubleGenerator3() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(0.97));
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

	@Test
	public void testDoubleGenerator4() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(0.97));
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

	@Test
	public void testDoubleGenerator5() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(0.97));
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

	@Test
	public void testDecimalGenerator() {
		final Params params = new Params();

		final RandomGenerator random = mockery.mock( RandomGenerator.class);
		
		// expectations
		mockery.checking(new Expectations() {
			{
				one(random).nextDouble();
				will(returnValue(0.97));
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
