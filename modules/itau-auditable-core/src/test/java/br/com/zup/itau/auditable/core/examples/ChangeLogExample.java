package br.com.zup.itau.auditable.core.examples;

import br.com.zup.itau.auditable.core.Changes;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.changelog.SimpleTextChangeLog;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.examples.model.Employee;
import br.com.zup.itau.auditable.repository.jql.QueryBuilder;
import org.junit.Test;
import java.util.List;

public class ChangeLogExample {

    @Test
    public void changesPrintingExampleWithGrouping() {
        // given
        ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

        Employee sam = new Employee("Sam", 1_000);
        Employee frodo = new Employee("Frodo", 10_000);
        itauAuditable.commit("author", frodo);

        frodo.addSubordinate(sam);
        frodo.setSalary(11_000);
        sam.setSalary(2_000);
        itauAuditable.commit("author", frodo);

        // when
        Changes changes = itauAuditable.findChanges(QueryBuilder.byClass(Employee.class)
                .withNewObjectChanges().build());

        //then
        System.out.println("Printing the flat list of Changes :");
        changes.forEach(change -> System.out.println("- " + change));

        //then
        System.out.println("Changes prettyPrint :");
        System.out.println(changes.prettyPrint());

        System.out.println("Printing Changes with grouping by commits and by objects :");
        changes.groupByCommit().forEach(byCommit -> {
            System.out.println("commit " + byCommit.getCommit().getId());
            byCommit.groupByObject().forEach(byObject -> {
                System.out.println("  changes on " + byObject.getGlobalId().value() + " : ");
                byObject.get().forEach(change -> {
                    System.out.println("  - " + change);
                });
            });
        });
    }

    @Test
    public void shouldPrintTextChangeLog() {
        // given:
        ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();
        Employee bob = new Employee("Bob", 9_000, "ScrumMaster");
        itauAuditable.commit("hr.manager", bob);

        // do some changes and commit
        bob.setPosition("Developer");
        bob.setSalary(11_000);
        itauAuditable.commit("hr.director", bob);

        bob.addSubordinates(new Employee("Trainee One"), new Employee("Trainee Two"));
        itauAuditable.commit("hr.manager", bob);

        // when:
        List<Change> changes = itauAuditable.findChanges(
            QueryBuilder.byInstanceId("Bob", Employee.class).build());
        String changeLog = itauAuditable.processChangeList(changes, new SimpleTextChangeLog());

        // then:
        System.out.println(changeLog);
    }
}
