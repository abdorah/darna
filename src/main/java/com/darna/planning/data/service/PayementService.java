package com.darna.planning.data.service;

import com.darna.planning.data.entity.Payement;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PayementService {

    private final PayementRepository repository;

    @Autowired
    public PayementService(PayementRepository repository) {
        this.repository = repository;
    }

    public Optional<Payement> get(UUID id) {
        return repository.findById(id);
    }

    public Payement update(Payement entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Payement> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
