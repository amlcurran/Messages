package com.amlcurran.messages;

public class ProviderHelper<T> {

    private Class<T> requiredClass;

    public ProviderHelper(Class<T> requiredClass) {
        this.requiredClass = requiredClass;
    }

    public T get(Object implementer) {
        try {
            return requiredClass.cast(implementer);
        } catch (ClassCastException cce) {
            String detailMessage = String.format("%1$s doesn't implement the required interface %2$s",
                    implementer.getClass().getSimpleName(), requiredClass.getSimpleName());
            throw new ClassCastException(detailMessage);
        }
    }

}
