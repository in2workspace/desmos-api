package es.in2.desmos.domain.exceptions;

public class RequestErrorException extends RuntimeException {

    public RequestErrorException(String message) {
        super(message);
    }

}
