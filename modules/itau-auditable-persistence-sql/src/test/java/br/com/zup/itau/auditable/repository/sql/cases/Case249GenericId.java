package br.com.zup.itau.auditable.repository.sql.cases;

import org.fest.assertions.Assertions;
import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.metamodel.annotation.Entity;
import br.com.zup.itau.auditable.core.metamodel.annotation.Id;
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot;
import br.com.zup.itau.auditable.repository.sql.H2RepositoryFactory;
import org.junit.Test;

import java.io.Serializable;

/**
 * see https://github.com/javers/javers/issues/249
 * @author bartosz.walacik
 */
public class Case249GenericId {

    public static abstract class AbstractEntity<T extends Serializable> {

        @Id
        protected T id;
        protected T value;

        public AbstractEntity(T id, T value) {
            this.id = id;
            this.value = value;
        }
    }

    @Entity
    public class Account extends AbstractEntity<String> {
        public Account(String id, String value) {
            super(id, value);
        }
    }

    @Test
    public void shouldCommitEntityWithSerializableId() {
        //given
        ItauAuditable javers = ItauAuditableBuilder.javers().
                registerItauAuditableRepository(H2RepositoryFactory.create()).build();

        //when
        Account acc = new Account("1","2");
        javers.commit("author", acc);

        //then
        CdoSnapshot snapshot = javers.getLatestSnapshot("1", Account.class).get();
        Assertions.assertThat(snapshot.getPropertyValue("id")).isEqualTo("1");
        Assertions.assertThat(snapshot.getPropertyValue("value")).isEqualTo("2");
    }
}
