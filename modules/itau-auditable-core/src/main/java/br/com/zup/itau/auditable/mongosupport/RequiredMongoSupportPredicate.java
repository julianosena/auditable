package br.com.zup.itau.auditable.mongosupport;

import br.com.zup.itau.auditable.repository.api.ItauAuditableRepository;
import java.util.function.Predicate;

public class RequiredMongoSupportPredicate implements Predicate<ItauAuditableRepository> {

    private static final String JAVERS_MONGO_REPOSITORY_CLASS_NAME = "br.com.zup.itau.auditable.repository.mongo.MongoRepository";

    @Override
    public boolean test(ItauAuditableRepository repository) {
        return repository != null && repository.getClass().getName().equals(JAVERS_MONGO_REPOSITORY_CLASS_NAME);
    }
}
