package com.darna.planning.data.service;

import com.darna.planning.data.entity.Payement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayementRepository extends JpaRepository<Payement, UUID> {

}