package com.fmdbx.fm.web.rest;

import com.fmdbx.fm.domain.Abonne;
import com.fmdbx.fm.repository.AbonneRepository;
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
 * REST controller for managing {@link com.fmdbx.fm.domain.Abonne}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AbonneResource {

    private final Logger log = LoggerFactory.getLogger(AbonneResource.class);

    private static final String ENTITY_NAME = "abonne";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AbonneRepository abonneRepository;

    public AbonneResource(AbonneRepository abonneRepository) {
        this.abonneRepository = abonneRepository;
    }

    /**
     * {@code POST  /abonnes} : Create a new abonne.
     *
     * @param abonne the abonne to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new abonne, or with status {@code 400 (Bad Request)} if the abonne has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/abonnes")
    public ResponseEntity<Abonne> createAbonne(@Valid @RequestBody Abonne abonne) throws URISyntaxException {
        log.debug("REST request to save Abonne : {}", abonne);
        if (abonne.getId() != null) {
            throw new BadRequestAlertException("A new abonne cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Abonne result = abonneRepository.save(abonne);
        return ResponseEntity
            .created(new URI("/api/abonnes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /abonnes/:id} : Updates an existing abonne.
     *
     * @param id the id of the abonne to save.
     * @param abonne the abonne to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated abonne,
     * or with status {@code 400 (Bad Request)} if the abonne is not valid,
     * or with status {@code 500 (Internal Server Error)} if the abonne couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/abonnes/{id}")
    public ResponseEntity<Abonne> updateAbonne(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Abonne abonne
    ) throws URISyntaxException {
        log.debug("REST request to update Abonne : {}, {}", id, abonne);
        if (abonne.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, abonne.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!abonneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Abonne result = abonneRepository.save(abonne);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, abonne.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /abonnes/:id} : Partial updates given fields of an existing abonne, field will ignore if it is null
     *
     * @param id the id of the abonne to save.
     * @param abonne the abonne to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated abonne,
     * or with status {@code 400 (Bad Request)} if the abonne is not valid,
     * or with status {@code 404 (Not Found)} if the abonne is not found,
     * or with status {@code 500 (Internal Server Error)} if the abonne couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/abonnes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Abonne> partialUpdateAbonne(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Abonne abonne
    ) throws URISyntaxException {
        log.debug("REST request to partial update Abonne partially : {}, {}", id, abonne);
        if (abonne.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, abonne.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!abonneRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Abonne> result = abonneRepository
            .findById(abonne.getId())
            .map(existingAbonne -> {
                if (abonne.getNom() != null) {
                    existingAbonne.setNom(abonne.getNom());
                }
                if (abonne.getAvatar() != null) {
                    existingAbonne.setAvatar(abonne.getAvatar());
                }
                if (abonne.getAvatarContentType() != null) {
                    existingAbonne.setAvatarContentType(abonne.getAvatarContentType());
                }
                if (abonne.getPremium() != null) {
                    existingAbonne.setPremium(abonne.getPremium());
                }

                return existingAbonne;
            })
            .map(abonneRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, abonne.getId().toString())
        );
    }

    /**
     * {@code GET  /abonnes} : get all the abonnes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of abonnes in body.
     */
    @GetMapping("/abonnes")
    public List<Abonne> getAllAbonnes() {
        log.debug("REST request to get all Abonnes");
        return abonneRepository.findAll();
    }

    /**
     * {@code GET  /abonnes/:id} : get the "id" abonne.
     *
     * @param id the id of the abonne to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the abonne, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/abonnes/{id}")
    public ResponseEntity<Abonne> getAbonne(@PathVariable Long id) {
        log.debug("REST request to get Abonne : {}", id);
        Optional<Abonne> abonne = abonneRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(abonne);
    }

    /**
     * {@code DELETE  /abonnes/:id} : delete the "id" abonne.
     *
     * @param id the id of the abonne to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/abonnes/{id}")
    public ResponseEntity<Void> deleteAbonne(@PathVariable Long id) {
        log.debug("REST request to delete Abonne : {}", id);
        abonneRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
