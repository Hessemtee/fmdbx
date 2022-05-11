package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Abonne;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Abonne entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AbonneRepository extends JpaRepository<Abonne, Long> {}
