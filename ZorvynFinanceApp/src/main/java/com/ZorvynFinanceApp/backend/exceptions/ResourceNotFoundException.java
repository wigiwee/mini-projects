package com.ZorvynFinanceApp.backend.exceptions;


public class ResourceNotFoundException extends RuntimeException {

    private Long id;

    public ResourceNotFoundException(String message, Long id) {
        super(message);
        this.id = id;
    }

    public ResourceNotFoundException() {
        super("Resource not found !");
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
