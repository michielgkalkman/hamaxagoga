package org.apache.xerces.impl.xpath.regex;

import lombok.NonNull;
import lombok.extern.flogger.Flogger;

import java.util.*;

@Flogger
public class RegexTree {

    private final Random random;

    public String getRandomizedValue() {
        return this.root.getRandomizedValue();
    }

    abstract static class RegexNode {

        abstract public int size();
        abstract boolean randomize();
        abstract String getRandomizedValue();
        abstract StringBuilder getRandomizedValue(StringBuilder stringBuilder);
        protected abstract Iterable<? extends RegexNode> getRegexNodes();

        protected abstract RegexNode cloneRegexNode();

        protected abstract void rewritableAsString(final StringBuilder stringBuilder);

        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder();
            rewritableAsString(stringBuilder);
            stringBuilder.append( "rewritable: ").append(stringBuilder);
            return stringBuilder.toString();
        }

        public String rewritableAsString() {
            final StringBuilder stringBuilder = new StringBuilder();
            rewritableAsString(stringBuilder);
            return stringBuilder.toString();
        }
    }

    class Regex extends RegexNode {
        private final LinkedList<RegexNode> regexNodes = new LinkedList<>();

        public Regex() {
        }

        public Regex(@NonNull RegexNode regex) {
            regexNodes.add(regex);
        }

        public void add(@NonNull RegexNode regex) {
            regexNodes.add(regex);
        }

        @Override
        public int size() {
            int size = 0;
            for (final RegexNode regexNode : regexNodes) {
                size += regexNode.size();
            }
            return size;
        }

        @Override
        boolean randomize() {
            for( RegexNode regexNode : regexNodes) {
                regexNode.randomize();
            }
            return false;
        }

        @Override
        String getRandomizedValue() {
            StringBuilder stringBuilder = new StringBuilder();
            return getRandomizedValue(stringBuilder).toString();
        }

        @Override
        StringBuilder getRandomizedValue(final StringBuilder stringBuilder) {
            for( RegexNode regexNode : regexNodes) {
                regexNode.getRandomizedValue(stringBuilder);
            }
            return stringBuilder;
        }

        @Override
        protected Iterable<? extends RegexNode> getRegexNodes() {
            return regexNodes;
        }

        @Override
        protected RegexNode cloneRegexNode() {
            final Regex regex = new Regex();
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

        public void randomize(Regex template, int min, int max) {
            regexNodes.clear();

            int nrTokens = random.nextInt( max-min) + min;

            for( int i=0; i<nrTokens; i++) {
                for( RegexNode regexNode : template.getRegexNodes()) {
                    final RegexNode clonedRegexNode = regexNode.cloneRegexNode();
                    regexNodes.add(clonedRegexNode);
                    clonedRegexNode.randomize();
                }
            }
        }
    }

    class TokenNode extends RegexNode {
        private final Token token;

        public TokenNode(Token token) {
            this.token = token;
        }

        @Override
        public int size() {
            switch( token.type) {
                case Token.CHAR:
                case Token.RANGE: {
                    return 1;
                }
            }
            return 0;
        }

        public String toString() {
            return "[" + token.toString() + "]";
        }

        private final Map< Token, Integer> totalRangesSize = new HashMap<>();
        private String randomizedValue = "?";

        @Override
        boolean randomize() {
            switch( token.type) {
                case Token.CHAR: {
                    randomizedValue = token.toString();
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

                    int count = 0;
                    {
                        for( int i = 0; i < rangeToken.ranges.length; i += 2) {
                            final int lower = rangeToken.ranges[i];
                            final int upper = rangeToken.ranges[i + 1];
                            count += upper;
                            count -= lower;
                            count++;
                        }
                    }

                    int choice = random.nextInt( count);
                    int chosenCharacter = 0;
                    count = 0;
                    for( int i = 0; i < rangeToken.ranges.length; i += 2) {
                        final int lower = rangeToken.ranges[i];
                        final int upper = rangeToken.ranges[i + 1];
                        for( int j = lower; j <= upper; j++ ) {
                            if( count == choice) {
                                chosenCharacter = j;
                            }
                            count++;
                        }
                    }

                    if( chosenCharacter == 0) {
                        chosenCharacter = 'a';
                    }

                    randomizedValue = String.valueOf( chosenCharacter);

                    break;
                }
                default: {
                    System.out.println("TokenNode, type " + token.type + " not supported yet.");
                }
            }

            return true;
        }

        @Override
        String getRandomizedValue() {
            return randomizedValue;
        }

        @Override
        StringBuilder getRandomizedValue(StringBuilder stringBuilder) {
            return stringBuilder.append(randomizedValue);
        }

        @Override
        protected Iterable<? extends RegexNode> getRegexNodes() {
            return Collections.emptyList();
        }

        @Override
        protected RegexNode cloneRegexNode() {
            return new TokenNode(token);
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

    class ClosureNode extends RegexNode {
        private final Regex template;
        private final Regex rewritable;
        private final int min;
        private final int max;

        public ClosureNode(@NonNull Regex regex, int min, int max) {
            this.template = regex;
            this.rewritable = new Regex();

            this.min = min;
            this.max = max;
        }
        @Override
        public int size() {
            return rewritable.size();
        }

        @Override
        boolean randomize() {
            rewritable.randomize( template, min, max);
            return false;
        }

        @Override
        String getRandomizedValue() {
            return rewritable.getRandomizedValue();
        }

        @Override
        StringBuilder getRandomizedValue(StringBuilder stringBuilder) {
            return rewritable.getRandomizedValue(stringBuilder);
        }

        @Override
        protected Iterable<? extends RegexNode> getRegexNodes() {
            return this.template.regexNodes;
        }

        @Override
        protected RegexNode cloneRegexNode() {
            return new ClosureNode(template, min, max);
        }

        @Override
        protected void rewritableAsString(StringBuilder stringBuilder) {
            rewritable.rewritableAsString(stringBuilder);
        }
    }

    private final RegexNode root;
    private final Set<ClosureNode> closureNodes = new HashSet<>();

    public RegexTree(final Token token, final Random random, int min, int max) {
        this.random = random;

        root = createRegex( token, min, max, closureNodes);



        if( root == null) {
            System.out.println("root is null!");
        } else {
            System.out.println(root.size());

            root.randomize();

            System.out.println(root.size());
            final String randomizedValue = root.getRandomizedValue();
            System.out.println(randomizedValue);

            System.out.println( "Michiel rewritableAsString: " + this.root.rewritableAsString());

            if( randomizedValue.length() < min) {

                enlarge( root.size(), min, max);

            } else if (randomizedValue.length() > max) {

            } else {

            }
        }
    }

    private void enlarge(int size, int min, int max) {
        if( size < min) {
            // find all closurenodes. Select one that increases the size of the string,
            // but does not go over it.
            for(ClosureNode closureNode : closureNodes) {
                System.out.println( "Michiel closurenode " + closureNode.template + ":" + closureNode.size());
            }
        }
    }

    public int size() {
        return root.size();
    }

    private RegexNode createRegex(Token token, int min, int max, Set<ClosureNode> closureNodes) {
        final RegexNode regexNode;

        switch( token.type) {
            case Token.EMPTY:
            case Token.DOT:
            case Token.CHAR:
            case Token.STRING:
            case Token.RANGE: {
                regexNode = new TokenNode(token);
                break;
            }
//            case Token.UNION: {
//                final Token.UnionToken unionToken = (Token.UnionToken) token;
//                break;
//            }
            case Token.CONCAT: {
                if( token instanceof Token.ConcatToken) {
                    final Token.ConcatToken concatToken = (Token.ConcatToken) token;
                    regexNode =  new TokenNode(concatToken);
                } else {
                    Regex regex = new Regex();

                    final Token.UnionToken unionToken = (Token.UnionToken) token;
                    final int size = unionToken.children.size();
                    for( int i = 0; i < size; i++) {
                        final Token childToken = unionToken.getChild( i);
                        regex.add( createRegex(childToken, min, max, closureNodes));
                    }

                    regexNode = regex;
                }
                break;
            }
            case Token.CLOSURE: {
                final Token.ClosureToken closureToken = (Token.ClosureToken) token;

                final ClosureNode closureNode = new ClosureNode(
                        new Regex(createRegex(token.getChild(0), min, max, closureNodes)), min, max);

                closureNodes.add(closureNode);

                regexNode = closureNode;

                break;
            }
//            case Token.NRANGE: {
//                final RangeToken rangeToken = (RangeToken) token;
//                break;
//            }
            case Token.PAREN: {
                final Token.ParenToken parenToken = (Token.ParenToken) token;

                regexNode = createRegex(parenToken, min, max, closureNodes);
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
