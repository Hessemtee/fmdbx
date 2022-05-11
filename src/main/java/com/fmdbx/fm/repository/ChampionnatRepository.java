package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Championnat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Championnat entity.
 */
@Repository
public interface ChampionnatRepository extends JpaRepository<Championnat, Long> {
    default Optional<Championnat> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Championnat> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Championnat> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct championnat from Championnat championnat left join fetch championnat.pays",
        countQuery = "select count(distinct championnat) from Championnat championnat"
    )
    Page<Championnat> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct championnat from Championnat championnat left join fetch championnat.pays")
    List<Championnat> findAllWithToOneRelationships();

    @Query("select championnat from Championnat championnat left join fetch championnat.pays where championnat.id =:id")
    Optional<Championnat> findOneWithToOneRelationships(@Param("id") Long id);
}
