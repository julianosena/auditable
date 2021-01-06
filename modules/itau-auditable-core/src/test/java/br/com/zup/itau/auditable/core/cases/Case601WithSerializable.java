package br.com.zup.itau.auditable.core.cases;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.metamodel.annotation.Id;
import br.com.zup.itau.auditable.core.metamodel.type.EntityType;
import br.com.zup.itau.auditable.core.metamodel.type.ItauAuditableType;
import br.com.zup.itau.auditable.core.metamodel.type.TokenType;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * see https://github.com/javers/javers/issues/601
 * @author Philippe Boyd
 */
public class Case601WithSerializable {

    private abstract static class MongoBaseModel<ID extends Serializable & Comparable<ID>> implements Comparable<MongoBaseModel<ID>> {

        protected int version;

        @Id
        protected ID id;

        public int compareTo(MongoBaseModel<ID> o) {
            return id.compareTo(o.id);
        }
    }

    private abstract static class AbstractPermission extends MongoBaseModel<String> implements Serializable {
        protected String name;
    }

    private static class Permission extends AbstractPermission {
        private Set<String> inclusions;
    }

    private static class Role extends AbstractPermission {
        private Set<Permission> permissions;
    }

    @Test
    public void shouldResolveTypeTokensInIdProperty(){
        //given
        final ItauAuditable javers = ItauAuditableBuilder.javers().build();

        //when
        final ItauAuditableType roleType = javers.getTypeMapping(Role.class);
        final ItauAuditableType permissionType = javers.getTypeMapping(Permission.class);
        final ItauAuditableType mongoBaseModelType = javers.getTypeMapping(MongoBaseModel.class);

        //then
        assertThat(roleType).isInstanceOf(EntityType.class);
        assertThat(permissionType).isInstanceOf(EntityType.class);
        assertThat(mongoBaseModelType).isInstanceOf(EntityType.class);
        assertThat(((EntityType) roleType).getIdProperty().getType().getBaseJavaType()).isEqualTo(String.class);
        assertThat(((EntityType) permissionType).getIdProperty().getType().getBaseJavaType()).isEqualTo(String.class);
        assertThat(((ItauAuditableType) ((EntityType) mongoBaseModelType).getIdProperty().getType())).isInstanceOf(TokenType.class);
    }
}
