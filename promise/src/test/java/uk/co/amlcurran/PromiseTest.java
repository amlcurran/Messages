/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.amlcurran;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class PromiseTest {

    @Test
    public void testStringsWork() {
        Promise.resolve("hello")
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        return s.substring(0, 3);
                    }
                })
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        return s + "woo";
                    }
                })
                .then(new Function<String, Void>() {
                    @Override
                    public Void act(String s) {
                        assertThat(s).isEqualTo("helwoo");
                        return null;
                    }
                });
    }


    @Test
    public void testCrossingOverClasses() {
        Promise.resolve("hello")
                .then(new Function<String, Integer>() {
                    @Override
                    public Integer act(String s) {
                        return s.hashCode();
                    }
                })
                .then(new Function<Integer, String>() {
                    @Override
                    public String act(Integer s) {
                        return String.valueOf(s);
                    }
                })
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        assertThat(s).isEqualTo(String.valueOf("hello".hashCode()));
                        return s;
                    }
                });
    }

    @Test
    public void testCatchingOneExceptionsWorks() {
        Promise.resolve("hello")
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        return s.substring(0, 3);
                    }
                })
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        throw new IllegalStateException();
                    }
                })
                .catchAll(new CatchFunction() {
                    @Override
                    public void error(Exception exception) {
                        assertThat(exception).isInstanceOf(IllegalStateException.class);
                    }
                });
    }

    @Test
    public void testCatchingMultipleExceptionsCatchesTheLastOne() {
        Promise.resolve("hello")
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        throw new NullPointerException();
                    }
                })
                .then(new Function<String, String>() {
                    @Override
                    public String act(String s) {
                        throw new IllegalStateException();
                    }
                })
                .catchAll(new CatchFunction() {
                    @Override
                    public void error(Exception exception) {
                        assertThat(exception).isInstanceOf(IllegalStateException.class);
                    }
                });
    }

}