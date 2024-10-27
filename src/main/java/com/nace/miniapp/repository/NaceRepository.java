package com.nace.miniapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nace.miniapp.model.Nace;

import java.util.Optional;

@Repository
public interface NaceRepository extends JpaRepository<Nace, Long> {

    Optional<Nace> findByOrder(long order);
}
