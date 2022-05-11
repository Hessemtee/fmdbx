package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Version;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Version entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {}
