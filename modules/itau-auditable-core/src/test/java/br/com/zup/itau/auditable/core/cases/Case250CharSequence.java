package br.com.zup.itau.auditable.core.cases;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.Diff;
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * see https://github.com/itauAuditable/itauAuditable/issues/250
 * @author Rick Schertz
 */
public class Case250CharSequence {
    private static class AvroAddress {
        private final CharSequence city;
        private final CharSequence street;

        public AvroAddress(CharSequence city, CharSequence street) {
            this.city = city;
            this.street = street;
        }

        public CharSequence getCity() { return city; }
        public CharSequence getStreet() { return street; }
    }


    @Test
    public void shouldCompareTwoObjectsWithCharSequencePropertiesOfString() {
        //given
        ItauAuditable itauAuditable = ItauAuditableBuilder.itauAuditable().build();

        AvroAddress oldVersion = new AvroAddress("New York", "First Avenue");
        AvroAddress currentVersion = new AvroAddress("New York", "Second Avenue");

        //when
        Diff diff = itauAuditable.compare(oldVersion, currentVersion);
        System.out.println(diff);

        //then
        ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

        assertThat(diff.getChanges()).hasSize(1);
        assertThat(change.getPropertyName()).isEqualTo("street");
        assertThat(change.getLeft()).isEqualTo(oldVersion.getStreet());
        assertThat(change.getRight()).isEqualTo(currentVersion.getStreet());
    }
}
