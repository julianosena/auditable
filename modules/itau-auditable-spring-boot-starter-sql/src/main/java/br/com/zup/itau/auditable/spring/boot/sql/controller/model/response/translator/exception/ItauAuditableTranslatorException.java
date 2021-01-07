package br.com.zup.itau.auditable.spring.boot.sql.controller.model.response.translator.exception;

public class ItauAuditableTranslatorException extends Exception {

    public ItauAuditableTranslatorException() {
    }

    public ItauAuditableTranslatorException(String message) {
        super(message);
    }

    public ItauAuditableTranslatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItauAuditableTranslatorException(Throwable cause) {
        super(cause);
    }

    public ItauAuditableTranslatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}