package org.apache.xerces.impl.xpath.regex;

import lombok.NonNull;
import lombok.extern.flogger.Flogger;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

@Flogger
public class RegexTree {
    private final RegularExpression regularExpression;

    abstract static class RegexNode {
        protected final Random random;

        public RegexNode(@NonNull final Random random) {
            this.random = random;
        }

        protected abstract RegexNode cloneRegexNode();

        protected abstract void rewritableAsString(final StringBuilder stringBuilder);

        public String toString() {
            return rewritableAsString();
        }

        public String rewritableAsString() {
            final StringBuilder stringBuilder = new StringBuilder();
            rewritableAsString(stringBuilder);
            return stringBuilder.toString();
        }

        private RegularExpression regularExpression;

        public void setRegularExpression(RegularExpression regularExpression) {
            this.regularExpression = regularExpression;
        }

        public String getRandomizedValue(int min, int max) {
            final Set<String> samples = getSamples(min, max);
            final List<String> options = new ArrayList<>();
            samples.forEach(sample -> {
                final String unescapedXml = StringEscapeUtils.unescapeXml(sample);
                if(unescapedXml.length() >= min && unescapedXml.length() <= max) {
                    options.add(sample);
                }
            });

            final String selectedOption = options.get(random.nextInt(options.size()));

            for (String option : options) {
                if(  regularExpression != null && !regularExpression.matches( selectedOption)) {
                    log.atSevere().log("Found one.");
                }
            }

            return selectedOption;
        }

        public abstract Set<String> getSamples(int min, int max);
    }

    static class UnionRegex extends RegexNode {
        private final LinkedList<RegexNode> regexNodes = new LinkedList<>();

        public UnionRegex(@NonNull final Random random) {
            super(random);
        }

        @Override
        protected RegexNode cloneRegexNode() {
            final UnionRegex regex = new UnionRegex(random);
            for( RegexNode regexNode : regexNodes) {
                regex.add(regexNode.cloneRegexNode());
            }
            return regex;
        }

        @Override
        protected void rewritableAsString(StringBuilder stringBuilder) {
            regexNodes.get(0).rewritableAsString(stringBuilder);
            for( int i=1; i<regexNodes.size(); i++) {
                stringBuilder.append('|');
                regexNodes.get(i).rewritableAsString(stringBuilder);
            }
        }

        @Override
        public Set<String> getSamples(int min, int max) {
            final Set<String> samples = new HashSet<>();

            for( int i=0; i<10 && samples.size() < 10; i++) {
                String sample = "";
                int tmpMax = max;

                final RegexNode regexNode = regexNodes.get(random.nextInt(regexNodes.size()));
                samples.addAll(regexNode.getSamples(min, max));
            }

            return samples;
        }

        public void add(@NonNull RegexNode regex) {
            regexNodes.add(regex);
        }
    }

    static class Regex extends RegexNode {
        private final LinkedList<RegexNode> regexNodes = new LinkedList<>();

        public Regex(final Random random) {
            super(random);
        }

        public Regex( @NonNull final Random random, @NonNull RegexNode regex) {
            super(random);
            regexNodes.add(regex);
        }

        public void add(@NonNull RegexNode regex) {
            regexNodes.add(regex);
        }

        @Override
        protected RegexNode cloneRegexNode() {
            final Regex regex = new Regex(random);
            for( RegexNode regexNode : regexNodes) {
                regex.add(regexNode.cloneRegexNode());
            }
            return regex;
        }

        @Override
        protected void rewritableAsString(StringBuilder stringBuilder) {
            for( RegexNode regexNode : regexNodes) {
                regexNode.rewritableAsString(stringBuilder);
            }
        }

        @Override
        public Set<String> getSamples(int min, int max) {
            final Set<String> samples = new HashSet<>();

            for( int i=0; i<10 && samples.size() < 10; i++) {
                for( RegexNode regexNode : regexNodes) {
                    samples.addAll(regexNode.getSamples( min, max));
                }
            }

            return samples;
        }

    }

    static class TokenNode extends RegexNode {
        private final Token token;

        public TokenNode(@NonNull final Random random, Token token) {
            super(random);
            this.token = token;
        }

        public String toString() {
            return "[" + token.toString() + "]";
        }

        @Override
        public Set<String> getSamples(int min, int max) {
            Set<String> samples = new HashSet<>();

            for(int i=0; i<10; i++) {
                String randomSample;
                do {
                    randomSample = getRandomSample();
                } while ( containsUnpairedSurrogates( randomSample));
                samples.add(randomSample);
            }

            return samples;
        }

        // See UnicodeUnpairedSurrogateRemover from commons-text
        private boolean containsUnpairedSurrogates(String randomSample) {
            for( int i=0; i<randomSample.length(); i++) {
                final int codePoint = randomSample.codePointAt(i);
                if (codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE) {
                    // It's a surrogate.
                    return true;
                }
            }
            return false;
        }

        private final Map< Token, Integer> totalRangesSize = new HashMap<>();

        private String getRandomSample() {
            String randomizedValue = "?";

            switch( token.type) {
                case Token.EMPTY: {
                    randomizedValue = "";
                    break;
                }
                case Token.DOT: {
                    randomizedValue = RandomStringUtils.randomAscii(1);
                    break;
                }
                case Token.CHAR: {
                    randomizedValue = StringEscapeUtils.unescapeJava(token.toString());
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

                        if(!fFound) {
                            throw new RuntimeException();
                        }
                    }

                    randomizedValue = String.valueOf(Character.toChars( codePoint));

                    break;
                }
                case Token.NRANGE: {
                    final RangeToken rangeToken = (RangeToken) token;

                    int chosenCharacter = ((int)'!') + random.nextInt(200);

                    {
                        for( int i = 0; i < rangeToken.ranges.length; i += 2) {
                            final int lower = rangeToken.ranges[i];
                            final int upper = rangeToken.ranges[i + 1];

                            if( chosenCharacter > lower) {
                                chosenCharacter += upper - lower;
                            }
                        }
                    }

                    randomizedValue = String.valueOf(Character.toChars( chosenCharacter)[0]);

                    break;
                }
                case Token.STRING: {
                    final Token.StringToken stringToken = (Token.StringToken) token;
                    randomizedValue = stringToken.getString();
                    break;
                }
                default: {
                    log.atSevere().log("TokenNode, type " + token.type + " not supported yet.");
                }
            }

            return randomizedValue;
        }

        @Override
        protected RegexNode cloneRegexNode() {
            return new TokenNode(random, token);
        }

        @Override
        protected void rewritableAsString(StringBuilder stringBuilder) {
            stringBuilder.append(token);
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
    }

    static class ConcatNode extends RegexNode {
        private final List<RegexNode> regexNodes = new ArrayList<>();

        public ConcatNode(@NonNull final Random random) {
            super(random);
        }

        @Override
        protected RegexNode cloneRegexNode() {
            return new ConcatNode(random);
        }

        @Override
        protected void rewritableAsString(StringBuilder stringBuilder) {

        }

        @Override
        public Set<String> getSamples(int min, int max) {
            final Set<String> samples = new HashSet<>();

            int i=0;
            while( samples.size() <= 3 && i<20) {
                String sample = "";
                String smallest = ""; // Always add the smallest possible version

                for(int j = 0; j< regexNodes.size(); j++) {
                    final Set<String> samples1 = regexNodes.get(j).getSamples(min, max);
                    sample = sample + new ArrayList<>(samples1).get(random.nextInt(samples1.size()));

                    final String[] smallestSample = {null};

                    samples1.forEach( sample2 -> {
                        if( smallestSample[0] == null) {
                            smallestSample[0] = sample2;
                        } else if( sample2.length() < smallestSample[0].length()) {
                            smallestSample[0] = sample2;
                        }
                    });

                    smallest += smallestSample[0];
                }

                if( sample.length() <= max) {
                    samples.add(sample);
                }

                samples.add(smallest);

                i++;
            }

            return samples;
        }

        public void add(@NonNull final RegexNode regexNode) {
            regexNodes.add(regexNode);
        }
    }

    static class ClosureNode extends RegexNode {
        private final Regex template;
        private final Regex rewritable;
//        private final int min;
//        private final int max;

        public ClosureNode(@NonNull Random random, @NonNull Regex regex) {
            super(random);
            this.template = regex;
            this.rewritable = new Regex(random);

//            this.min = min;
//            this.max = max;
        }

        @Override
        protected RegexNode cloneRegexNode() {
            return new ClosureNode(random, template);
        }

        @Override
        protected void rewritableAsString(StringBuilder stringBuilder) {
            rewritable.rewritableAsString(stringBuilder);
        }

        @Override
        public Set<String> getSamples( int min, int max) {
            final Set<String> samples = new HashSet<>();

            final List<String> templateSamples = new ArrayList<>(template.getSamples(min, max));

            for( int i = 0; i<10; i++) {
                String sample = templateSamples.get(random.nextInt( templateSamples.size()));

                while( sample.length() < min) {
                    sample = sample + templateSamples.get(random.nextInt( templateSamples.size()));
                }

                String temp = sample;

                while(random.nextBoolean() && sample.length() <= max) {
                    temp = sample;
                    sample = sample + templateSamples.get(random.nextInt( templateSamples.size()));
                }
                samples.add(temp);
            }

            // Always add empty string in case of closure (X*)
            samples.add("");

            return samples;
        }
    }

    private final RegexNode root;
    private final int min;
    private final int max;


    public RegexTree(RegularExpression regularExpression, Random random) {
        this.regularExpression = regularExpression;
        final Token token = regularExpression.tokentree;
        root = createRegex(random, token);
        min = token.getMin();
        max = token.getMax() < 0 ? 100 : token.getMax();
    }

//    public RegexTree(final Token token, final Random random) {
//        root = createRegex(random, token);
//        min = token.getMin();
//        max = token.getMax() < 0 ? 10 : token.getMax();
//    }

    public String getRandomString(int _min, int _max) {
        String randomizedValue;
        do {
            randomizedValue = root.getRandomizedValue(_min > min ? _min : min, _max);
        } while( containsUnpairedSurrogates( randomizedValue));
//        final String escapedRandomizedValue = StringEscapeUtils.escapeXml11(randomizedValue);
        final String escapedRandomizedValue = randomizedValue;
        return escapedRandomizedValue;
    }

    public String getRandomString() {
        String randomizedValue;
        do {
            randomizedValue = root.getRandomizedValue( min, max);
        } while( containsUnpairedSurrogates( randomizedValue));
//        final String escapedRandomizedValue = StringEscapeUtils.escapeXml11(randomizedValue);
        final String escapedRandomizedValue = randomizedValue;
        return randomizedValue;
    }

    // See UnicodeUnpairedSurrogateRemover from commons-text
    private boolean containsUnpairedSurrogates(String randomSample) {
        for( int i=0; i<randomSample.length(); i++) {
            final int codePoint = randomSample.codePointAt(i);
            if (codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE) {
                // It's a surrogate.
                return true;
            }
        }
        return false;
    }

    public void setRegularExpression(RegularExpression regularExpression) {
        root.setRegularExpression(regularExpression);
    }

    private RegexNode createRegex(@NonNull Random random, @NonNull Token token) {
        final RegexNode regexNode;

        switch( token.type) {
            case Token.EMPTY:
            case Token.DOT:
            case Token.CHAR:
            case Token.STRING:
            case Token.RANGE: {
                regexNode = new TokenNode(random,token);
                break;
            }
            case Token.NRANGE: {
                regexNode = new TokenNode(random,token);
                break;
            }
            case Token.UNION: {
                final UnionRegex regex = new UnionRegex( random);

                for( int i=0; i<token.size(); i++) {
                    regex.add( createRegex( random, token.getChild(i)));
                }

                regexNode = regex;
                break;            }
            case Token.PAREN: {
                Regex regex = new Regex(random);

                final Token.ParenToken parenToken = (Token.ParenToken) token;

                regex.add( createRegex(random, parenToken.child));

                regexNode = regex;
                break;
            }
            case Token.CONCAT: {
//                if( token instanceof Token.ConcatToken) {

                    final ConcatNode concatNode = new ConcatNode(random);

                for( int i=0; i<token.size(); i++) {
                        concatNode.add( createRegex( random, token.getChild(i)));
                    }

                    regexNode = concatNode;
//                } else if( token instanceof Token.ParenToken) {
//                    Regex regex = new Regex(random);
//
//                    final Token.ParenToken parenToken = (Token.ParenToken) token;
//
//                    regex.add( createRegex(random, parenToken.child));
//
//                    regexNode = regex;
//                } else {
//                    UnionRegex unionRegex = new UnionRegex(random);
//
//                    final Token.UnionToken unionToken = (Token.UnionToken) token;
//                    final int size = unionToken.children.size();
//                    for( int i = 0; i < size; i++) {
//                        final Token childToken = unionToken.getChild( i);
//                        unionRegex.add( createRegex(random, childToken));
//                    }
//
//                    regexNode = unionRegex;
//                }
                break;
            }
            case Token.CLOSURE: {
                final RegexNode regexNode1 = createRegex(random, token.getChild(0));

                regexNode = new ClosureNode(
                        random, new Regex(random, regexNode1));

                break;
            }
            default: {
                final String errorString = String.format("Token %s with value '%s' not handled", token.type,
                        token.getString());
                log.atSevere().log(errorString);

                throw new IllegalArgumentException(errorString);
            }
        }

        return regexNode;
    }
}
