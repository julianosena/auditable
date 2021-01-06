package br.com.zup.itau.auditable.spring.boot.sql.gateway.exception;

public class ItauAuditableGatewayException extends Exception {

    public ItauAuditableGatewayException() {
    }

    public ItauAuditableGatewayException(String message) {
        super(message);
    }

    public ItauAuditableGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItauAuditableGatewayException(Throwable cause) {
        super(cause);
    }

    public ItauAuditableGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
