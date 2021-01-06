package br.com.zup.itau.auditable.common.exception;

import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.RUNTIME_EXCEPTION;

/**
 *  @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
@SuppressWarnings("serial")
public class ItauAuditableException extends RuntimeException {
    public static final String BOOTSTRAP_ERROR = "JaVers bootstrap error - ";

    private final ItauAuditableExceptionCode code;

    public ItauAuditableException(Throwable throwable) {
        super(String.format(RUNTIME_EXCEPTION.getMessage(),
                "Cause: " + throwable.getClass().getName() + " - " + throwable.getMessage()), throwable);
        this.code = RUNTIME_EXCEPTION;
    }

    public ItauAuditableException(ItauAuditableExceptionCode code, Object... arguments) {
        super(code.name() + ": " + String.format(code.getMessage(), arguments));
        this.code = code;
    }

    public ItauAuditableExceptionCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+" "+ getMessage();
    }
}
