package com.banquito.analisis.repository;

import com.banquito.analisis.model.ConsultaBuro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultaBuroRepository extends JpaRepository<ConsultaBuro, Integer> {
    Optional<ConsultaBuro> findByIdSolicitud(Integer idSolicitud);
} 