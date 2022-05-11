package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Joueur;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Joueur entity.
 */
@Repository
public interface JoueurRepository extends JoueurRepositoryWithBagRelationships, JpaRepository<Joueur, Long> {
    default Optional<Joueur> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Joueur> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Joueur> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct joueur from Joueur joueur left join fetch joueur.club",
        countQuery = "select count(distinct joueur) from Joueur joueur"
    )
    Page<Joueur> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct joueur from Joueur joueur left join fetch joueur.club")
    List<Joueur> findAllWithToOneRelationships();

    @Query("select joueur from Joueur joueur left join fetch joueur.club where joueur.id =:id")
    Optional<Joueur> findOneWithToOneRelationships(@Param("id") Long id);
}
