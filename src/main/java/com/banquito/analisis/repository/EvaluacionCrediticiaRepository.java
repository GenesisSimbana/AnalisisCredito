package com.banquito.analisis.repository;

import com.banquito.analisis.model.EvaluacionCrediticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluacionCrediticiaRepository extends JpaRepository<EvaluacionCrediticia, Integer> {
    Optional<EvaluacionCrediticia> findByIdSolicitud(Integer idSolicitud);
} 