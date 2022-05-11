package com.fmdbx.fm.repository;

import com.fmdbx.fm.domain.Club;
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
public class ClubRepositoryWithBagRelationshipsImpl implements ClubRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Club> fetchBagRelationships(Optional<Club> club) {
        return club.map(this::fetchJeuxes).map(this::fetchBookmarks);
    }

    @Override
    public Page<Club> fetchBagRelationships(Page<Club> clubs) {
        return new PageImpl<>(fetchBagRelationships(clubs.getContent()), clubs.getPageable(), clubs.getTotalElements());
    }

    @Override
    public List<Club> fetchBagRelationships(List<Club> clubs) {
        return Optional.of(clubs).map(this::fetchJeuxes).map(this::fetchBookmarks).orElse(Collections.emptyList());
    }

    Club fetchJeuxes(Club result) {
        return entityManager
            .createQuery("select club from Club club left join fetch club.jeuxes where club is :club", Club.class)
            .setParameter("club", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Club> fetchJeuxes(List<Club> clubs) {
        return entityManager
            .createQuery("select distinct club from Club club left join fetch club.jeuxes where club in :clubs", Club.class)
            .setParameter("clubs", clubs)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }

    Club fetchBookmarks(Club result) {
        return entityManager
            .createQuery("select club from Club club left join fetch club.bookmarks where club is :club", Club.class)
            .setParameter("club", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Club> fetchBookmarks(List<Club> clubs) {
        return entityManager
            .createQuery("select distinct club from Club club left join fetch club.bookmarks where club in :clubs", Club.class)
            .setParameter("clubs", clubs)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
