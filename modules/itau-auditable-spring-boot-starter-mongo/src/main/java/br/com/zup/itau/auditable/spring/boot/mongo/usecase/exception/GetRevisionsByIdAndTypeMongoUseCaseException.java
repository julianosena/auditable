package br.com.zup.itau.auditable.spring.boot.mongo.usecase.exception;

public class GetRevisionsByIdAndTypeMongoUseCaseException extends Exception {
    private static final long serialVersionUID = 8336661837044913079L;

    public GetRevisionsByIdAndTypeMongoUseCaseException(String message) {
        super(message);
    }

    public GetRevisionsByIdAndTypeMongoUseCaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
