package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Club;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ClubRepositoryWithBagRelationships {
    Optional<Club> fetchBagRelationships(Optional<Club> club);

    List<Club> fetchBagRelationships(List<Club> clubs);

    Page<Club> fetchBagRelationships(Page<Club> clubs);
}
