package br.com.zup.itau.auditable.usecase.exception;

public class ItauAuditableUseCaseException extends Exception {

    public ItauAuditableUseCaseException() {
    }

    public ItauAuditableUseCaseException(String message) {
        super(message);
    }

    public ItauAuditableUseCaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItauAuditableUseCaseException(Throwable cause) {
        super(cause);
    }

    public ItauAuditableUseCaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
