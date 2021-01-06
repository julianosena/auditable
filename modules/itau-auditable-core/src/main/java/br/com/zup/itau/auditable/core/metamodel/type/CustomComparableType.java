package br.com.zup.itau.auditable.core.metamodel.type;

public interface CustomComparableType {

    boolean hasCustomValueComparator();

    String valueToString(Object value);
}
