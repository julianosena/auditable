package br.com.zup.itau.auditable.spring.boot.sql;

import org.hibernate.dialect.*;
import br.com.zup.itau.auditable.common.exception.ItauAuditableException;
import br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode;
import br.com.zup.itau.auditable.repository.sql.DialectName;

public class DialectMapper {

    public DialectName map(Dialect hibernateDialect) {

        if (hibernateDialect instanceof SQLServerDialect) {
            return DialectName.MSSQL;
        }
        if (hibernateDialect instanceof H2Dialect){
            return DialectName.H2;
        }
        if (hibernateDialect instanceof Oracle8iDialect){
            return DialectName.ORACLE;
        }
        if (hibernateDialect instanceof PostgreSQL81Dialect){
            return DialectName.POSTGRES;
        }
        if (hibernateDialect instanceof MySQLDialect){
            return DialectName.MYSQL;
        }
        throw new ItauAuditableException(ItauAuditableExceptionCode.UNSUPPORTED_SQL_DIALECT, hibernateDialect.getClass().getSimpleName());
    }
}
