package com.itvillage.renttech.base.controller;



import com.itvillage.renttech.base.expection.MagicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class MagicController<S, E> {

    private final S service;

    public MagicController(S service) {
        this.service = service;
    }

    @GetMapping("/magic")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getAll() {
        try {
            Method method = service.getClass().getMethod("getAll");
            Object result = method.invoke(service);
            return ResponseEntity.status(HttpStatus.OK).body((List<Object>) result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}/magic")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            Method method = service.getClass().getMethod("getById",String.class);
            Object result = method.invoke(service,id);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new MagicException.NotFoundException();
        }
    }

    @PostMapping("/magic")
    public ResponseEntity<?> create(@RequestBody E entity) {
        try {
            Method method = service.getClass().getMethod("save",Object.class);
            Object result = method.invoke(service,entity);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
            throw new MagicException.NotFoundException();
        }
    }

    @PutMapping("/{id}/magic")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody E entity) {
        try {
            Method method = service.getClass().getMethod("update",String.class, Object.class);
            Object result = method.invoke(service,id,entity);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Megic error", e.getMessage());
            throw new MagicException.NotFoundException();
        }
    }

    @DeleteMapping("/{id}/magic")
    public void delete(@PathVariable String id) {
        try {
            Method method = service.getClass().getMethod("deleteById",String.class);
             method.invoke(service,id);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new MagicException.NotFoundException();
        }
    }
}
