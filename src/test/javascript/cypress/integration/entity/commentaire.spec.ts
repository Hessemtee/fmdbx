import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Commentaire e2e test', () => {
  const commentairePageUrl = '/commentaire';
  const commentairePageUrlPattern = new RegExp('/commentaire(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const commentaireSample = {};

  let commentaire: any;
  //let abonne: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/abonnes',
      body: {"nom":"Arizona Checking","avatar":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","avatarContentType":"unknown","premium":true},
    }).then(({ body }) => {
      abonne = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/commentaires+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/commentaires').as('postEntityRequest');
    cy.intercept('DELETE', '/api/commentaires/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/joueurs', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/clubs', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/abonnes', {
      statusCode: 200,
      body: [abonne],
    });

  });
   */

  afterEach(() => {
    if (commentaire) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/commentaires/${commentaire.id}`,
      }).then(() => {
        commentaire = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (abonne) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/abonnes/${abonne.id}`,
      }).then(() => {
        abonne = undefined;
      });
    }
  });
   */

  it('Commentaires menu should load Commentaires page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('commentaire');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Commentaire').should('exist');
    cy.url().should('match', commentairePageUrlPattern);
  });

  describe('Commentaire page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(commentairePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Commentaire page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/commentaire/new$'));
        cy.getEntityCreateUpdateHeading('Commentaire');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', commentairePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/commentaires',
          body: {
            ...commentaireSample,
            abonne: abonne,
          },
        }).then(({ body }) => {
          commentaire = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/commentaires+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [commentaire],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(commentairePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(commentairePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Commentaire page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('commentaire');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', commentairePageUrlPattern);
      });

      it('edit button click should load edit Commentaire page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Commentaire');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', commentairePageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Commentaire', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('commentaire').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', commentairePageUrlPattern);

        commentaire = undefined;
      });
    });
  });

  describe('new Commentaire page', () => {
    beforeEach(() => {
      cy.visit(`${commentairePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Commentaire');
    });

    it.skip('should create an instance of Commentaire', () => {
      cy.get(`[data-cy="contenu"]`).type('Tasty').should('have.value', 'Tasty');

      cy.get(`[data-cy="visible"]`).should('not.be.checked');
      cy.get(`[data-cy="visible"]`).click().should('be.checked');

      cy.get(`[data-cy="abonne"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        commentaire = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', commentairePageUrlPattern);
    });
  });
});
