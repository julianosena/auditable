package br.com.zup.itau.auditable.core.cases;

import br.com.zup.itau.auditable.core.ItauAuditable;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import br.com.zup.itau.auditable.core.diff.Diff;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListWithChildClassesTest {
    private ItauAuditable itauAuditable = ItauAuditableBuilder
            .itauAuditable()
            .build();

    @Test
    public void testIssue(){
        Container container1 = new Container(Collections.singletonList(new StringField("StringField","String value")));
        Container container2 = new Container(Collections.singletonList(new ListField("ArrayField", Arrays.asList("V1", "V2"))));

        Diff diff = itauAuditable.compare(container1, container2);
        assertThat(diff).isNotNull();

        Diff diff2 = itauAuditable.compare(container2, container1);
        assertThat(diff2).isNotNull();
    }


    class Container{
        private List<ContainerField> fields;

        public Container(List<ContainerField> fields) {
            this.fields = fields;
        }

        public List<ContainerField> getFields() {
            return fields;
        }
    }

    abstract class ContainerField{
        private String name;

        public ContainerField(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    class StringField extends ContainerField{
        private String value;

        public StringField(String name, String value) {
            super(name);
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    class ListField extends ContainerField{
        private List<String> value;

        public ListField(String name, List<String> value) {
            super(name);
            this.value = value;
        }

        public List<String> getValue() {
            return value;
        }

    }
}
