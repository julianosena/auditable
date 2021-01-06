package br.com.zup.itau.auditable.core.cases;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.metamodel.annotation.TypeName;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.repository.jql.JqlQuery;
import br.com.zup.itau.auditable.repository.jql.QueryBuilder;
import br.com.zup.itau.auditable.shadow.Shadow;
import org.junit.Test;

import javax.persistence.Id;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class CaseShadowWithInitializedPrimitiveBooleanField {

    @Test
    public void shouldFindShadowWithSameBooleanValue() {
        ItauAuditable javers = ItauAuditableBuilder.javers().build();

        Long personId = 1L;
        Person original = new Person();
        original.setPersonId( personId );
        original.setActive( false );

        javers.commit( "author", original );

        JqlQuery query = QueryBuilder.byInstanceId( personId, Person.class ).build();
        List<Shadow<Person>> shadows = javers.findShadows( query );
        List<CdoSnapshot> snapshots = javers.findSnapshots( query);

        Person shadow = shadows.get( 0 ).get();

        System.out.println("loaded snapshot " + snapshots.get(0));
        System.out.println("original " + original.isActive());
        System.out.println("shadow " + shadow.isActive());

        assertThat( shadow.isActive() ).isEqualTo( original.isActive() );
    }

    @TypeName( "Person" )
    static class Person {
        @Id
        private Long personId;

        private boolean isActive = true;

        public Long getPersonId() {
            return personId;
        }

        public void setPersonId( Long personId ) {
            this.personId = personId;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive( boolean active ) {
            isActive = active;
        }
    }
}
