package com.amlcurran.messages.notifications;

public class ArrayHelper<T> {
    private T[] array;

    public ArrayHelper(T[] array) {
        this.array = array;
    }

    T atIndex(int index) {
        if (array.length > index) {
            return array[index];
        }
        return null;
    }
}