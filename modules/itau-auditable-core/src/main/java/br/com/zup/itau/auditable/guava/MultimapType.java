package br.com.zup.itau.auditable.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import br.com.zup.itau.auditable.common.collections.EnumerableFunction;
import br.com.zup.itau.auditable.common.collections.Maps;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.metamodel.object.OwnerContext;
import br.com.zup.itau.auditable.core.metamodel.type.KeyValueType;
import br.com.zup.itau.auditable.core.metamodel.type.MapEnumerationOwnerContext;
import br.com.zup.itau.auditable.core.metamodel.type.MapType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static br.com.zup.itau.auditable.guava.Multimaps.toNotNullMultimap;


/**
 * @author akrystian
 */
public class MultimapType extends KeyValueType {

    public static MultimapType getInstance(){
        return new MultimapType(Multimap.class);
    }

    public MultimapType(Type baseJavaType) {
        super(baseJavaType,2);
    }

    /**
     * @return immutable Multimap
     */
    @Override
    public Multimap map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentIsNotNull(mapFunction);

        Multimap sourceMultimap = toNotNullMultimap(sourceEnumerable);
        Multimap targetMultimap = ArrayListMultimap.create();
        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(owner, true);

        MapType.mapEntrySet(sourceMultimap.entries(), mapFunction, enumeratorContext, (k,v) -> targetMultimap.put(k,v));

        return Multimaps.unmodifiableMultimap(targetMultimap);
    }

    @Override
    public boolean isEmpty(Object container){
        return container == null || ((Multimap) container).isEmpty();
    }

    /**
     * Nulls keys are filtered
     */
    @Override
    public Object map(Object sourceEnumerable, Function mapFunction, boolean filterNulls) {
        Validate.argumentIsNotNull(mapFunction);

        Multimap sourceMultimap = toNotNullMultimap(sourceEnumerable);
        Multimap targetMultimap = ArrayListMultimap.create();

        MapType.mapEntrySet(sourceMultimap.entries(), mapFunction, (k,v) -> targetMultimap.put(k,v), filterNulls);

        return targetMultimap;
    }

    @Override
    protected Stream<Map.Entry> entries(Object source) {
        Map sourceMap = Maps.wrapNull(source);
        return sourceMap.entrySet().stream();
    }

    @Override
    public Object empty() {
        return ArrayListMultimap.create();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Multimap.class;
    }
}
