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
    @SuppressWarnings({ "unused" }) // Eclipse thinks this method is not used.
    private static Stream<Arguments> dataForTestSearchString() {
        return Stream.of(//
                Arguments.of("a", 1, 1), //
                Arguments.of("[a-f]", 1, 4), //
                Arguments.of("[^a-f]", 1, 4) //
        );
    }

    @ParameterizedTest(name = "#{index} - [{0}]")
    @MethodSource("dataForTestSearchString")
    public void testClosureNode(final String regularXpression, final int min, final int max) {
        final Random random = new SecureRandom();

        final RegularExpression regularExpression = new RegularExpression(regularXpression);

        final RegexTree.TokenNode tokenNode = new RegexTree.TokenNode(random, regularExpression.tokentree);

        RegexTree.Regex regex = new RegexTree.Regex(random, tokenNode);

        RegexTree.ClosureNode closureNode =
                new RegexTree.ClosureNode( random, regex, min, max);

        closureNode.randomize();

        final String randomizedValue = closureNode.getRandomizedValue();

        Assertions.assertNotNull(randomizedValue);
        Assertions.assertTrue(randomizedValue.length() >= min, String.format(
                "Expected string of minimum length %d, but was '%s'", min, randomizedValue));
        Assertions.assertTrue(randomizedValue.length() <= max, String.format(
                "Expected string of maximum length %d, but was '%s'", max, randomizedValue));
    }
}
