package br.com.zup.itau.auditable.core.examples;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.changetype.*;
import br.com.zup.itau.auditable.core.examples.model.Employee;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;

public class EmployeeHierarchiesDiffExample {

  /** {@link NewObject} example */
  @Test
  public void shouldDetectHired() {
    //given
    ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

    Employee oldBoss = new Employee("Big Boss")
        .addSubordinates(
            new Employee("Great Developer"));

    Employee newBoss = new Employee("Big Boss")
        .addSubordinates(
            new Employee("Great Developer"),
            new Employee("Hired One"),
            new Employee("Hired Second"));

    //when
    Diff diff = itauAuditable.compare(oldBoss, newBoss);

    //then
    assertThat(diff.getObjectsByChangeType(NewObject.class))
        .hasSize(2)
        .containsOnly(new Employee("Hired One"),
                      new Employee("Hired Second"));

    System.out.println(diff);
  }

  /** {@link ObjectRemoved} example */
  @Test
  public void shouldDetectFired() {
    //given
    ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

    Employee oldBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Great Developer"),
                    new Employee("Team Lead").addSubordinates(
                            new Employee("Another Dev"),
                            new Employee("To Be Fired")
                    ));

    Employee newBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Great Developer"),
                    new Employee("Team Lead").addSubordinates(
                            new Employee("Another Dev")
                    ));

    //when
    Diff diff = itauAuditable.compare(oldBoss, newBoss);

    //then
    assertThat(diff.getChangesByType(ObjectRemoved.class)).hasSize(1);

    System.out.println(diff);
  }

  /** {@link ValueChange} example */
  @Test
  public void shouldDetectSalaryChange(){
    //given
    ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

    Employee oldBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Noisy Manager"),
                    new Employee("Great Developer", 10000));

    Employee newBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Noisy Manager"),
                    new Employee("Great Developer", 20000));

    //when
    Diff diff = itauAuditable.compare(oldBoss, newBoss);

    //then
    ValueChange change =  diff.getChangesByType(ValueChange.class).get(0);

    assertThat(change.getAffectedLocalId()).isEqualTo("Great Developer");
    assertThat(change.getPropertyName()).isEqualTo("salary");
    assertThat(change.getLeft()).isEqualTo(10000);
    assertThat(change.getRight()).isEqualTo(20000);

    System.out.println(diff);
  }

  /** {@link ReferenceChange} example */
  @Test
  public void shouldDetectBossChange() {
    //given
    ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

    Employee oldBoss = new Employee("Big Boss")
        .addSubordinates(
             new Employee("Manager One")
                 .addSubordinate(new Employee("Great Developer")),
             new Employee("Manager Second"));

    Employee newBoss = new Employee("Big Boss")
        .addSubordinates(
             new Employee("Manager One"),
             new Employee("Manager Second")
                 .addSubordinate(new Employee("Great Developer")));

    //when
    Diff diff = itauAuditable.compare(oldBoss, newBoss);

    //then
    ReferenceChange change = diff.getChangesByType(ReferenceChange.class).get(0);

    assertThat(change.getAffectedLocalId()).isEqualTo("Great Developer");
    assertThat(change.getLeft().value()).endsWith("Manager One");
    assertThat(change.getRight().value()).endsWith("Manager Second");

    System.out.println(diff);
  }
}
