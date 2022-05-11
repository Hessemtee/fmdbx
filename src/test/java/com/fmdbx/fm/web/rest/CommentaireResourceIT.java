package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Abonne;
import com.fmdbx.fm.domain.Commentaire;
import com.fmdbx.fm.repository.CommentaireRepository;
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

/**
 * Integration tests for the {@link CommentaireResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CommentaireResourceIT {

    private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VISIBLE = false;
    private static final Boolean UPDATED_VISIBLE = true;

    private static final String ENTITY_API_URL = "/api/commentaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Mock
    private CommentaireRepository commentaireRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommentaireMockMvc;

    private Commentaire commentaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commentaire createEntity(EntityManager em) {
        Commentaire commentaire = new Commentaire().contenu(DEFAULT_CONTENU).visible(DEFAULT_VISIBLE);
        // Add required entity
        Abonne abonne;
        if (TestUtil.findAll(em, Abonne.class).isEmpty()) {
            abonne = AbonneResourceIT.createEntity(em);
            em.persist(abonne);
            em.flush();
        } else {
            abonne = TestUtil.findAll(em, Abonne.class).get(0);
        }
        commentaire.setAbonne(abonne);
        return commentaire;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commentaire createUpdatedEntity(EntityManager em) {
        Commentaire commentaire = new Commentaire().contenu(UPDATED_CONTENU).visible(UPDATED_VISIBLE);
        // Add required entity
        Abonne abonne;
        if (TestUtil.findAll(em, Abonne.class).isEmpty()) {
            abonne = AbonneResourceIT.createUpdatedEntity(em);
            em.persist(abonne);
            em.flush();
        } else {
            abonne = TestUtil.findAll(em, Abonne.class).get(0);
        }
        commentaire.setAbonne(abonne);
        return commentaire;
    }

    @BeforeEach
    public void initTest() {
        commentaire = createEntity(em);
    }

    @Test
    @Transactional
    void createCommentaire() throws Exception {
        int databaseSizeBeforeCreate = commentaireRepository.findAll().size();
        // Create the Commentaire
        restCommentaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentaire)))
            .andExpect(status().isCreated());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeCreate + 1);
        Commentaire testCommentaire = commentaireList.get(commentaireList.size() - 1);
        assertThat(testCommentaire.getContenu()).isEqualTo(DEFAULT_CONTENU);
        assertThat(testCommentaire.getVisible()).isEqualTo(DEFAULT_VISIBLE);
    }

    @Test
    @Transactional
    void createCommentaireWithExistingId() throws Exception {
        // Create the Commentaire with an existing ID
        commentaire.setId(1L);

        int databaseSizeBeforeCreate = commentaireRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentaire)))
            .andExpect(status().isBadRequest());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommentaires() throws Exception {
        // Initialize the database
        commentaireRepository.saveAndFlush(commentaire);

        // Get all the commentaireList
        restCommentaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commentaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)))
            .andExpect(jsonPath("$.[*].visible").value(hasItem(DEFAULT_VISIBLE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCommentairesWithEagerRelationshipsIsEnabled() throws Exception {
        when(commentaireRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCommentaireMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(commentaireRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCommentairesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(commentaireRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCommentaireMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(commentaireRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getCommentaire() throws Exception {
        // Initialize the database
        commentaireRepository.saveAndFlush(commentaire);

        // Get the commentaire
        restCommentaireMockMvc
            .perform(get(ENTITY_API_URL_ID, commentaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commentaire.getId().intValue()))
            .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU))
            .andExpect(jsonPath("$.visible").value(DEFAULT_VISIBLE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingCommentaire() throws Exception {
        // Get the commentaire
        restCommentaireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCommentaire() throws Exception {
        // Initialize the database
        commentaireRepository.saveAndFlush(commentaire);

        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();

        // Update the commentaire
        Commentaire updatedCommentaire = commentaireRepository.findById(commentaire.getId()).get();
        // Disconnect from session so that the updates on updatedCommentaire are not directly saved in db
        em.detach(updatedCommentaire);
        updatedCommentaire.contenu(UPDATED_CONTENU).visible(UPDATED_VISIBLE);

        restCommentaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCommentaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCommentaire))
            )
            .andExpect(status().isOk());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
        Commentaire testCommentaire = commentaireList.get(commentaireList.size() - 1);
        assertThat(testCommentaire.getContenu()).isEqualTo(UPDATED_CONTENU);
        assertThat(testCommentaire.getVisible()).isEqualTo(UPDATED_VISIBLE);
    }

    @Test
    @Transactional
    void putNonExistingCommentaire() throws Exception {
        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();
        commentaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commentaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commentaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommentaire() throws Exception {
        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();
        commentaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commentaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommentaire() throws Exception {
        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();
        commentaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentaireMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentaire)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommentaireWithPatch() throws Exception {
        // Initialize the database
        commentaireRepository.saveAndFlush(commentaire);

        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();

        // Update the commentaire using partial update
        Commentaire partialUpdatedCommentaire = new Commentaire();
        partialUpdatedCommentaire.setId(commentaire.getId());

        partialUpdatedCommentaire.visible(UPDATED_VISIBLE);

        restCommentaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommentaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommentaire))
            )
            .andExpect(status().isOk());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
        Commentaire testCommentaire = commentaireList.get(commentaireList.size() - 1);
        assertThat(testCommentaire.getContenu()).isEqualTo(DEFAULT_CONTENU);
        assertThat(testCommentaire.getVisible()).isEqualTo(UPDATED_VISIBLE);
    }

    @Test
    @Transactional
    void fullUpdateCommentaireWithPatch() throws Exception {
        // Initialize the database
        commentaireRepository.saveAndFlush(commentaire);

        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();

        // Update the commentaire using partial update
        Commentaire partialUpdatedCommentaire = new Commentaire();
        partialUpdatedCommentaire.setId(commentaire.getId());

        partialUpdatedCommentaire.contenu(UPDATED_CONTENU).visible(UPDATED_VISIBLE);

        restCommentaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommentaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommentaire))
            )
            .andExpect(status().isOk());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
        Commentaire testCommentaire = commentaireList.get(commentaireList.size() - 1);
        assertThat(testCommentaire.getContenu()).isEqualTo(UPDATED_CONTENU);
        assertThat(testCommentaire.getVisible()).isEqualTo(UPDATED_VISIBLE);
    }

    @Test
    @Transactional
    void patchNonExistingCommentaire() throws Exception {
        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();
        commentaire.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commentaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commentaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommentaire() throws Exception {
        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();
        commentaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commentaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommentaire() throws Exception {
        int databaseSizeBeforeUpdate = commentaireRepository.findAll().size();
        commentaire.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentaireMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(commentaire))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commentaire in the database
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommentaire() throws Exception {
        // Initialize the database
        commentaireRepository.saveAndFlush(commentaire);

        int databaseSizeBeforeDelete = commentaireRepository.findAll().size();

        // Delete the commentaire
        restCommentaireMockMvc
            .perform(delete(ENTITY_API_URL_ID, commentaire.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Commentaire> commentaireList = commentaireRepository.findAll();
        assertThat(commentaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
