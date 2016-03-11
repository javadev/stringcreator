package com.github.javadev.stringcreator;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class Form1Test {

    private final Form1 form1 = new Form1();

    @Test
    public void generateString1() {
        String result = form1.generateString("Маша {11} {243} и {243}.");
        assertThat(result, startsWith("Маша "));
        assertThat(result, not(containsString(" {11} ")));
        assertThat(result, not(containsString(" {243} ")));
        assertThat(result, containsString(" и "));
        assertThat(result, endsWith("."));
    }
    
    @Test
    public void generateString2() {
        String result = form1.generateString("Маша {111} {243} и {243}.");
        assertThat(result, startsWith("Маша {111}"));
        assertThat(result, containsString(" и "));
        assertThat(result, endsWith("."));
    }
    
    @Test
    public void generateString3() {
        String result = form1.generateString("Маша {11} {11} {11} {11} {243} и {243}.");
        assertThat(result, startsWith("Маша "));
        assertThat(result, containsString(" {11} "));
        assertThat(result, containsString(" и "));
        assertThat(result, endsWith("."));
    }
}
