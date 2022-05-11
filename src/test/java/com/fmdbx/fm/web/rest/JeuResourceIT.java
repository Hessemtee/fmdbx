package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Jeu;
import com.fmdbx.fm.domain.Version;
import com.fmdbx.fm.repository.JeuRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link JeuResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JeuResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/jeus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JeuRepository jeuRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJeuMockMvc;

    private Jeu jeu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jeu createEntity(EntityManager em) {
        Jeu jeu = new Jeu().nom(DEFAULT_NOM);
        // Add required entity
        Version version;
        if (TestUtil.findAll(em, Version.class).isEmpty()) {
            version = VersionResourceIT.createEntity(em);
            em.persist(version);
            em.flush();
        } else {
            version = TestUtil.findAll(em, Version.class).get(0);
        }
        jeu.getVersions().add(version);
        return jeu;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jeu createUpdatedEntity(EntityManager em) {
        Jeu jeu = new Jeu().nom(UPDATED_NOM);
        // Add required entity
        Version version;
        if (TestUtil.findAll(em, Version.class).isEmpty()) {
            version = VersionResourceIT.createUpdatedEntity(em);
            em.persist(version);
            em.flush();
        } else {
            version = TestUtil.findAll(em, Version.class).get(0);
        }
        jeu.getVersions().add(version);
        return jeu;
    }

    @BeforeEach
    public void initTest() {
        jeu = createEntity(em);
    }

    @Test
    @Transactional
    void createJeu() throws Exception {
        int databaseSizeBeforeCreate = jeuRepository.findAll().size();
        // Create the Jeu
        restJeuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jeu)))
            .andExpect(status().isCreated());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeCreate + 1);
        Jeu testJeu = jeuList.get(jeuList.size() - 1);
        assertThat(testJeu.getNom()).isEqualTo(DEFAULT_NOM);
    }

    @Test
    @Transactional
    void createJeuWithExistingId() throws Exception {
        // Create the Jeu with an existing ID
        jeu.setId(1L);

        int databaseSizeBeforeCreate = jeuRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJeuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jeu)))
            .andExpect(status().isBadRequest());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = jeuRepository.findAll().size();
        // set the field null
        jeu.setNom(null);

        // Create the Jeu, which fails.

        restJeuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jeu)))
            .andExpect(status().isBadRequest());

        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJeus() throws Exception {
        // Initialize the database
        jeuRepository.saveAndFlush(jeu);

        // Get all the jeuList
        restJeuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jeu.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    void getJeu() throws Exception {
        // Initialize the database
        jeuRepository.saveAndFlush(jeu);

        // Get the jeu
        restJeuMockMvc
            .perform(get(ENTITY_API_URL_ID, jeu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jeu.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getNonExistingJeu() throws Exception {
        // Get the jeu
        restJeuMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJeu() throws Exception {
        // Initialize the database
        jeuRepository.saveAndFlush(jeu);

        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();

        // Update the jeu
        Jeu updatedJeu = jeuRepository.findById(jeu.getId()).get();
        // Disconnect from session so that the updates on updatedJeu are not directly saved in db
        em.detach(updatedJeu);
        updatedJeu.nom(UPDATED_NOM);

        restJeuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJeu.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJeu))
            )
            .andExpect(status().isOk());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
        Jeu testJeu = jeuList.get(jeuList.size() - 1);
        assertThat(testJeu.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void putNonExistingJeu() throws Exception {
        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();
        jeu.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJeuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jeu.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jeu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJeu() throws Exception {
        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();
        jeu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJeuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jeu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJeu() throws Exception {
        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();
        jeu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJeuMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(jeu)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJeuWithPatch() throws Exception {
        // Initialize the database
        jeuRepository.saveAndFlush(jeu);

        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();

        // Update the jeu using partial update
        Jeu partialUpdatedJeu = new Jeu();
        partialUpdatedJeu.setId(jeu.getId());

        partialUpdatedJeu.nom(UPDATED_NOM);

        restJeuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJeu.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJeu))
            )
            .andExpect(status().isOk());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
        Jeu testJeu = jeuList.get(jeuList.size() - 1);
        assertThat(testJeu.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void fullUpdateJeuWithPatch() throws Exception {
        // Initialize the database
        jeuRepository.saveAndFlush(jeu);

        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();

        // Update the jeu using partial update
        Jeu partialUpdatedJeu = new Jeu();
        partialUpdatedJeu.setId(jeu.getId());

        partialUpdatedJeu.nom(UPDATED_NOM);

        restJeuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJeu.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJeu))
            )
            .andExpect(status().isOk());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
        Jeu testJeu = jeuList.get(jeuList.size() - 1);
        assertThat(testJeu.getNom()).isEqualTo(UPDATED_NOM);
    }

    @Test
    @Transactional
    void patchNonExistingJeu() throws Exception {
        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();
        jeu.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJeuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jeu.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jeu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJeu() throws Exception {
        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();
        jeu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJeuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jeu))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJeu() throws Exception {
        int databaseSizeBeforeUpdate = jeuRepository.findAll().size();
        jeu.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJeuMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(jeu)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jeu in the database
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJeu() throws Exception {
        // Initialize the database
        jeuRepository.saveAndFlush(jeu);

        int databaseSizeBeforeDelete = jeuRepository.findAll().size();

        // Delete the jeu
        restJeuMockMvc.perform(delete(ENTITY_API_URL_ID, jeu.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Jeu> jeuList = jeuRepository.findAll();
        assertThat(jeuList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
