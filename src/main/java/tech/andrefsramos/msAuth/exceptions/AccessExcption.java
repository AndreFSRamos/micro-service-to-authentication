package tech.andrefsramos.msAuth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.nio.file.AccessDeniedException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessExcption extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AccessExcption(String ex){super(ex);}
}