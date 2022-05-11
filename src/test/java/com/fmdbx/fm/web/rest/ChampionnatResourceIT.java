package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Championnat;
import com.fmdbx.fm.domain.Pays;
import com.fmdbx.fm.repository.ChampionnatRepository;
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
 * Integration tests for the {@link ChampionnatResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChampionnatResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Integer DEFAULT_NOMBRE_D_EQUIPES = 1;
    private static final Integer UPDATED_NOMBRE_D_EQUIPES = 2;

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_NIVEAU = 1;
    private static final Integer UPDATED_NIVEAU = 2;

    private static final Integer DEFAULT_REPUTATION = 1;
    private static final Integer UPDATED_REPUTATION = 2;

    private static final String ENTITY_API_URL = "/api/championnats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChampionnatRepository championnatRepository;

    @Mock
    private ChampionnatRepository championnatRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChampionnatMockMvc;

    private Championnat championnat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Championnat createEntity(EntityManager em) {
        Championnat championnat = new Championnat()
            .nom(DEFAULT_NOM)
            .nombreDEquipes(DEFAULT_NOMBRE_D_EQUIPES)
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .niveau(DEFAULT_NIVEAU)
            .reputation(DEFAULT_REPUTATION);
        // Add required entity
        Pays pays;
        if (TestUtil.findAll(em, Pays.class).isEmpty()) {
            pays = PaysResourceIT.createEntity(em);
            em.persist(pays);
            em.flush();
        } else {
            pays = TestUtil.findAll(em, Pays.class).get(0);
        }
        championnat.setPays(pays);
        return championnat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Championnat createUpdatedEntity(EntityManager em) {
        Championnat championnat = new Championnat()
            .nom(UPDATED_NOM)
            .nombreDEquipes(UPDATED_NOMBRE_D_EQUIPES)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .niveau(UPDATED_NIVEAU)
            .reputation(UPDATED_REPUTATION);
        // Add required entity
        Pays pays;
        if (TestUtil.findAll(em, Pays.class).isEmpty()) {
            pays = PaysResourceIT.createUpdatedEntity(em);
            em.persist(pays);
            em.flush();
        } else {
            pays = TestUtil.findAll(em, Pays.class).get(0);
        }
        championnat.setPays(pays);
        return championnat;
    }

    @BeforeEach
    public void initTest() {
        championnat = createEntity(em);
    }

    @Test
    @Transactional
    void createChampionnat() throws Exception {
        int databaseSizeBeforeCreate = championnatRepository.findAll().size();
        // Create the Championnat
        restChampionnatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(championnat)))
            .andExpect(status().isCreated());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeCreate + 1);
        Championnat testChampionnat = championnatList.get(championnatList.size() - 1);
        assertThat(testChampionnat.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testChampionnat.getNombreDEquipes()).isEqualTo(DEFAULT_NOMBRE_D_EQUIPES);
        assertThat(testChampionnat.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testChampionnat.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testChampionnat.getNiveau()).isEqualTo(DEFAULT_NIVEAU);
        assertThat(testChampionnat.getReputation()).isEqualTo(DEFAULT_REPUTATION);
    }

    @Test
    @Transactional
    void createChampionnatWithExistingId() throws Exception {
        // Create the Championnat with an existing ID
        championnat.setId(1L);

        int databaseSizeBeforeCreate = championnatRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChampionnatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(championnat)))
            .andExpect(status().isBadRequest());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllChampionnats() throws Exception {
        // Initialize the database
        championnatRepository.saveAndFlush(championnat);

        // Get all the championnatList
        restChampionnatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(championnat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].nombreDEquipes").value(hasItem(DEFAULT_NOMBRE_D_EQUIPES)))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU)))
            .andExpect(jsonPath("$.[*].reputation").value(hasItem(DEFAULT_REPUTATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChampionnatsWithEagerRelationshipsIsEnabled() throws Exception {
        when(championnatRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChampionnatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(championnatRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChampionnatsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(championnatRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChampionnatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(championnatRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getChampionnat() throws Exception {
        // Initialize the database
        championnatRepository.saveAndFlush(championnat);

        // Get the championnat
        restChampionnatMockMvc
            .perform(get(ENTITY_API_URL_ID, championnat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(championnat.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.nombreDEquipes").value(DEFAULT_NOMBRE_D_EQUIPES))
            .andExpect(jsonPath("$.logoContentType").value(DEFAULT_LOGO_CONTENT_TYPE))
            .andExpect(jsonPath("$.logo").value(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.niveau").value(DEFAULT_NIVEAU))
            .andExpect(jsonPath("$.reputation").value(DEFAULT_REPUTATION));
    }

    @Test
    @Transactional
    void getNonExistingChampionnat() throws Exception {
        // Get the championnat
        restChampionnatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewChampionnat() throws Exception {
        // Initialize the database
        championnatRepository.saveAndFlush(championnat);

        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();

        // Update the championnat
        Championnat updatedChampionnat = championnatRepository.findById(championnat.getId()).get();
        // Disconnect from session so that the updates on updatedChampionnat are not directly saved in db
        em.detach(updatedChampionnat);
        updatedChampionnat
            .nom(UPDATED_NOM)
            .nombreDEquipes(UPDATED_NOMBRE_D_EQUIPES)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .niveau(UPDATED_NIVEAU)
            .reputation(UPDATED_REPUTATION);

        restChampionnatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedChampionnat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedChampionnat))
            )
            .andExpect(status().isOk());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
        Championnat testChampionnat = championnatList.get(championnatList.size() - 1);
        assertThat(testChampionnat.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testChampionnat.getNombreDEquipes()).isEqualTo(UPDATED_NOMBRE_D_EQUIPES);
        assertThat(testChampionnat.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testChampionnat.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testChampionnat.getNiveau()).isEqualTo(UPDATED_NIVEAU);
        assertThat(testChampionnat.getReputation()).isEqualTo(UPDATED_REPUTATION);
    }

    @Test
    @Transactional
    void putNonExistingChampionnat() throws Exception {
        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();
        championnat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChampionnatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, championnat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(championnat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChampionnat() throws Exception {
        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();
        championnat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChampionnatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(championnat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChampionnat() throws Exception {
        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();
        championnat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChampionnatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(championnat)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChampionnatWithPatch() throws Exception {
        // Initialize the database
        championnatRepository.saveAndFlush(championnat);

        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();

        // Update the championnat using partial update
        Championnat partialUpdatedChampionnat = new Championnat();
        partialUpdatedChampionnat.setId(championnat.getId());

        partialUpdatedChampionnat.nom(UPDATED_NOM).reputation(UPDATED_REPUTATION);

        restChampionnatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChampionnat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedChampionnat))
            )
            .andExpect(status().isOk());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
        Championnat testChampionnat = championnatList.get(championnatList.size() - 1);
        assertThat(testChampionnat.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testChampionnat.getNombreDEquipes()).isEqualTo(DEFAULT_NOMBRE_D_EQUIPES);
        assertThat(testChampionnat.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testChampionnat.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testChampionnat.getNiveau()).isEqualTo(DEFAULT_NIVEAU);
        assertThat(testChampionnat.getReputation()).isEqualTo(UPDATED_REPUTATION);
    }

    @Test
    @Transactional
    void fullUpdateChampionnatWithPatch() throws Exception {
        // Initialize the database
        championnatRepository.saveAndFlush(championnat);

        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();

        // Update the championnat using partial update
        Championnat partialUpdatedChampionnat = new Championnat();
        partialUpdatedChampionnat.setId(championnat.getId());

        partialUpdatedChampionnat
            .nom(UPDATED_NOM)
            .nombreDEquipes(UPDATED_NOMBRE_D_EQUIPES)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .niveau(UPDATED_NIVEAU)
            .reputation(UPDATED_REPUTATION);

        restChampionnatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChampionnat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedChampionnat))
            )
            .andExpect(status().isOk());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
        Championnat testChampionnat = championnatList.get(championnatList.size() - 1);
        assertThat(testChampionnat.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testChampionnat.getNombreDEquipes()).isEqualTo(UPDATED_NOMBRE_D_EQUIPES);
        assertThat(testChampionnat.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testChampionnat.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testChampionnat.getNiveau()).isEqualTo(UPDATED_NIVEAU);
        assertThat(testChampionnat.getReputation()).isEqualTo(UPDATED_REPUTATION);
    }

    @Test
    @Transactional
    void patchNonExistingChampionnat() throws Exception {
        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();
        championnat.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChampionnatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, championnat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(championnat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChampionnat() throws Exception {
        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();
        championnat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChampionnatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(championnat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChampionnat() throws Exception {
        int databaseSizeBeforeUpdate = championnatRepository.findAll().size();
        championnat.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChampionnatMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(championnat))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Championnat in the database
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChampionnat() throws Exception {
        // Initialize the database
        championnatRepository.saveAndFlush(championnat);

        int databaseSizeBeforeDelete = championnatRepository.findAll().size();

        // Delete the championnat
        restChampionnatMockMvc
            .perform(delete(ENTITY_API_URL_ID, championnat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Championnat> championnatList = championnatRepository.findAll();
        assertThat(championnatList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
