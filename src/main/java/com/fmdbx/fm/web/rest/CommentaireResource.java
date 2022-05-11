package com.fmdbx.fm.web.rest;

import com.fmdbx.fm.domain.Commentaire;
import com.fmdbx.fm.repository.CommentaireRepository;
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
 * REST controller for managing {@link com.fmdbx.fm.domain.Commentaire}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CommentaireResource {

    private final Logger log = LoggerFactory.getLogger(CommentaireResource.class);

    private static final String ENTITY_NAME = "commentaire";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommentaireRepository commentaireRepository;

    public CommentaireResource(CommentaireRepository commentaireRepository) {
        this.commentaireRepository = commentaireRepository;
    }

    /**
     * {@code POST  /commentaires} : Create a new commentaire.
     *
     * @param commentaire the commentaire to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commentaire, or with status {@code 400 (Bad Request)} if the commentaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/commentaires")
    public ResponseEntity<Commentaire> createCommentaire(@Valid @RequestBody Commentaire commentaire) throws URISyntaxException {
        log.debug("REST request to save Commentaire : {}", commentaire);
        if (commentaire.getId() != null) {
            throw new BadRequestAlertException("A new commentaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Commentaire result = commentaireRepository.save(commentaire);
        return ResponseEntity
            .created(new URI("/api/commentaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /commentaires/:id} : Updates an existing commentaire.
     *
     * @param id the id of the commentaire to save.
     * @param commentaire the commentaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentaire,
     * or with status {@code 400 (Bad Request)} if the commentaire is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commentaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/commentaires/{id}")
    public ResponseEntity<Commentaire> updateCommentaire(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Commentaire commentaire
    ) throws URISyntaxException {
        log.debug("REST request to update Commentaire : {}, {}", id, commentaire);
        if (commentaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commentaire.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Commentaire result = commentaireRepository.save(commentaire);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commentaire.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /commentaires/:id} : Partial updates given fields of an existing commentaire, field will ignore if it is null
     *
     * @param id the id of the commentaire to save.
     * @param commentaire the commentaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commentaire,
     * or with status {@code 400 (Bad Request)} if the commentaire is not valid,
     * or with status {@code 404 (Not Found)} if the commentaire is not found,
     * or with status {@code 500 (Internal Server Error)} if the commentaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/commentaires/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Commentaire> partialUpdateCommentaire(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Commentaire commentaire
    ) throws URISyntaxException {
        log.debug("REST request to partial update Commentaire partially : {}, {}", id, commentaire);
        if (commentaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commentaire.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Commentaire> result = commentaireRepository
            .findById(commentaire.getId())
            .map(existingCommentaire -> {
                if (commentaire.getContenu() != null) {
                    existingCommentaire.setContenu(commentaire.getContenu());
                }
                if (commentaire.getVisible() != null) {
                    existingCommentaire.setVisible(commentaire.getVisible());
                }

                return existingCommentaire;
            })
            .map(commentaireRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, commentaire.getId().toString())
        );
    }

    /**
     * {@code GET  /commentaires} : get all the commentaires.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commentaires in body.
     */
    @GetMapping("/commentaires")
    public List<Commentaire> getAllCommentaires(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Commentaires");
        return commentaireRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /commentaires/:id} : get the "id" commentaire.
     *
     * @param id the id of the commentaire to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commentaire, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/commentaires/{id}")
    public ResponseEntity<Commentaire> getCommentaire(@PathVariable Long id) {
        log.debug("REST request to get Commentaire : {}", id);
        Optional<Commentaire> commentaire = commentaireRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(commentaire);
    }

    /**
     * {@code DELETE  /commentaires/:id} : delete the "id" commentaire.
     *
     * @param id the id of the commentaire to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/commentaires/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        log.debug("REST request to delete Commentaire : {}", id);
        commentaireRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
