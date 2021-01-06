package br.com.zup.itau.auditable.core.json.typeadapter.change;

import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeMetadata;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
class ListChangeTypeAdapter extends ContainerChangeTypeAdapter<ListChange> {

    public ListChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        return new ListChange(metadata, changes);
    }

    @Override
    public Class getValueType() {
        return ListChange.class;
    }
}
