package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Commentaire;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Commentaire entity.
 */
@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    default Optional<Commentaire> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Commentaire> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Commentaire> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct commentaire from Commentaire commentaire left join fetch commentaire.joueurConcerne left join fetch commentaire.clubConcerne left join fetch commentaire.abonne",
        countQuery = "select count(distinct commentaire) from Commentaire commentaire"
    )
    Page<Commentaire> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct commentaire from Commentaire commentaire left join fetch commentaire.joueurConcerne left join fetch commentaire.clubConcerne left join fetch commentaire.abonne"
    )
    List<Commentaire> findAllWithToOneRelationships();

    @Query(
        "select commentaire from Commentaire commentaire left join fetch commentaire.joueurConcerne left join fetch commentaire.clubConcerne left join fetch commentaire.abonne where commentaire.id =:id"
    )
    Optional<Commentaire> findOneWithToOneRelationships(@Param("id") Long id);
}
