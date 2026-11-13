package com.previ.previ_backend.exceptions;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException() {

        super("Email já cadastrado");
    }
}
