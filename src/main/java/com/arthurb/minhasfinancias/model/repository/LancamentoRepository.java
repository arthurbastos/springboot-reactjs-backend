package com.arthurb.minhasfinancias.model.repository;

import com.arthurb.minhasfinancias.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
