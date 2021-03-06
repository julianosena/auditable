package br.com.zup.itau.auditable.spring.boot.sql.gateway.database;

import br.com.zup.itau.auditable.spring.boot.sql.domain.GlobalId;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.GetRevisionsByIdAndTypeGateway;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.model.GlobalIdDatabase;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.repository.GlobalIdDatabaseRepository;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.database.translator.GlobalIdDatabaseToGlobalIdTranslator;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGatewayException;
import br.com.zup.itau.auditable.spring.boot.sql.gateway.exception.ItauAuditableGetGatewayException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetRevisionByIdAndTypeDatabaseGateway implements GetRevisionsByIdAndTypeGateway {

    private final GlobalIdDatabaseRepository repository;

    public GetRevisionByIdAndTypeDatabaseGateway(GlobalIdDatabaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<GlobalId> execute(String id, String type) throws ItauAuditableGatewayException {
        try {

            Optional<GlobalIdDatabase> optionalGlobalIdDatabase = this.repository.findAllByLocalIdAndTypeName(id, type);

            return optionalGlobalIdDatabase.map(GlobalIdDatabaseToGlobalIdTranslator ::translate);

        } catch (Exception e){
            throw new ItauAuditableGetGatewayException("Problem in get revisions by id and type process", e);
        }
    }

}
