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
    private static Stream<Arguments> dataForTestClosureNode() {
        return Stream.of(//
                Arguments.of("a", 1, 1), //
                Arguments.of("[a-f]", 1, 4), //
                Arguments.of("[^a-f]", 3, 4) //
        );
    }

    @ParameterizedTest(name = "#{index} - [{0}]")
    @MethodSource("dataForTestClosureNode")
    public void testClosureNode(final String regularXpression, final int min, final int max) {
        final Random random = new SecureRandom();

        final RegularExpression regularExpression = new RegularExpression(regularXpression);

        final RegexTree.TokenNode tokenNode = new RegexTree.TokenNode(random, regularExpression.tokentree);

        RegexTree.Regex regex = new RegexTree.Regex(random, tokenNode);

        RegexTree.ClosureNode closureNode =
                new RegexTree.ClosureNode( random, regex);

        final String randomizedValue = closureNode.getRandomizedValue( min, max);

        Assertions.assertNotNull(randomizedValue);
        Assertions.assertTrue(randomizedValue.length() >= min, String.format(
                "Expected string of minimum length %d, but was '%s'", min, randomizedValue));
        Assertions.assertTrue(randomizedValue.length() <= max, String.format(
                "Expected string of maximum length %d, but was '%s'", max, randomizedValue));
    }

    @SuppressWarnings({ "unused" }) // Eclipse thinks this method is not used.
    private static Stream<Arguments> dataForTestConcatNode() {
        return Stream.of(//
                Arguments.of("[a-f]", "[x-z]")
                ,Arguments.of("q", "[x-z]")
        );
    }

    @ParameterizedTest(name = "#{index} - [{0}]")
    @MethodSource("dataForTestConcatNode")
    public void testConcatNode(final String regularXpression, final String regularXpression2) {
        final int min = 2;
        final int max = 2;
        final Random random = new SecureRandom();

        RegexTree.ConcatNode concatNode =
                new RegexTree.ConcatNode( random);

        concatNode.add( new RegexTree.TokenNode(random, new RegularExpression(regularXpression).tokentree));
        concatNode.add( new RegexTree.TokenNode(random, new RegularExpression(regularXpression2).tokentree));

        final String randomizedValue = concatNode.getRandomizedValue( min, max);

        Assertions.assertNotNull(randomizedValue);
        Assertions.assertTrue(randomizedValue.length() >= min, String.format(
                "Expected string of minimum length %d, but was '%s'", min, randomizedValue));
        Assertions.assertTrue(randomizedValue.length() <= max, String.format(
                "Expected string of maximum length %d, but was '%s'", max, randomizedValue));
    }

    @SuppressWarnings({ "unused" }) // Eclipse thinks this method is not used.
    private static Stream<Arguments> dataForTestUnionNode() {
        return Stream.of(//
                Arguments.of((Object) new String[]{"1", "2"})
//                ,Arguments.of(new String[] { "a", "b"})
        );
    }

    @ParameterizedTest(name = "#{index} - [{0}]")
    @MethodSource("dataForTestUnionNode")
    public void testUnionNode(final String... regularXpressions) {
        final Random random = new SecureRandom();

        RegexTree.UnionRegex unionRegex =
                new RegexTree.UnionRegex( random);

        for( String regularXpression : regularXpressions) {
            final RegularExpression regularExpression = new RegularExpression(regularXpression);

            final RegexTree.TokenNode tokenNode = new RegexTree.TokenNode(random, regularExpression.tokentree);

            unionRegex.add(tokenNode);
        }

        final String randomizedValue = unionRegex.getRandomizedValue(1,3);

        Assertions.assertNotNull(randomizedValue);
    }

    @Test
    public void test() {
        final Random random = new SecureRandom();

        final String regex = "q[abcde]*";
        RegularExpression regularExpression = new RegularExpression(regex);

        final RegexTree regexTree = new RegexTree(regularExpression, random);

        int min = 1;
        int max = 3;

        final String randomString = regexTree.getRandomString(1, 3);
        Assertions.assertTrue( regularExpression.matches( randomString),
                String.format( "Generated string %s does not match regex %s", randomString, regex));
        Assertions.assertTrue( randomString.length() >= min, String.format("Generated string %s is shorter than %d positions", randomString, min));
        Assertions.assertTrue( randomString.length() <= max
                , String.format("Generated string %s is longer than %d positions", randomString, max));
    }

    @Test
    public void test2() {
        final Random random = new SecureRandom();

        final String regex = "(a*|b)*";
        RegularExpression regularExpression = new RegularExpression(regex);

        final RegexTree regexTree = new RegexTree(regularExpression, random);


        int min = 1;
        int max = 3;

        final String randomString = regexTree.getRandomString(min, max);
        Assertions.assertTrue( regularExpression.matches( randomString),
                String.format( "Generated string %s does not match regex %s", randomString, regex));
        Assertions.assertTrue( randomString.length() >= min, String.format("Generated string %s is shorter than %d positions", randomString, min));
        Assertions.assertTrue( randomString.length() <= max
                , String.format("Generated string %s is longer than %d positions", randomString, max));
    }

    @Test
    public void testBsn() {
        final Random random = new SecureRandom();

        final String regex = "[0-9]{9}";
        RegularExpression regularExpression = new RegularExpression(regex);

        final RegexTree regexTree = new RegexTree(regularExpression, random);

        int min = 1;
        int max = 9;

        final String randomString = regexTree.getRandomString(min, max);
        Assertions.assertTrue( regularExpression.matches( randomString),
                String.format( "Generated string %s does not match regex %s", randomString, regex));
        Assertions.assertTrue( randomString.length() >= max,
                String.format("Generated string %s is shorter than %d positions", randomString, max));
        Assertions.assertTrue( randomString.length() <= max
                , String.format("Generated string %s is longer than %d positions", randomString, max));
    }

    @Test
    public void testVersion() {
        final Random random = new SecureRandom();

        final String regex = "(0|[1-9][0-9]*).(0|[1-9][0-9]*).(0|[1-9][0-9]*)";
        RegularExpression regularExpression = new RegularExpression(regex);

        final RegexTree regexTree = new RegexTree(regularExpression, random);

        final String randomString = regexTree.getRandomString(1, 100);
        Assertions.assertTrue( randomString.length() >= 5,
                String.format("Generated string %s is shorter than %d positions", randomString, 5));
        Assertions.assertTrue( regularExpression.matches( randomString),
                String.format( "Generated string %s does not match regex %s", randomString, regex));
        log.atInfo().log("Generated %s", randomString);
    }

    @Test
    public void testNRange() {
        final Random random = new SecureRandom();

        final String regex = "[^abcde]*";
        RegularExpression regularExpression = new RegularExpression(regex);

        final RegexTree regexTree = new RegexTree(regularExpression, random);

        int min = 1;
        int max = 3;

        final String randomString = regexTree.getRandomString(1, 3);
        Assertions.assertTrue( regularExpression.matches( randomString),
                String.format( "Generated string %s does not match regex %s", randomString, regex));
        Assertions.assertTrue( randomString.length() >= min, String.format("Generated string %s is shorter than %d positions", randomString, min));
        Assertions.assertTrue( randomString.length() <= max
                , String.format("Generated string %s is longer than %d positions", randomString, max));
    }

    @Test
    public void testQuestionMark() {
        final Random random = new SecureRandom();

        final String regex = "[?x]*";
        RegularExpression regularExpression = new RegularExpression(regex);

        final RegexTree regexTree = new RegexTree(regularExpression, random);


        int min = 1;
        int max = 3;

        final String randomString = regexTree.getRandomString(1, 3);
        Assertions.assertTrue( regularExpression.matches( randomString),
                String.format( "Generated string %s does not match regex %s", randomString, regex));
        Assertions.assertTrue( randomString.length() >= min, String.format("Generated string %s is shorter than %d positions", randomString, min));
        Assertions.assertTrue( randomString.length() <= max
                , String.format("Generated string %s is longer than %d positions", randomString, max));
    }

}
