package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Pays;
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
public class PaysRepositoryWithBagRelationshipsImpl implements PaysRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Pays> fetchBagRelationships(Optional<Pays> pays) {
        return pays.map(this::fetchJoueurs);
    }

    @Override
    public Page<Pays> fetchBagRelationships(Page<Pays> pays) {
        return new PageImpl<>(fetchBagRelationships(pays.getContent()), pays.getPageable(), pays.getTotalElements());
    }

    @Override
    public List<Pays> fetchBagRelationships(List<Pays> pays) {
        return Optional.of(pays).map(this::fetchJoueurs).orElse(Collections.emptyList());
    }

    Pays fetchJoueurs(Pays result) {
        return entityManager
            .createQuery("select pays from Pays pays left join fetch pays.joueurs where pays is :pays", Pays.class)
            .setParameter("pays", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Pays> fetchJoueurs(List<Pays> pays) {
        return entityManager
            .createQuery("select distinct pays from Pays pays left join fetch pays.joueurs where pays in :pays", Pays.class)
            .setParameter("pays", pays)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
