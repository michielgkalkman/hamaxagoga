package org.apache.xerces.impl.xpath.regex;

import lombok.NonNull;
import lombok.extern.flogger.Flogger;

import java.util.*;

@Flogger
public class RegexTree {

    private final Random random;

    abstract static class RegexNode {

        abstract public int size();
        abstract boolean randomize();
        abstract String getRandomizedValue();
        abstract StringBuilder getRandomizedValue(StringBuilder stringBuilder);
        protected abstract Iterable<? extends RegexNode> getRegexNodes();

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
                    log.atInfo().log("TokenNode, type " + token.type + " not supported yet.");
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
            return template.size();
        }

        @Override
        boolean randomize() {
            rewritable.randomize( template, min, max);
            return false;
        }

        /**
         * Apply closure node once more
         * @param closureNodes
         */
        public void increase(Set<ClosureNode> closureNodes) {
            rewritable.add(template);
            // TODO add any new closuernodes
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

    public RegexTree(final Token token, final Random random) {
        this.random = random;

        root = createRegex(token, closureNodes);
    }

    public String getRandomString(int min, int max) {
        log.atInfo().log( "Michiel rewritableAsString: " + this.root.rewritableAsString());

        if( root.rewritableAsString().length() < min) {
            while( root.rewritableAsString().length() < min && enlarge(root.size(), min, max)) {
                log.atInfo().log( "Michiel rewritableAsString: " + this.root.rewritableAsString());
            }

        } else if (root.rewritableAsString().length() > max) {

        } else {

        }

        return root.rewritableAsString();
    }

    private boolean enlarge(int size, int min, int max) {
        boolean fEnlarged = false;
        if( size < min) {
            // find all closurenodes. Select one that increases the size of the string,
            // but does not go over it.

            List<ClosureNode> selectableClosureNodes = new ArrayList<>();

            for(ClosureNode closureNode : closureNodes) {
                log.atInfo().log( "Michiel closurenode " + closureNode.template + ":" + closureNode.size());
                if( closureNode.size() + size <= max) {
                    selectableClosureNodes.add(closureNode);
                }
            }

            if( !selectableClosureNodes.isEmpty()) {
                final int nextInt = random.nextInt(selectableClosureNodes.size());

                final ClosureNode closureNode = selectableClosureNodes.get(nextInt);

                closureNode.increase(closureNodes);

                fEnlarged = true;
            }
        }
        return fEnlarged;
    }

    public int size() {
        return root.size();
    }

    private RegexNode createRegex(@NonNull Token token, Set<ClosureNode> closureNodes) {
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
            case Token.PAREN:
            case Token.UNION: {
                final Regex regex = new Regex();

                for( int i=0; i<token.size(); i++) {
                    regex.add( createRegex( token.getChild(i), closureNodes));
                }

                regexNode = regex;
                break;            }
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
                        regex.add( createRegex(childToken, closureNodes));
                    }

                    regexNode = regex;
                }
                break;
            }
            case Token.CLOSURE: {
                final Token.ClosureToken closureToken = (Token.ClosureToken) token;

                final ClosureNode closureNode = new ClosureNode(
                        new Regex(createRegex(token.getChild(0), closureNodes)), token.getMin(), token.getMax());

                closureNodes.add(closureNode);

                regexNode = closureNode;

                break;
            }
//            case Token.NRANGE: {
//                final RangeToken rangeToken = (RangeToken) token;
//                break;
//            }
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
