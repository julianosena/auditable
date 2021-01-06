package br.com.zup.itau.auditable.spring.auditable;

import org.aspectj.lang.JoinPoint;
import br.com.zup.itau.auditable.common.collections.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class AspectUtil {
    public static List<Object> collectArguments(JoinPoint jp){
        List<Object> result = new ArrayList<>();

        for (Object arg: jp.getArgs()) {
            if (arg instanceof Collection) {
                result.addAll((Collection)arg);
            } else {
                result.add(arg);
            }
        }
        return result;
    }

    public static Iterable<Object> collectReturnedObjects(Object returnedObject){
        if (returnedObject instanceof Iterable) {
            return (Iterable)returnedObject;
        }
        return Lists.immutableListOf(returnedObject);
    }
}
