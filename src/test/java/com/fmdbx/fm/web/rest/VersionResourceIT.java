package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Version;
import com.fmdbx.fm.repository.VersionRepository;
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
 * Integration tests for the {@link VersionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VersionResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/versions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVersionMockMvc;

    private Version version;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Version createEntity(EntityManager em) {
        Version version = new Version().version(DEFAULT_VERSION);
        return version;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Version createUpdatedEntity(EntityManager em) {
        Version version = new Version().version(UPDATED_VERSION);
        return version;
    }

    @BeforeEach
    public void initTest() {
        version = createEntity(em);
    }

    @Test
    @Transactional
    void createVersion() throws Exception {
        int databaseSizeBeforeCreate = versionRepository.findAll().size();
        // Create the Version
        restVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(version)))
            .andExpect(status().isCreated());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeCreate + 1);
        Version testVersion = versionList.get(versionList.size() - 1);
        assertThat(testVersion.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void createVersionWithExistingId() throws Exception {
        // Create the Version with an existing ID
        version.setId(1L);

        int databaseSizeBeforeCreate = versionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(version)))
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = versionRepository.findAll().size();
        // set the field null
        version.setVersion(null);

        // Create the Version, which fails.

        restVersionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(version)))
            .andExpect(status().isBadRequest());

        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVersions() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        // Get all the versionList
        restVersionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(version.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    @Transactional
    void getVersion() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        // Get the version
        restVersionMockMvc
            .perform(get(ENTITY_API_URL_ID, version.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(version.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    @Transactional
    void getNonExistingVersion() throws Exception {
        // Get the version
        restVersionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVersion() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        int databaseSizeBeforeUpdate = versionRepository.findAll().size();

        // Update the version
        Version updatedVersion = versionRepository.findById(version.getId()).get();
        // Disconnect from session so that the updates on updatedVersion are not directly saved in db
        em.detach(updatedVersion);
        updatedVersion.version(UPDATED_VERSION);

        restVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVersion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVersion))
            )
            .andExpect(status().isOk());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
        Version testVersion = versionList.get(versionList.size() - 1);
        assertThat(testVersion.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void putNonExistingVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();
        version.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, version.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(version))
            )
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();
        version.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(version))
            )
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();
        version.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(version)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVersionWithPatch() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        int databaseSizeBeforeUpdate = versionRepository.findAll().size();

        // Update the version using partial update
        Version partialUpdatedVersion = new Version();
        partialUpdatedVersion.setId(version.getId());

        partialUpdatedVersion.version(UPDATED_VERSION);

        restVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVersion))
            )
            .andExpect(status().isOk());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
        Version testVersion = versionList.get(versionList.size() - 1);
        assertThat(testVersion.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void fullUpdateVersionWithPatch() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        int databaseSizeBeforeUpdate = versionRepository.findAll().size();

        // Update the version using partial update
        Version partialUpdatedVersion = new Version();
        partialUpdatedVersion.setId(version.getId());

        partialUpdatedVersion.version(UPDATED_VERSION);

        restVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVersion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVersion))
            )
            .andExpect(status().isOk());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
        Version testVersion = versionList.get(versionList.size() - 1);
        assertThat(testVersion.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void patchNonExistingVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();
        version.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, version.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(version))
            )
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();
        version.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(version))
            )
            .andExpect(status().isBadRequest());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVersion() throws Exception {
        int databaseSizeBeforeUpdate = versionRepository.findAll().size();
        version.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(version)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Version in the database
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVersion() throws Exception {
        // Initialize the database
        versionRepository.saveAndFlush(version);

        int databaseSizeBeforeDelete = versionRepository.findAll().size();

        // Delete the version
        restVersionMockMvc
            .perform(delete(ENTITY_API_URL_ID, version.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Version> versionList = versionRepository.findAll();
        assertThat(versionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
