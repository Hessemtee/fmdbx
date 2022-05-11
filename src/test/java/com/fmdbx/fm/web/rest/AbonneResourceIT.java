package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Abonne;
import com.fmdbx.fm.domain.User;
import com.fmdbx.fm.repository.AbonneRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link AbonneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AbonneResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_AVATAR = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AVATAR = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AVATAR_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_PREMIUM = false;
    private static final Boolean UPDATED_PREMIUM = true;

    private static final String ENTITY_API_URL = "/api/abonnes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AbonneRepository abonneRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAbonneMockMvc;

    private Abonne abonne;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Abonne createEntity(EntityManager em) {
        Abonne abonne = new Abonne()
            .nom(DEFAULT_NOM)
            .avatar(DEFAULT_AVATAR)
            .avatarContentType(DEFAULT_AVATAR_CONTENT_TYPE)
            .premium(DEFAULT_PREMIUM);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        abonne.setUser(user);
        return abonne;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Abonne createUpdatedEntity(EntityManager em) {
        Abonne abonne = new Abonne()
            .nom(UPDATED_NOM)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .premium(UPDATED_PREMIUM);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        abonne.setUser(user);
        return abonne;
    }

    @BeforeEach
    public void initTest() {
        abonne = createEntity(em);
    }

    @Test
    @Transactional
    void createAbonne() throws Exception {
        int databaseSizeBeforeCreate = abonneRepository.findAll().size();
        // Create the Abonne
        restAbonneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(abonne)))
            .andExpect(status().isCreated());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeCreate + 1);
        Abonne testAbonne = abonneList.get(abonneList.size() - 1);
        assertThat(testAbonne.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testAbonne.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testAbonne.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testAbonne.getPremium()).isEqualTo(DEFAULT_PREMIUM);
    }

    @Test
    @Transactional
    void createAbonneWithExistingId() throws Exception {
        // Create the Abonne with an existing ID
        abonne.setId(1L);

        int databaseSizeBeforeCreate = abonneRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAbonneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(abonne)))
            .andExpect(status().isBadRequest());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAbonnes() throws Exception {
        // Initialize the database
        abonneRepository.saveAndFlush(abonne);

        // Get all the abonneList
        restAbonneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(abonne.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].avatarContentType").value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].avatar").value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR))))
            .andExpect(jsonPath("$.[*].premium").value(hasItem(DEFAULT_PREMIUM.booleanValue())));
    }

    @Test
    @Transactional
    void getAbonne() throws Exception {
        // Initialize the database
        abonneRepository.saveAndFlush(abonne);

        // Get the abonne
        restAbonneMockMvc
            .perform(get(ENTITY_API_URL_ID, abonne.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(abonne.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.avatarContentType").value(DEFAULT_AVATAR_CONTENT_TYPE))
            .andExpect(jsonPath("$.avatar").value(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .andExpect(jsonPath("$.premium").value(DEFAULT_PREMIUM.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingAbonne() throws Exception {
        // Get the abonne
        restAbonneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAbonne() throws Exception {
        // Initialize the database
        abonneRepository.saveAndFlush(abonne);

        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();

        // Update the abonne
        Abonne updatedAbonne = abonneRepository.findById(abonne.getId()).get();
        // Disconnect from session so that the updates on updatedAbonne are not directly saved in db
        em.detach(updatedAbonne);
        updatedAbonne.nom(UPDATED_NOM).avatar(UPDATED_AVATAR).avatarContentType(UPDATED_AVATAR_CONTENT_TYPE).premium(UPDATED_PREMIUM);

        restAbonneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAbonne.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAbonne))
            )
            .andExpect(status().isOk());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
        Abonne testAbonne = abonneList.get(abonneList.size() - 1);
        assertThat(testAbonne.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAbonne.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testAbonne.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testAbonne.getPremium()).isEqualTo(UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void putNonExistingAbonne() throws Exception {
        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();
        abonne.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAbonneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, abonne.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(abonne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAbonne() throws Exception {
        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();
        abonne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbonneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(abonne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAbonne() throws Exception {
        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();
        abonne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbonneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(abonne)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAbonneWithPatch() throws Exception {
        // Initialize the database
        abonneRepository.saveAndFlush(abonne);

        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();

        // Update the abonne using partial update
        Abonne partialUpdatedAbonne = new Abonne();
        partialUpdatedAbonne.setId(abonne.getId());

        restAbonneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAbonne.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAbonne))
            )
            .andExpect(status().isOk());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
        Abonne testAbonne = abonneList.get(abonneList.size() - 1);
        assertThat(testAbonne.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testAbonne.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testAbonne.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testAbonne.getPremium()).isEqualTo(DEFAULT_PREMIUM);
    }

    @Test
    @Transactional
    void fullUpdateAbonneWithPatch() throws Exception {
        // Initialize the database
        abonneRepository.saveAndFlush(abonne);

        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();

        // Update the abonne using partial update
        Abonne partialUpdatedAbonne = new Abonne();
        partialUpdatedAbonne.setId(abonne.getId());

        partialUpdatedAbonne
            .nom(UPDATED_NOM)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .premium(UPDATED_PREMIUM);

        restAbonneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAbonne.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAbonne))
            )
            .andExpect(status().isOk());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
        Abonne testAbonne = abonneList.get(abonneList.size() - 1);
        assertThat(testAbonne.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testAbonne.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testAbonne.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testAbonne.getPremium()).isEqualTo(UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    void patchNonExistingAbonne() throws Exception {
        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();
        abonne.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAbonneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, abonne.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(abonne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAbonne() throws Exception {
        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();
        abonne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbonneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(abonne))
            )
            .andExpect(status().isBadRequest());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAbonne() throws Exception {
        int databaseSizeBeforeUpdate = abonneRepository.findAll().size();
        abonne.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAbonneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(abonne)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Abonne in the database
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAbonne() throws Exception {
        // Initialize the database
        abonneRepository.saveAndFlush(abonne);

        int databaseSizeBeforeDelete = abonneRepository.findAll().size();

        // Delete the abonne
        restAbonneMockMvc
            .perform(delete(ENTITY_API_URL_ID, abonne.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Abonne> abonneList = abonneRepository.findAll();
        assertThat(abonneList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
