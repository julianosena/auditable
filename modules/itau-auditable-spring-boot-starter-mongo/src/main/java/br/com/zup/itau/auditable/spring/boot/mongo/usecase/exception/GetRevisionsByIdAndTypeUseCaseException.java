package br.com.zup.itau.auditable.spring.boot.mongo.usecase.exception;

public class GetRevisionsByIdAndTypeUseCaseException extends Exception {
    private static final long serialVersionUID = 8336661837044913079L;

    public GetRevisionsByIdAndTypeUseCaseException(String message) {
        super(message);
    }

    public GetRevisionsByIdAndTypeUseCaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
