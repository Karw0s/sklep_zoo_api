package pl.michalkarwowski.api.exceptions;

public class InvoiceExistsException extends Exception {

    public InvoiceExistsException() {
    }

    public InvoiceExistsException(String message) {
        super(message);
    }
}
