package com.amlcurran.messages.bucket;

public class ArrayHelper<T> {
    private T[] array;

    public ArrayHelper(T[] array) {
        this.array = array;
    }

    public T atIndex(int index) {
        if (array.length > index) {
            return array[index];
        }
        return null;
    }
}