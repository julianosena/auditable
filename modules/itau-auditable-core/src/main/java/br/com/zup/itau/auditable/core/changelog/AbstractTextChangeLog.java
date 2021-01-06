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
 * Convenient scaffolding class for text changeLog rendering
 *
 * @author bartosz walacik
 */
public abstract class AbstractTextChangeLog implements ChangeProcessor<String> {

    private final StringBuilder builder = new StringBuilder();


    @Override
    public void onCommit(CommitMetadata commitMetadata) {

    }

    @Override
    public void onAffectedObject(GlobalId globalId) {

    }

    @Override
    public void beforeChangeList() {

    }

    @Override
    public void afterChangeList() {

    }

    @Override
    public void beforeChange(Change change) {

    }

    @Override
    public void afterChange(Change change) {
    }

    @Override
    public void onPropertyChange(PropertyChange propertyChange) {

    }

    @Override
    public void onValueChange(ValueChange valueChange) {
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {

    }

    @Override
    public void onNewObject(NewObject newObject) {

    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {

    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {

    }

    @Override
    public void onSetChange(SetChange setChange) {

    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {

    }

    @Override
    public void onListChange(ListChange listChange) {

    }

    @Override
    public void onMapChange(MapChange mapChange) {

    }

    @Override
    public String result(){
        return builder.toString();
    }

    /**
     * null safe
     */
    protected void append(String text){
        if (text != null) {
            builder.append(text);
        }
    }

    /**
     * null safe
     */
    protected void append(Object text){
        if (text != null) {
            builder.append(text.toString());
        }
    }

    /**
     * null safe
     */
    protected void appendln(String text){
        if (text != null) {
            builder.append(text+"\n");
        }
    }

    /**
     * null safe
     */
    protected void appendln(Object text){
        if (text != null) {
            builder.append(text.toString()+"\n");
        }
    }
}
