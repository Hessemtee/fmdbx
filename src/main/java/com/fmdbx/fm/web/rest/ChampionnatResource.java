package com.fmdbx.fm.web.rest;

import com.fmdbx.fm.domain.Championnat;
import com.fmdbx.fm.repository.ChampionnatRepository;
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
 * REST controller for managing {@link com.fmdbx.fm.domain.Championnat}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ChampionnatResource {

    private final Logger log = LoggerFactory.getLogger(ChampionnatResource.class);

    private static final String ENTITY_NAME = "championnat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChampionnatRepository championnatRepository;

    public ChampionnatResource(ChampionnatRepository championnatRepository) {
        this.championnatRepository = championnatRepository;
    }

    /**
     * {@code POST  /championnats} : Create a new championnat.
     *
     * @param championnat the championnat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new championnat, or with status {@code 400 (Bad Request)} if the championnat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/championnats")
    public ResponseEntity<Championnat> createChampionnat(@Valid @RequestBody Championnat championnat) throws URISyntaxException {
        log.debug("REST request to save Championnat : {}", championnat);
        if (championnat.getId() != null) {
            throw new BadRequestAlertException("A new championnat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Championnat result = championnatRepository.save(championnat);
        return ResponseEntity
            .created(new URI("/api/championnats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /championnats/:id} : Updates an existing championnat.
     *
     * @param id the id of the championnat to save.
     * @param championnat the championnat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated championnat,
     * or with status {@code 400 (Bad Request)} if the championnat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the championnat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/championnats/{id}")
    public ResponseEntity<Championnat> updateChampionnat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Championnat championnat
    ) throws URISyntaxException {
        log.debug("REST request to update Championnat : {}, {}", id, championnat);
        if (championnat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, championnat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!championnatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Championnat result = championnatRepository.save(championnat);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, championnat.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /championnats/:id} : Partial updates given fields of an existing championnat, field will ignore if it is null
     *
     * @param id the id of the championnat to save.
     * @param championnat the championnat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated championnat,
     * or with status {@code 400 (Bad Request)} if the championnat is not valid,
     * or with status {@code 404 (Not Found)} if the championnat is not found,
     * or with status {@code 500 (Internal Server Error)} if the championnat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/championnats/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Championnat> partialUpdateChampionnat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Championnat championnat
    ) throws URISyntaxException {
        log.debug("REST request to partial update Championnat partially : {}, {}", id, championnat);
        if (championnat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, championnat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!championnatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Championnat> result = championnatRepository
            .findById(championnat.getId())
            .map(existingChampionnat -> {
                if (championnat.getNom() != null) {
                    existingChampionnat.setNom(championnat.getNom());
                }
                if (championnat.getNombreDEquipes() != null) {
                    existingChampionnat.setNombreDEquipes(championnat.getNombreDEquipes());
                }
                if (championnat.getLogo() != null) {
                    existingChampionnat.setLogo(championnat.getLogo());
                }
                if (championnat.getLogoContentType() != null) {
                    existingChampionnat.setLogoContentType(championnat.getLogoContentType());
                }
                if (championnat.getNiveau() != null) {
                    existingChampionnat.setNiveau(championnat.getNiveau());
                }
                if (championnat.getReputation() != null) {
                    existingChampionnat.setReputation(championnat.getReputation());
                }

                return existingChampionnat;
            })
            .map(championnatRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, championnat.getId().toString())
        );
    }

    /**
     * {@code GET  /championnats} : get all the championnats.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of championnats in body.
     */
    @GetMapping("/championnats")
    public List<Championnat> getAllChampionnats(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Championnats");
        return championnatRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /championnats/:id} : get the "id" championnat.
     *
     * @param id the id of the championnat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the championnat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/championnats/{id}")
    public ResponseEntity<Championnat> getChampionnat(@PathVariable Long id) {
        log.debug("REST request to get Championnat : {}", id);
        Optional<Championnat> championnat = championnatRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(championnat);
    }

    /**
     * {@code DELETE  /championnats/:id} : delete the "id" championnat.
     *
     * @param id the id of the championnat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/championnats/{id}")
    public ResponseEntity<Void> deleteChampionnat(@PathVariable Long id) {
        log.debug("REST request to delete Championnat : {}", id);
        championnatRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
