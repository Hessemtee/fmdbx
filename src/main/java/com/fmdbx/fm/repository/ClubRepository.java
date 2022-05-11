package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Club;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Club entity.
 */
@Repository
public interface ClubRepository extends ClubRepositoryWithBagRelationships, JpaRepository<Club, Long> {
    default Optional<Club> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Club> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Club> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct club from Club club left join fetch club.championnat",
        countQuery = "select count(distinct club) from Club club"
    )
    Page<Club> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct club from Club club left join fetch club.championnat")
    List<Club> findAllWithToOneRelationships();

    @Query("select club from Club club left join fetch club.championnat where club.id =:id")
    Optional<Club> findOneWithToOneRelationships(@Param("id") Long id);
}
