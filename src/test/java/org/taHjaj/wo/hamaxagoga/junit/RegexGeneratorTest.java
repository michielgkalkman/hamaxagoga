package org.taHjaj.wo.hamaxagoga.junit;

import org.apache.xerces.impl.xpath.regex.RegexGenerator;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class RegexGeneratorTest {
    @Test
    public void test() {
        final RegexGenerator regexGenerator = new RegexGenerator(new Random(), "0|1");
        regexGenerator.getRegex();


    }

}
