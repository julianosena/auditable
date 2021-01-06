package br.com.zup.itau.auditable.core.changelog;

import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import br.com.zup.itau.auditable.core.diff.Change;
import br.com.zup.itau.auditable.core.diff.changetype.*;
import br.com.zup.itau.auditable.core.diff.changetype.container.ArrayChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ContainerChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.ListChange;
import br.com.zup.itau.auditable.core.diff.changetype.container.SetChange;
import br.com.zup.itau.auditable.core.diff.changetype.map.MapChange;
import br.com.zup.itau.auditable.core.metamodel.object.GlobalId;

/**
 * For convenient change processing, e.g. rendering a change log
 *
 * @author bartosz walacik
 */
public interface ChangeProcessor<T> {

    public void onCommit(CommitMetadata commitMetadata);

    void onAffectedObject(GlobalId globalId);

    void beforeChangeList();

    void afterChangeList();

    /**
     * called before each change
     */
    void beforeChange(Change change);

    /**
     * called after each change
     */
    void afterChange(Change change);

    /**
     * called on {@link ValueChange}, {@link ReferenceChange},
     * {@link ContainerChange} and {@link MapChange}
     */
    void onPropertyChange(PropertyChange propertyChange);

    void onValueChange(ValueChange valueChange);

    void onReferenceChange(ReferenceChange referenceChange);

    void onNewObject(NewObject newObject);

    void onObjectRemoved(ObjectRemoved objectRemoved);

    /**
     * called on {@link ListChange}, {@link SetChange} and {@link ArrayChange}
     */
    void onContainerChange(ContainerChange containerChange);

    void onSetChange(SetChange setChange);

    void onArrayChange(ArrayChange arrayChange);

    void onListChange(ListChange listChange);

    void onMapChange(MapChange mapChange);

    /**
     * should return processing result, for example a change log
     */
    T result();
}
