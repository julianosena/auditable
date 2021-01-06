package br.com.zup.itau.auditable.core.metamodel.type;

import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.core.ItauAuditableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Generic type created when a class definition for named type is missing.
 * <br/><br/>
 * Should be avoided because Snapshots with UnknownType can't be properly deserialized,
 * see {@link ItauAuditableBuilder#withPackagesToScan(String)}.
 */
public class UnknownType extends ManagedType {
    public static final Logger logger = LoggerFactory.getLogger(UnknownType.class);

    public UnknownType(String typeName) {
        super(ManagedClass.unknown(), Optional.of(typeName));
        logger.warn("Missing class definition with @TypeName '"+typeName+"', \n"+
                "cant't properly deserialize its Snapshots from ItauAuditableRepository.\n"+
                "To fix this issue provide the fully-qualified package name of the class "+
                "named '"+typeName+"' in the packagesToScan property."
        );
    }

    @Override
    ManagedType spawn(ManagedClass managedClass, Optional<String> typeName) {
        throw new ItauAuditableException(ItauAuditableExceptionCode.NOT_IMPLEMENTED);
    }

}
