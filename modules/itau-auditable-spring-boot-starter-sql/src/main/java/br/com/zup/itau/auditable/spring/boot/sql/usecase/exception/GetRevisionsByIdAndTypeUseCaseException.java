package br.com.zup.itau.auditable.spring.boot.sql.usecase.exception;

import br.com.zup.itau.auditable.usecase.exception.ItauAuditableUseCaseException;

public class GetRevisionsByIdAndTypeUseCaseException extends ItauAuditableUseCaseException {

    public GetRevisionsByIdAndTypeUseCaseException() {
    }

    public GetRevisionsByIdAndTypeUseCaseException(String message) {
        super(message);
    }

    public GetRevisionsByIdAndTypeUseCaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetRevisionsByIdAndTypeUseCaseException(Throwable cause) {
        super(cause);
    }

    public GetRevisionsByIdAndTypeUseCaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
