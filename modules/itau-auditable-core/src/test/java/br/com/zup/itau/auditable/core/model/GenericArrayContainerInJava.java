package br.com.zup.itau.auditable.core.model;

public class GenericArrayContainerInJava<T> {
    final private T[] array;

    public GenericArrayContainerInJava(T[] array) {
        this.array = array;
    }

    public T[] getArray() {
        return array;
    }
}
