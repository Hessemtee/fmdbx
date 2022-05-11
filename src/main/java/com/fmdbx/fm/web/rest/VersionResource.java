package com.fmdbx.fm.web.rest;

import com.fmdbx.fm.domain.Version;
import com.fmdbx.fm.repository.VersionRepository;
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
 * REST controller for managing {@link com.fmdbx.fm.domain.Version}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VersionResource {

    private final Logger log = LoggerFactory.getLogger(VersionResource.class);

    private static final String ENTITY_NAME = "version";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VersionRepository versionRepository;

    public VersionResource(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    /**
     * {@code POST  /versions} : Create a new version.
     *
     * @param version the version to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new version, or with status {@code 400 (Bad Request)} if the version has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/versions")
    public ResponseEntity<Version> createVersion(@Valid @RequestBody Version version) throws URISyntaxException {
        log.debug("REST request to save Version : {}", version);
        if (version.getId() != null) {
            throw new BadRequestAlertException("A new version cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Version result = versionRepository.save(version);
        return ResponseEntity
            .created(new URI("/api/versions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /versions/:id} : Updates an existing version.
     *
     * @param id the id of the version to save.
     * @param version the version to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated version,
     * or with status {@code 400 (Bad Request)} if the version is not valid,
     * or with status {@code 500 (Internal Server Error)} if the version couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/versions/{id}")
    public ResponseEntity<Version> updateVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Version version
    ) throws URISyntaxException {
        log.debug("REST request to update Version : {}, {}", id, version);
        if (version.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, version.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!versionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Version result = versionRepository.save(version);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, version.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /versions/:id} : Partial updates given fields of an existing version, field will ignore if it is null
     *
     * @param id the id of the version to save.
     * @param version the version to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated version,
     * or with status {@code 400 (Bad Request)} if the version is not valid,
     * or with status {@code 404 (Not Found)} if the version is not found,
     * or with status {@code 500 (Internal Server Error)} if the version couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/versions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Version> partialUpdateVersion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Version version
    ) throws URISyntaxException {
        log.debug("REST request to partial update Version partially : {}, {}", id, version);
        if (version.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, version.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!versionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Version> result = versionRepository
            .findById(version.getId())
            .map(existingVersion -> {
                if (version.getVersion() != null) {
                    existingVersion.setVersion(version.getVersion());
                }

                return existingVersion;
            })
            .map(versionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, version.getId().toString())
        );
    }

    /**
     * {@code GET  /versions} : get all the versions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of versions in body.
     */
    @GetMapping("/versions")
    public List<Version> getAllVersions() {
        log.debug("REST request to get all Versions");
        return versionRepository.findAll();
    }

    /**
     * {@code GET  /versions/:id} : get the "id" version.
     *
     * @param id the id of the version to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the version, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/versions/{id}")
    public ResponseEntity<Version> getVersion(@PathVariable Long id) {
        log.debug("REST request to get Version : {}", id);
        Optional<Version> version = versionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(version);
    }

    /**
     * {@code DELETE  /versions/:id} : delete the "id" version.
     *
     * @param id the id of the version to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/versions/{id}")
    public ResponseEntity<Void> deleteVersion(@PathVariable Long id) {
        log.debug("REST request to delete Version : {}", id);
        versionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
