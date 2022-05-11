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

describe('Championnat e2e test', () => {
  const championnatPageUrl = '/championnat';
  const championnatPageUrlPattern = new RegExp('/championnat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const championnatSample = {};

  let championnat: any;
  let pays: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/pays',
      body: {
        nom: 'withdrawal',
        drapeau: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
        drapeauContentType: 'unknown',
        confederation: 'intranet COM',
        indiceConf: 38436,
        rankingFifa: 19100,
        anneesAvantNationalite: 98143,
        importanceEnJeu: 33624,
      },
    }).then(({ body }) => {
      pays = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/championnats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/championnats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/championnats/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/pays', {
      statusCode: 200,
      body: [pays],
    });
  });

  afterEach(() => {
    if (championnat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/championnats/${championnat.id}`,
      }).then(() => {
        championnat = undefined;
      });
    }
  });

  afterEach(() => {
    if (pays) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/pays/${pays.id}`,
      }).then(() => {
        pays = undefined;
      });
    }
  });

  it('Championnats menu should load Championnats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('championnat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Championnat').should('exist');
    cy.url().should('match', championnatPageUrlPattern);
  });

  describe('Championnat page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(championnatPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Championnat page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/championnat/new$'));
        cy.getEntityCreateUpdateHeading('Championnat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', championnatPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/championnats',
          body: {
            ...championnatSample,
            pays: pays,
          },
        }).then(({ body }) => {
          championnat = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/championnats+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [championnat],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(championnatPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Championnat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('championnat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', championnatPageUrlPattern);
      });

      it('edit button click should load edit Championnat page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Championnat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', championnatPageUrlPattern);
      });

      it('last delete button click should delete instance of Championnat', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('championnat').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', championnatPageUrlPattern);

        championnat = undefined;
      });
    });
  });

  describe('new Championnat page', () => {
    beforeEach(() => {
      cy.visit(`${championnatPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Championnat');
    });

    it('should create an instance of Championnat', () => {
      cy.get(`[data-cy="nom"]`).type('scale foreground matrix').should('have.value', 'scale foreground matrix');

      cy.get(`[data-cy="nombreDEquipes"]`).type('9192').should('have.value', '9192');

      cy.setFieldImageAsBytesOfEntity('logo', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="niveau"]`).type('12880').should('have.value', '12880');

      cy.get(`[data-cy="reputation"]`).type('76410').should('have.value', '76410');

      cy.get(`[data-cy="pays"]`).select(1);

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        championnat = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', championnatPageUrlPattern);
    });
  });
});
