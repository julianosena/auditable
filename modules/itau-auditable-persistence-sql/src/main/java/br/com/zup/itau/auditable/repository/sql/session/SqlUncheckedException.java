package br.com.zup.itau.auditable.repository.sql.session;

import java.sql.SQLException;

public class SqlUncheckedException extends RuntimeException {
    public SqlUncheckedException(String message, SQLException cause) {
        super(message, cause);
    }
}
