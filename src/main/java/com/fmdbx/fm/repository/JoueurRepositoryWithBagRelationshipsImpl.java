package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Joueur;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class JoueurRepositoryWithBagRelationshipsImpl implements JoueurRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Joueur> fetchBagRelationships(Optional<Joueur> joueur) {
        return joueur.map(this::fetchFavorises);
    }

    @Override
    public Page<Joueur> fetchBagRelationships(Page<Joueur> joueurs) {
        return new PageImpl<>(fetchBagRelationships(joueurs.getContent()), joueurs.getPageable(), joueurs.getTotalElements());
    }

    @Override
    public List<Joueur> fetchBagRelationships(List<Joueur> joueurs) {
        return Optional.of(joueurs).map(this::fetchFavorises).orElse(Collections.emptyList());
    }

    Joueur fetchFavorises(Joueur result) {
        return entityManager
            .createQuery("select joueur from Joueur joueur left join fetch joueur.favorises where joueur is :joueur", Joueur.class)
            .setParameter("joueur", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Joueur> fetchFavorises(List<Joueur> joueurs) {
        return entityManager
            .createQuery(
                "select distinct joueur from Joueur joueur left join fetch joueur.favorises where joueur in :joueurs",
                Joueur.class
            )
            .setParameter("joueurs", joueurs)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
