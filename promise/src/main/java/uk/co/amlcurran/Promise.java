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

public final class Promise<From, To> {

    private final From input;
    private final Function<From, To> action;
    private To output;
    private Exception fallenException;

    private Promise(From input, Function<From, To> action, Exception fallenException) {
        this.input = input;
        this.action = action;
        this.fallenException = fallenException;
        execute();
    }

    private void execute() {
        executeAction(action, new Execution.Callback<To>() {

            @Override
            public void completed(To output) {
                Promise.this.output = output;
            }

            @Override
            public void failed(Exception exception) {
                Promise.this.fallenException = exception;
            }
        });
    }

    private void executeAction(Function<From, To> action, Execution.Callback<To> callback) {
        try {
            output = action.act(input);
            callback.completed(output);
        } catch (Exception exception) {
            callback.failed(exception);
        }
    }

    public <ToAgain> Promise<To, ToAgain> then(Function<To, ToAgain> action) {
        return new Promise<>(output, action, fallenException);
    }

    public void catchAll(CatchFunction catchFunction) {
        catchFunction.error(fallenException);
    }

    public static <From> Promise<From, From> resolve(From input) {
        return new Promise<>(input, new Function<From, From>() {
            @Override
            public From act(From o) {
                return o;
            }
        }, null);
    }

    private static class Execution<To> {
        private final To output;
        private final Exception caughtException;

        public Execution(To output, Exception caughtException) {
            this.output = output;
            this.caughtException = caughtException;
        }

        public interface Callback<To> {
            void completed(To output);

            void failed(Exception exception);
        }

    }
}
