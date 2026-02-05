package com.sbstvsllm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FreightCalculator_ESTest {

    @Test
    public void test0() {
        FreightCalculator freightCalculator0 = new FreightCalculator();
        double double0 = freightCalculator0.calculate(1.0, 1.0, "SUDESTE", false);
        assertEquals(10.0, double0, 0.01);
    }

    @Test
    public void test1() {
        FreightCalculator freightCalculator0 = new FreightCalculator();
        try {
            freightCalculator0.calculate(0.0, 100.0, "NORTE", true);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void test2() {
        FreightCalculator freightCalculator0 = new FreightCalculator();
        double double0 = freightCalculator0.calculate(10.0, 200.0, "SUL", true);
        assertEquals(51.4, double0, 0.01);
    }

    @Test
    public void test3() {
        FreightCalculator freightCalculator0 = new FreightCalculator();
        double double0 = freightCalculator0.calculate(25.0, 600.0, "OESTE", false);
        assertEquals(122.85, double0, 0.01);
    }

    @Test
    public void test4() {
        FreightCalculator freightCalculator0 = new FreightCalculator();
        double double0 = freightCalculator0.calculate(1.0, 1200.0, "SUDESTE", false);
        assertEquals(99.9, double0, 0.01);
    }

    @Test
    public void test5() {
        FreightCalculator freightCalculator0 = new FreightCalculator();
        double double0 = freightCalculator0.calculate(15.0, 50.0, "NORTE", true);
        assertEquals(47.3, double0, 0.01);
    }
}
