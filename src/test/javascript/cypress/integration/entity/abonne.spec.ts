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

describe('Abonne e2e test', () => {
  const abonnePageUrl = '/abonne';
  const abonnePageUrlPattern = new RegExp('/abonne(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const abonneSample = {};

  let abonne: any;
  //let user: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"Multi-layered markets","firstName":"Johnathon","lastName":"Hettinger"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/abonnes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/abonnes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/abonnes/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/commentaires', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/conversations', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/messages', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/clubs', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/joueurs', {
      statusCode: 200,
      body: [],
    });

  });
   */

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

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('Abonnes menu should load Abonnes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('abonne');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Abonne').should('exist');
    cy.url().should('match', abonnePageUrlPattern);
  });

  describe('Abonne page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(abonnePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Abonne page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/abonne/new$'));
        cy.getEntityCreateUpdateHeading('Abonne');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', abonnePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/abonnes',
          body: {
            ...abonneSample,
            user: user,
          },
        }).then(({ body }) => {
          abonne = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/abonnes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [abonne],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(abonnePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(abonnePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Abonne page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('abonne');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', abonnePageUrlPattern);
      });

      it('edit button click should load edit Abonne page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Abonne');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', abonnePageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Abonne', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('abonne').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', abonnePageUrlPattern);

        abonne = undefined;
      });
    });
  });

  describe('new Abonne page', () => {
    beforeEach(() => {
      cy.visit(`${abonnePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Abonne');
    });

    it.skip('should create an instance of Abonne', () => {
      cy.get(`[data-cy="nom"]`).type('Clothing').should('have.value', 'Clothing');

      cy.setFieldImageAsBytesOfEntity('avatar', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="premium"]`).should('not.be.checked');
      cy.get(`[data-cy="premium"]`).click().should('be.checked');

      cy.get(`[data-cy="user"]`).select(1);

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        abonne = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', abonnePageUrlPattern);
    });
  });
});
