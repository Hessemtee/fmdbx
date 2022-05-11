import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IJeu, Jeu } from '../jeu.model';

import { JeuService } from './jeu.service';

describe('Jeu Service', () => {
  let service: JeuService;
  let httpMock: HttpTestingController;
  let elemDefault: IJeu;
  let expectedResult: IJeu | IJeu[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JeuService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Jeu', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Jeu()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Jeu', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Jeu', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
        },
        new Jeu()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Jeu', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Jeu', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addJeuToCollectionIfMissing', () => {
      it('should add a Jeu to an empty array', () => {
        const jeu: IJeu = { id: 123 };
        expectedResult = service.addJeuToCollectionIfMissing([], jeu);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jeu);
      });

      it('should not add a Jeu to an array that contains it', () => {
        const jeu: IJeu = { id: 123 };
        const jeuCollection: IJeu[] = [
          {
            ...jeu,
          },
          { id: 456 },
        ];
        expectedResult = service.addJeuToCollectionIfMissing(jeuCollection, jeu);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Jeu to an array that doesn't contain it", () => {
        const jeu: IJeu = { id: 123 };
        const jeuCollection: IJeu[] = [{ id: 456 }];
        expectedResult = service.addJeuToCollectionIfMissing(jeuCollection, jeu);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jeu);
      });

      it('should add only unique Jeu to an array', () => {
        const jeuArray: IJeu[] = [{ id: 123 }, { id: 456 }, { id: 30345 }];
        const jeuCollection: IJeu[] = [{ id: 123 }];
        expectedResult = service.addJeuToCollectionIfMissing(jeuCollection, ...jeuArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const jeu: IJeu = { id: 123 };
        const jeu2: IJeu = { id: 456 };
        expectedResult = service.addJeuToCollectionIfMissing([], jeu, jeu2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jeu);
        expect(expectedResult).toContain(jeu2);
      });

      it('should accept null and undefined values', () => {
        const jeu: IJeu = { id: 123 };
        expectedResult = service.addJeuToCollectionIfMissing([], null, jeu, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jeu);
      });

      it('should return initial array if no Jeu is added', () => {
        const jeuCollection: IJeu[] = [{ id: 123 }];
        expectedResult = service.addJeuToCollectionIfMissing(jeuCollection, undefined, null);
        expect(expectedResult).toEqual(jeuCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
