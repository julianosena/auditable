package br.com.zup.itau.auditable.core.json.typeadapter.change;

import br.com.zup.itau.auditable.core.diff.changetype.PropertyChangeMetadata;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerElementChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.SetChange;
import br.com.zup.itau.auditable.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
class SetChangeTypeAdapter extends ContainerChangeTypeAdapter<SetChange> {

    public SetChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        return new SetChange(metadata, changes);
    }

    @Override
    public Class getValueType() {
        return SetChange.class;
    }
}