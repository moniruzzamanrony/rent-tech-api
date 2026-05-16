package com.itvillage.renttech.base.service;



import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.model.MagicBaseModel;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class MagicService<T, ID > {
    protected final JpaRepository<T, ID> repository;

    public MagicService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public T getById(String id) {
        return repository.findById((ID) id).orElseThrow(MagicException.NotFoundException::new);
    }

    public void deleteById(String id) {
        T entity = getById(id);
        ((MagicBaseModel) entity).setDelete(true);
        repository.save(entity);
    }

    public T save(T body) {
        return repository.save(body);
    }

    public T update(String id,T body) {
        T entity = getById(id);
        BeanUtils.copyProperties(body, entity,"id");
        return repository.save(entity);
    }
}
