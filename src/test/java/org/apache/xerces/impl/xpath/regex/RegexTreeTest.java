package org.apache.xerces.impl.xpath.regex;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Stream;


@Log4j2
public class RegexTreeTest {
    @Test
    public void testClosureNode() {
        final Random random = new SecureRandom();

        RegexTree.Regex regex = new RegexTree.Regex(random);

        RegexTree.ClosureNode closureNode =
                new RegexTree.ClosureNode( random, regex, 1, 2);

        closureNode.randomize();

        final String randomizedValue = closureNode.getRandomizedValue();

        Assertions.assertNotNull(randomizedValue);
        Assertions.assertEquals("", randomizedValue);
    }

    @SuppressWarnings({ "unused" }) // Eclipse thinks this method is not used.
    private static Stream<Arguments> dataForTestSearchString() {
        return Stream.of(//
                Arguments.of("a"), //
                Arguments.of("[a-f]"), //
                Arguments.of("[^a-f]") //
        );
    }

    @ParameterizedTest(name = "#{index} - [{0}]")
    @MethodSource("dataForTestSearchString")
    public void testClosureNode2(final String regularXpression) {
        final Random random = new SecureRandom();

        final RegularExpression regularExpression = new RegularExpression(regularXpression);

        final RegexTree.TokenNode tokenNode = new RegexTree.TokenNode(random, regularExpression.tokentree);

        RegexTree.Regex regex = new RegexTree.Regex(random, tokenNode);

        RegexTree.ClosureNode closureNode =
                new RegexTree.ClosureNode( random, regex, 1, 2);

        closureNode.randomize();

        final String randomizedValue = closureNode.getRandomizedValue();

        Assertions.assertNotNull(randomizedValue);
        Assertions.assertTrue(randomizedValue.length() == 1, "Expected string of length 1, but was '" + randomizedValue + "'");
    }
}
