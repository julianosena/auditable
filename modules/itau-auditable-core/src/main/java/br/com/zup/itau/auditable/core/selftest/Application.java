package br.com.zup.itau.auditable.core.selftest;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.Diff;

import static br.com.zup.itau.auditable.common.validation.Validate.conditionFulfilled;

/**
 * @author bartosz walacik
 */
public class Application {

    public static void main(String[] args) {
        System.out.println(".. Starting itau-auditable-core runtime environment self test ...");

        System.out.println("java.runtime.name:          " + System.getProperty("java.runtime.name"));
        System.out.println("java.vendor:                " + System.getProperty("java.vendor"));
        System.out.println("java.runtime.version:       " + System.getProperty("java.runtime.version"));
        System.out.println("java.version:               " + System.getProperty("java.version"));
        System.out.println("java.home:                  " + System.getProperty("java.home"));
        System.out.println("os.name & ver:              " + System.getProperty("os.name")+" v."+System.getProperty("os.version"));

        System.out.println(".. building JaVers instance ...");

        try {
            ItauAuditable javers = ItauAuditableBuilder.javers().build();

            SampleValueObject left = new SampleValueObject("red");
            SampleValueObject right = new SampleValueObject("green");

            System.out.println(".. calculating diff for two simple ValueObjects...");
            Diff diff = javers.compare(left, right);

            conditionFulfilled(diff.getChanges().size() == 1, "assertion failed");
            conditionFulfilled(diff.getPropertyChanges("color").size() == 1, "assertion failed");

            System.out.println(".. self test PASSED ..");
        }catch(Throwable e) {
            System.out.println(e);
            e.printStackTrace();
            System.out.println(".. self test FAILED! ..");
        }
    }

    private static class SampleValueObject {
        private String color;
        private SampleValueObject(String value) {
            this.color = value;
        }
    }
}