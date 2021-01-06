package br.com.zup.itau.auditable.spring.boot.sql.gateway.exception;

public class ItauAuditableGetGatewayException extends ItauAuditableGatewayException {

    public ItauAuditableGetGatewayException() {
    }

    public ItauAuditableGetGatewayException(String message) {
        super(message);
    }

    public ItauAuditableGetGatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItauAuditableGetGatewayException(Throwable cause) {
        super(cause);
    }

    public ItauAuditableGetGatewayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
