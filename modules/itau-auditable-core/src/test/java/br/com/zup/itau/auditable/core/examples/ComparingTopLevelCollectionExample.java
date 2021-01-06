package br.com.zup.itau.auditable.core.examples;

import br.com.zup.itau.auditable.common.collections.Lists;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange;
import br.com.zup.itau.auditable.core.examples.model.Person;
import org.junit.Test;
import java.util.List;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author bartosz.walacik
 */
public class ComparingTopLevelCollectionExample {

  @Test
  public void shouldDeeplyCompareTwoTopLevelCollections() {
    //given
    ItauAuditable javers = ItauAuditableBuilder.javers().build();

    List<Person> oldList = Lists.asList( new Person("tommy", "Tommy Smart") );
    List<Person> newList = Lists.asList( new Person("tommy", "Tommy C. Smart") );

    //when
    Diff diff = javers.compareCollections(oldList, newList, Person.class);

    //then
    //there should be one change of type {@link ValueChange}
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getPropertyName()).isEqualTo("name");
    assertThat(change.getLeft()).isEqualTo("Tommy Smart");
    assertThat(change.getRight()).isEqualTo("Tommy C. Smart");

    System.out.println(diff);
  }
}
