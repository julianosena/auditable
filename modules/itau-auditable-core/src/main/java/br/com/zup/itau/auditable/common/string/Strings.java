package br.com.zup.itau.auditable.common.string;

public class Strings {
    public static boolean isNonEmpty(String val) {
        return val != null && !val.isEmpty();
    }
}
