package com.fmdbx.fm.web.rest;

import com.fmdbx.fm.domain.Jeu;
import com.fmdbx.fm.repository.JeuRepository;
import com.fmdbx.fm.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.fmdbx.fm.domain.Jeu}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class JeuResource {

    private final Logger log = LoggerFactory.getLogger(JeuResource.class);

    private static final String ENTITY_NAME = "jeu";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JeuRepository jeuRepository;

    public JeuResource(JeuRepository jeuRepository) {
        this.jeuRepository = jeuRepository;
    }

    /**
     * {@code POST  /jeus} : Create a new jeu.
     *
     * @param jeu the jeu to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jeu, or with status {@code 400 (Bad Request)} if the jeu has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/jeus")
    public ResponseEntity<Jeu> createJeu(@Valid @RequestBody Jeu jeu) throws URISyntaxException {
        log.debug("REST request to save Jeu : {}", jeu);
        if (jeu.getId() != null) {
            throw new BadRequestAlertException("A new jeu cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Jeu result = jeuRepository.save(jeu);
        return ResponseEntity
            .created(new URI("/api/jeus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /jeus/:id} : Updates an existing jeu.
     *
     * @param id the id of the jeu to save.
     * @param jeu the jeu to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jeu,
     * or with status {@code 400 (Bad Request)} if the jeu is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jeu couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/jeus/{id}")
    public ResponseEntity<Jeu> updateJeu(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Jeu jeu)
        throws URISyntaxException {
        log.debug("REST request to update Jeu : {}, {}", id, jeu);
        if (jeu.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jeu.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jeuRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Jeu result = jeuRepository.save(jeu);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jeu.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /jeus/:id} : Partial updates given fields of an existing jeu, field will ignore if it is null
     *
     * @param id the id of the jeu to save.
     * @param jeu the jeu to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jeu,
     * or with status {@code 400 (Bad Request)} if the jeu is not valid,
     * or with status {@code 404 (Not Found)} if the jeu is not found,
     * or with status {@code 500 (Internal Server Error)} if the jeu couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/jeus/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Jeu> partialUpdateJeu(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Jeu jeu)
        throws URISyntaxException {
        log.debug("REST request to partial update Jeu partially : {}, {}", id, jeu);
        if (jeu.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jeu.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jeuRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Jeu> result = jeuRepository
            .findById(jeu.getId())
            .map(existingJeu -> {
                if (jeu.getNom() != null) {
                    existingJeu.setNom(jeu.getNom());
                }

                return existingJeu;
            })
            .map(jeuRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jeu.getId().toString())
        );
    }

    /**
     * {@code GET  /jeus} : get all the jeus.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jeus in body.
     */
    @GetMapping("/jeus")
    public List<Jeu> getAllJeus() {
        log.debug("REST request to get all Jeus");
        return jeuRepository.findAll();
    }

    /**
     * {@code GET  /jeus/:id} : get the "id" jeu.
     *
     * @param id the id of the jeu to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jeu, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/jeus/{id}")
    public ResponseEntity<Jeu> getJeu(@PathVariable Long id) {
        log.debug("REST request to get Jeu : {}", id);
        Optional<Jeu> jeu = jeuRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(jeu);
    }

    /**
     * {@code DELETE  /jeus/:id} : delete the "id" jeu.
     *
     * @param id the id of the jeu to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/jeus/{id}")
    public ResponseEntity<Void> deleteJeu(@PathVariable Long id) {
        log.debug("REST request to delete Jeu : {}", id);
        jeuRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
