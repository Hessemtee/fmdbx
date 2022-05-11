package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Pays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PaysRepositoryWithBagRelationships {
    Optional<Pays> fetchBagRelationships(Optional<Pays> pays);

    List<Pays> fetchBagRelationships(List<Pays> pays);

    Page<Pays> fetchBagRelationships(Page<Pays> pays);
}
