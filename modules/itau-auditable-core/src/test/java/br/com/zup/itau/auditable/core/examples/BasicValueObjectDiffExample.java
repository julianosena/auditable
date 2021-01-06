package br.com.zup.itau.auditable.core.examples;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange;
import br.com.zup.itau.auditable.core.examples.model.Address;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;

public class BasicValueObjectDiffExample {

  @Test
  public void shouldCompareTwoObjects() {

    //given
    ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

    Address address1 = new Address("New York","5th Avenue");
    Address address2 = new Address("New York","6th Avenue");

    //when
    Diff diff = itauAuditable.compare(address1, address2);

    //then
    //there should be one change of type {@link ValueChange}
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getAffectedGlobalId().value())
              .isEqualTo("br.com.zup.itau.auditable.core.examples.model.Address/");
    assertThat(change.getPropertyName()).isEqualTo("street");
    assertThat(change.getLeft()).isEqualTo("5th Avenue");
    assertThat(change.getRight()).isEqualTo("6th Avenue");

    System.out.println(diff);
  }
}
