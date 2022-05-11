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

describe('Jeu e2e test', () => {
  const jeuPageUrl = '/jeu';
  const jeuPageUrlPattern = new RegExp('/jeu(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const jeuSample = { nom: 'e-tailers Investor' };

  let jeu: any;
  let version: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/versions',
      body: { version: 'plum Gloves redundant' },
    }).then(({ body }) => {
      version = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/jeus+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/jeus').as('postEntityRequest');
    cy.intercept('DELETE', '/api/jeus/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/versions', {
      statusCode: 200,
      body: [version],
    });

    cy.intercept('GET', '/api/clubs', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (jeu) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/jeus/${jeu.id}`,
      }).then(() => {
        jeu = undefined;
      });
    }
  });

  afterEach(() => {
    if (version) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/versions/${version.id}`,
      }).then(() => {
        version = undefined;
      });
    }
  });

  it('Jeus menu should load Jeus page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('jeu');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Jeu').should('exist');
    cy.url().should('match', jeuPageUrlPattern);
  });

  describe('Jeu page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(jeuPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Jeu page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/jeu/new$'));
        cy.getEntityCreateUpdateHeading('Jeu');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', jeuPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/jeus',
          body: {
            ...jeuSample,
            versions: version,
          },
        }).then(({ body }) => {
          jeu = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/jeus+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [jeu],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(jeuPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Jeu page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('jeu');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', jeuPageUrlPattern);
      });

      it('edit button click should load edit Jeu page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Jeu');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', jeuPageUrlPattern);
      });

      it('last delete button click should delete instance of Jeu', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('jeu').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', jeuPageUrlPattern);

        jeu = undefined;
      });
    });
  });

  describe('new Jeu page', () => {
    beforeEach(() => {
      cy.visit(`${jeuPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Jeu');
    });

    it('should create an instance of Jeu', () => {
      cy.get(`[data-cy="nom"]`).type('Borders invoice').should('have.value', 'Borders invoice');

      cy.get(`[data-cy="versions"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        jeu = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', jeuPageUrlPattern);
    });
  });
});
