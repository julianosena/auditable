package br.com.zup.itau.auditable.core.examples;

import org.fest.assertions.Assertions;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.examples.model.Employee;
import br.com.zup.itau.auditable.repository.jql.QueryBuilder;
import br.com.zup.itau.auditable.shadow.Shadow;
import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ShadowStreamExample {
    @Test
    public void shouldFindShadowsAndStream() {
        // given
        ItauAuditable javers = ItauAuditableBuilder.javers().build();
        Employee frodo = new Employee("Frodo");
        frodo.addSubordinate(new Employee("Sam"));

        javers.commit("author", frodo);

        //when
        IntStream.range(1,10).forEach( i -> {
            frodo.setSalary(1_000 * i);
            javers.commit("author", frodo);
        });

        Stream<Shadow<Employee>> shadows = javers.findShadowsAndStream(
                QueryBuilder.byInstanceId("Frodo", Employee.class).build());

        //then
        Employee employeeV5 = shadows.filter(shadow -> shadow.getCommitId().getMajorId() == 5)
               .map(shadow -> shadow.get())
               .findFirst().orElse(null);

        Assertions.assertThat( employeeV5.getSalary() == 5_000 );
    }
}
