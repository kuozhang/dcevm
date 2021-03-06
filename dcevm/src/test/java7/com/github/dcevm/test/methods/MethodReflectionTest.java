/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

package com.github.dcevm.test.methods;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.github.dcevm.test.util.HotSwapTestHelper.__toVersion__;
import static com.github.dcevm.test.util.HotSwapTestHelper.__version__;
import static org.junit.Assert.assertEquals;

/**
 * Testing correct reflection functionality after class redefinition.
 *
 * @author Thomas Wuerthinger
 */
public class MethodReflectionTest {

    @Before
    public void setUp() throws Exception {
        __toVersion__(0);
    }

    // Version 0
    public static class A {

        public int value() {
            return 1;
        }
    }

    public static class B extends A {

        @Override
        public int value() {
            return 2;
        }
    }

    public static class C extends A {

        @Override
        public int value() {
            return 3;
        }
    }

    // Version 1
    public static class A___1 {

        public int value() {
            return 1;
        }

        public int value2() {
            return 2;
        }
    }

    // Version 2
    public static class C___2 extends B {

        @Override
        public int value() {
            return super.value();
        }
    }

    @Test
    public void testMethodReflection() {

        assert __version__() == 0;

        A a = new A();
        B b = new B();
        C c = new C();

        assertEquals(1, a.value());
        assertEquals(2, b.value());
        assertEquals(3, c.value());

        assertContainsMethod(A.class, "value");
        assertDoesNotContainMethod(A.class, "value2");

        __toVersion__(1);

        assertEquals(1, a.value());
        assertEquals(2, b.value());
        assertEquals(3, c.value());

        assertContainsMethod(A.class, "value");
        assertContainsMethod(A.class, "value2");

        __toVersion__(0);

        assertEquals(1, a.value());
        assertEquals(2, b.value());
        assertEquals(3, c.value());

        assertContainsMethod(A.class, "value");
        assertDoesNotContainMethod(A.class, "value2");
    }

    private void assertContainsMethod(Class<?> c, String methodName) {
        boolean found = false;
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                found = true;
                break;
            }
        }

        Assert.assertTrue(found);
    }

    private void assertDoesNotContainMethod(Class<?> c, String methodName) {
        boolean found = false;
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                found = true;
                break;
            }
        }

        Assert.assertFalse(found);
    }
}
