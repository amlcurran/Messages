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

public class StringPromise {

    private final String input;
    private final String output;

    public StringPromise(String input, Function<String, String> action) {
        this.input = input;
        this.output = action.act(this.input);
    }

    public StringPromise then(Function<String, String> action) {
        return new StringPromise(output, action);
    }

    public static StringPromise direct(String input) {
        return new StringPromise(input, new IdentityFunction());
    }

    interface Function<Input, Output> {
        Output act(Input input);
    }

    private static class IdentityFunction implements Function<String, String> {
        @Override
        public String act(String s) {
            return s;
        }
    }
}
