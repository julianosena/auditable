package br.com.zup.itau.auditable.common.reflection;

/**
 * @author bartosz walacik
 */
public class ReflectionConstructorTestClass {

    private final String someString;

    public ReflectionConstructorTestClass(String someString) {
        this.someString = someString;
    }

    public String getSomeString() {
        return someString;
    }
}
