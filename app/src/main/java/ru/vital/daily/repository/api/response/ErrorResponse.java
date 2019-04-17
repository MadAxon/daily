package ru.vital.daily.repository.api.response;

public class ErrorResponse extends Throwable {

    private String message;

    private int statusCode;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        super(message);
        this.message = message;
    }

    public ErrorResponse(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
