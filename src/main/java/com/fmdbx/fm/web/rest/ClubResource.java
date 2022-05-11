package com.fmdbx.fm.web.rest;

import com.fmdbx.fm.domain.Club;
import com.fmdbx.fm.repository.ClubRepository;
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
 * REST controller for managing {@link com.fmdbx.fm.domain.Club}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClubResource {

    private final Logger log = LoggerFactory.getLogger(ClubResource.class);

    private static final String ENTITY_NAME = "club";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClubRepository clubRepository;

    public ClubResource(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * {@code POST  /clubs} : Create a new club.
     *
     * @param club the club to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new club, or with status {@code 400 (Bad Request)} if the club has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clubs")
    public ResponseEntity<Club> createClub(@Valid @RequestBody Club club) throws URISyntaxException {
        log.debug("REST request to save Club : {}", club);
        if (club.getId() != null) {
            throw new BadRequestAlertException("A new club cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Club result = clubRepository.save(club);
        return ResponseEntity
            .created(new URI("/api/clubs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clubs/:id} : Updates an existing club.
     *
     * @param id the id of the club to save.
     * @param club the club to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated club,
     * or with status {@code 400 (Bad Request)} if the club is not valid,
     * or with status {@code 500 (Internal Server Error)} if the club couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clubs/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Club club)
        throws URISyntaxException {
        log.debug("REST request to update Club : {}, {}", id, club);
        if (club.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, club.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clubRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Club result = clubRepository.save(club);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, club.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /clubs/:id} : Partial updates given fields of an existing club, field will ignore if it is null
     *
     * @param id the id of the club to save.
     * @param club the club to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated club,
     * or with status {@code 400 (Bad Request)} if the club is not valid,
     * or with status {@code 404 (Not Found)} if the club is not found,
     * or with status {@code 500 (Internal Server Error)} if the club couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clubs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Club> partialUpdateClub(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Club club
    ) throws URISyntaxException {
        log.debug("REST request to partial update Club partially : {}, {}", id, club);
        if (club.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, club.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clubRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Club> result = clubRepository
            .findById(club.getId())
            .map(existingClub -> {
                if (club.getNom() != null) {
                    existingClub.setNom(club.getNom());
                }
                if (club.getLogo() != null) {
                    existingClub.setLogo(club.getLogo());
                }
                if (club.getLogoContentType() != null) {
                    existingClub.setLogoContentType(club.getLogoContentType());
                }
                if (club.getVille() != null) {
                    existingClub.setVille(club.getVille());
                }
                if (club.getBalance() != null) {
                    existingClub.setBalance(club.getBalance());
                }
                if (club.getMasseSalariale() != null) {
                    existingClub.setMasseSalariale(club.getMasseSalariale());
                }
                if (club.getBudgetSalaires() != null) {
                    existingClub.setBudgetSalaires(club.getBudgetSalaires());
                }
                if (club.getBudgetTransferts() != null) {
                    existingClub.setBudgetTransferts(club.getBudgetTransferts());
                }
                if (club.getInfrastructuresEntrainement() != null) {
                    existingClub.setInfrastructuresEntrainement(club.getInfrastructuresEntrainement());
                }
                if (club.getInfrastructuresJeunes() != null) {
                    existingClub.setInfrastructuresJeunes(club.getInfrastructuresJeunes());
                }
                if (club.getRecrutementJeunes() != null) {
                    existingClub.setRecrutementJeunes(club.getRecrutementJeunes());
                }
                if (club.getNomStade() != null) {
                    existingClub.setNomStade(club.getNomStade());
                }
                if (club.getCapaciteStade() != null) {
                    existingClub.setCapaciteStade(club.getCapaciteStade());
                }
                if (club.getPrevisionMedia() != null) {
                    existingClub.setPrevisionMedia(club.getPrevisionMedia());
                }
                if (club.getIndiceContinental() != null) {
                    existingClub.setIndiceContinental(club.getIndiceContinental());
                }
                if (club.getCompetitionContinentale() != null) {
                    existingClub.setCompetitionContinentale(club.getCompetitionContinentale());
                }

                return existingClub;
            })
            .map(clubRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, club.getId().toString())
        );
    }

    /**
     * {@code GET  /clubs} : get all the clubs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clubs in body.
     */
    @GetMapping("/clubs")
    public List<Club> getAllClubs(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Clubs");
        return clubRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /clubs/:id} : get the "id" club.
     *
     * @param id the id of the club to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the club, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clubs/{id}")
    public ResponseEntity<Club> getClub(@PathVariable Long id) {
        log.debug("REST request to get Club : {}", id);
        Optional<Club> club = clubRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(club);
    }

    /**
     * {@code DELETE  /clubs/:id} : delete the "id" club.
     *
     * @param id the id of the club to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clubs/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        log.debug("REST request to delete Club : {}", id);
        clubRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
