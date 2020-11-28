package test;

import org.junit.Test;

import javaa.Adapter;
import javaa.Targetable;

public class aTest {
    @Test
    public void test() {
        Targetable target = new Adapter();
        target.method1();
        target.method2();
    }

}
