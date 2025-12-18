package com.cibertec.app.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ConfigExceptions {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handlerTypeMismatch(MethodArgumentTypeMismatchException ex){
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("fecha", LocalDateTime.now());
        mapa.put("mensaje", "El parámetro '" + ex.getName() + "' debe ser de tipo " + ex.getRequiredType().getSimpleName());
        mapa.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapa);
    }
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerValidationErrors(MethodArgumentNotValidException ex){
        Map<String, Object> errores = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("fecha", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("errores", errores); 
        respuesta.put("mensaje", "Existen errores de validación en los datos enviados");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<?> handlerNotFound(java.util.NoSuchElementException ex){
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("fecha", LocalDateTime.now());
        mapa.put("mensaje", ex.getMessage());
        mapa.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapa);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handlerIllegalArgument(IllegalArgumentException ex){
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("fecha", LocalDateTime.now());
        mapa.put("mensaje", ex.getMessage());
        mapa.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapa);
    }
    
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("fecha", java.time.LocalDateTime.now());
        mapa.put("status", HttpStatus.CONFLICT.value());
        mapa.put("mensaje", "Error de integridad: Esta operación no es posible porque los datos están siendo usados por otro registro o violan una restricción única.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mapa);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handlerIllegalState(IllegalStateException ex){
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("fecha", LocalDateTime.now());
        mapa.put("mensaje", ex.getMessage());
        mapa.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapa);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<?> handlerEntityNotFound(jakarta.persistence.EntityNotFoundException ex){
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("fecha", LocalDateTime.now());
        mapa.put("mensaje", ex.getMessage());
        mapa.put("status", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapa);
    }
}
