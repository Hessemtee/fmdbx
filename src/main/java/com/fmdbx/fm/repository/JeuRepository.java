package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Jeu;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Jeu entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JeuRepository extends JpaRepository<Jeu, Long> {}
