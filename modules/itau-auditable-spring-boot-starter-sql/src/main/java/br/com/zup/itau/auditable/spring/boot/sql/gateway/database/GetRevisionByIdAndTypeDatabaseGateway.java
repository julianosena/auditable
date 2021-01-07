package br.com.zup.itau.auditable.spring.boot.sql.gateway.database;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.GetRevisionsByIdAndTypeGateway;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.GlobalIdDatabase;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.repository.GlobalIdDatabaseRepository;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator.GlobalIdDatabaseToGlobalIdTranslator;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGatewayException;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGetGatewayException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetRevisionByIdAndTypeDatabaseGateway implements GetRevisionsByIdAndTypeGateway {

    private final GlobalIdDatabaseRepository repository;

    public GetRevisionByIdAndTypeDatabaseGateway(GlobalIdDatabaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GlobalId> execute(String id, String type) throws ItauAuditableGatewayException {
        try {

            List<GlobalIdDatabase> globalIdDatabases = this.repository.findAllByLocalIdAndTypeName(id, type);
            return globalIdDatabases.stream().map(GlobalIdDatabaseToGlobalIdTranslator::translate).collect(Collectors.toList());

        } catch (Exception e){
            throw new ItauAuditableGetGatewayException("Problem in get revisions by id and type process", e);
        }
    }

}
