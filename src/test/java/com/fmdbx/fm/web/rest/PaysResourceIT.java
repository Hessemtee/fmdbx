package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Pays;
import com.fmdbx.fm.repository.PaysRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PaysResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PaysResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DRAPEAU = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DRAPEAU = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DRAPEAU_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DRAPEAU_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CONFEDERATION = "AAAAAAAAAA";
    private static final String UPDATED_CONFEDERATION = "BBBBBBBBBB";

    private static final Integer DEFAULT_INDICE_CONF = 1;
    private static final Integer UPDATED_INDICE_CONF = 2;

    private static final Integer DEFAULT_RANKING_FIFA = 1;
    private static final Integer UPDATED_RANKING_FIFA = 2;

    private static final Integer DEFAULT_ANNEES_AVANT_NATIONALITE = 1;
    private static final Integer UPDATED_ANNEES_AVANT_NATIONALITE = 2;

    private static final Integer DEFAULT_IMPORTANCE_EN_JEU = 1;
    private static final Integer UPDATED_IMPORTANCE_EN_JEU = 2;

    private static final String ENTITY_API_URL = "/api/pays";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaysRepository paysRepository;

    @Mock
    private PaysRepository paysRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaysMockMvc;

    private Pays pays;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pays createEntity(EntityManager em) {
        Pays pays = new Pays()
            .nom(DEFAULT_NOM)
            .drapeau(DEFAULT_DRAPEAU)
            .drapeauContentType(DEFAULT_DRAPEAU_CONTENT_TYPE)
            .confederation(DEFAULT_CONFEDERATION)
            .indiceConf(DEFAULT_INDICE_CONF)
            .rankingFifa(DEFAULT_RANKING_FIFA)
            .anneesAvantNationalite(DEFAULT_ANNEES_AVANT_NATIONALITE)
            .importanceEnJeu(DEFAULT_IMPORTANCE_EN_JEU);
        return pays;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pays createUpdatedEntity(EntityManager em) {
        Pays pays = new Pays()
            .nom(UPDATED_NOM)
            .drapeau(UPDATED_DRAPEAU)
            .drapeauContentType(UPDATED_DRAPEAU_CONTENT_TYPE)
            .confederation(UPDATED_CONFEDERATION)
            .indiceConf(UPDATED_INDICE_CONF)
            .rankingFifa(UPDATED_RANKING_FIFA)
            .anneesAvantNationalite(UPDATED_ANNEES_AVANT_NATIONALITE)
            .importanceEnJeu(UPDATED_IMPORTANCE_EN_JEU);
        return pays;
    }

    @BeforeEach
    public void initTest() {
        pays = createEntity(em);
    }

    @Test
    @Transactional
    void createPays() throws Exception {
        int databaseSizeBeforeCreate = paysRepository.findAll().size();
        // Create the Pays
        restPaysMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pays)))
            .andExpect(status().isCreated());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeCreate + 1);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testPays.getDrapeau()).isEqualTo(DEFAULT_DRAPEAU);
        assertThat(testPays.getDrapeauContentType()).isEqualTo(DEFAULT_DRAPEAU_CONTENT_TYPE);
        assertThat(testPays.getConfederation()).isEqualTo(DEFAULT_CONFEDERATION);
        assertThat(testPays.getIndiceConf()).isEqualTo(DEFAULT_INDICE_CONF);
        assertThat(testPays.getRankingFifa()).isEqualTo(DEFAULT_RANKING_FIFA);
        assertThat(testPays.getAnneesAvantNationalite()).isEqualTo(DEFAULT_ANNEES_AVANT_NATIONALITE);
        assertThat(testPays.getImportanceEnJeu()).isEqualTo(DEFAULT_IMPORTANCE_EN_JEU);
    }

    @Test
    @Transactional
    void createPaysWithExistingId() throws Exception {
        // Create the Pays with an existing ID
        pays.setId(1L);

        int databaseSizeBeforeCreate = paysRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaysMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pays)))
            .andExpect(status().isBadRequest());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        // Get all the paysList
        restPaysMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pays.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].drapeauContentType").value(hasItem(DEFAULT_DRAPEAU_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].drapeau").value(hasItem(Base64Utils.encodeToString(DEFAULT_DRAPEAU))))
            .andExpect(jsonPath("$.[*].confederation").value(hasItem(DEFAULT_CONFEDERATION)))
            .andExpect(jsonPath("$.[*].indiceConf").value(hasItem(DEFAULT_INDICE_CONF)))
            .andExpect(jsonPath("$.[*].rankingFifa").value(hasItem(DEFAULT_RANKING_FIFA)))
            .andExpect(jsonPath("$.[*].anneesAvantNationalite").value(hasItem(DEFAULT_ANNEES_AVANT_NATIONALITE)))
            .andExpect(jsonPath("$.[*].importanceEnJeu").value(hasItem(DEFAULT_IMPORTANCE_EN_JEU)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaysWithEagerRelationshipsIsEnabled() throws Exception {
        when(paysRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaysMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paysRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaysWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(paysRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaysMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paysRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        // Get the pays
        restPaysMockMvc
            .perform(get(ENTITY_API_URL_ID, pays.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pays.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.drapeauContentType").value(DEFAULT_DRAPEAU_CONTENT_TYPE))
            .andExpect(jsonPath("$.drapeau").value(Base64Utils.encodeToString(DEFAULT_DRAPEAU)))
            .andExpect(jsonPath("$.confederation").value(DEFAULT_CONFEDERATION))
            .andExpect(jsonPath("$.indiceConf").value(DEFAULT_INDICE_CONF))
            .andExpect(jsonPath("$.rankingFifa").value(DEFAULT_RANKING_FIFA))
            .andExpect(jsonPath("$.anneesAvantNationalite").value(DEFAULT_ANNEES_AVANT_NATIONALITE))
            .andExpect(jsonPath("$.importanceEnJeu").value(DEFAULT_IMPORTANCE_EN_JEU));
    }

    @Test
    @Transactional
    void getNonExistingPays() throws Exception {
        // Get the pays
        restPaysMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        int databaseSizeBeforeUpdate = paysRepository.findAll().size();

        // Update the pays
        Pays updatedPays = paysRepository.findById(pays.getId()).get();
        // Disconnect from session so that the updates on updatedPays are not directly saved in db
        em.detach(updatedPays);
        updatedPays
            .nom(UPDATED_NOM)
            .drapeau(UPDATED_DRAPEAU)
            .drapeauContentType(UPDATED_DRAPEAU_CONTENT_TYPE)
            .confederation(UPDATED_CONFEDERATION)
            .indiceConf(UPDATED_INDICE_CONF)
            .rankingFifa(UPDATED_RANKING_FIFA)
            .anneesAvantNationalite(UPDATED_ANNEES_AVANT_NATIONALITE)
            .importanceEnJeu(UPDATED_IMPORTANCE_EN_JEU);

        restPaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPays.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPays))
            )
            .andExpect(status().isOk());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testPays.getDrapeau()).isEqualTo(UPDATED_DRAPEAU);
        assertThat(testPays.getDrapeauContentType()).isEqualTo(UPDATED_DRAPEAU_CONTENT_TYPE);
        assertThat(testPays.getConfederation()).isEqualTo(UPDATED_CONFEDERATION);
        assertThat(testPays.getIndiceConf()).isEqualTo(UPDATED_INDICE_CONF);
        assertThat(testPays.getRankingFifa()).isEqualTo(UPDATED_RANKING_FIFA);
        assertThat(testPays.getAnneesAvantNationalite()).isEqualTo(UPDATED_ANNEES_AVANT_NATIONALITE);
        assertThat(testPays.getImportanceEnJeu()).isEqualTo(UPDATED_IMPORTANCE_EN_JEU);
    }

    @Test
    @Transactional
    void putNonExistingPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().size();
        pays.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pays.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pays))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().size();
        pays.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pays))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().size();
        pays.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaysMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pays)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaysWithPatch() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        int databaseSizeBeforeUpdate = paysRepository.findAll().size();

        // Update the pays using partial update
        Pays partialUpdatedPays = new Pays();
        partialUpdatedPays.setId(pays.getId());

        partialUpdatedPays
            .nom(UPDATED_NOM)
            .anneesAvantNationalite(UPDATED_ANNEES_AVANT_NATIONALITE)
            .importanceEnJeu(UPDATED_IMPORTANCE_EN_JEU);

        restPaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPays))
            )
            .andExpect(status().isOk());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testPays.getDrapeau()).isEqualTo(DEFAULT_DRAPEAU);
        assertThat(testPays.getDrapeauContentType()).isEqualTo(DEFAULT_DRAPEAU_CONTENT_TYPE);
        assertThat(testPays.getConfederation()).isEqualTo(DEFAULT_CONFEDERATION);
        assertThat(testPays.getIndiceConf()).isEqualTo(DEFAULT_INDICE_CONF);
        assertThat(testPays.getRankingFifa()).isEqualTo(DEFAULT_RANKING_FIFA);
        assertThat(testPays.getAnneesAvantNationalite()).isEqualTo(UPDATED_ANNEES_AVANT_NATIONALITE);
        assertThat(testPays.getImportanceEnJeu()).isEqualTo(UPDATED_IMPORTANCE_EN_JEU);
    }

    @Test
    @Transactional
    void fullUpdatePaysWithPatch() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        int databaseSizeBeforeUpdate = paysRepository.findAll().size();

        // Update the pays using partial update
        Pays partialUpdatedPays = new Pays();
        partialUpdatedPays.setId(pays.getId());

        partialUpdatedPays
            .nom(UPDATED_NOM)
            .drapeau(UPDATED_DRAPEAU)
            .drapeauContentType(UPDATED_DRAPEAU_CONTENT_TYPE)
            .confederation(UPDATED_CONFEDERATION)
            .indiceConf(UPDATED_INDICE_CONF)
            .rankingFifa(UPDATED_RANKING_FIFA)
            .anneesAvantNationalite(UPDATED_ANNEES_AVANT_NATIONALITE)
            .importanceEnJeu(UPDATED_IMPORTANCE_EN_JEU);

        restPaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPays))
            )
            .andExpect(status().isOk());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testPays.getDrapeau()).isEqualTo(UPDATED_DRAPEAU);
        assertThat(testPays.getDrapeauContentType()).isEqualTo(UPDATED_DRAPEAU_CONTENT_TYPE);
        assertThat(testPays.getConfederation()).isEqualTo(UPDATED_CONFEDERATION);
        assertThat(testPays.getIndiceConf()).isEqualTo(UPDATED_INDICE_CONF);
        assertThat(testPays.getRankingFifa()).isEqualTo(UPDATED_RANKING_FIFA);
        assertThat(testPays.getAnneesAvantNationalite()).isEqualTo(UPDATED_ANNEES_AVANT_NATIONALITE);
        assertThat(testPays.getImportanceEnJeu()).isEqualTo(UPDATED_IMPORTANCE_EN_JEU);
    }

    @Test
    @Transactional
    void patchNonExistingPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().size();
        pays.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pays))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().size();
        pays.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pays))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().size();
        pays.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaysMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pays)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePays() throws Exception {
        // Initialize the database
        paysRepository.saveAndFlush(pays);

        int databaseSizeBeforeDelete = paysRepository.findAll().size();

        // Delete the pays
        restPaysMockMvc
            .perform(delete(ENTITY_API_URL_ID, pays.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pays> paysList = paysRepository.findAll();
        assertThat(paysList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
