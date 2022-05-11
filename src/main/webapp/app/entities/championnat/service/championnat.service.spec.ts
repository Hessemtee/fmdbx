import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IChampionnat, Championnat } from '../championnat.model';

import { ChampionnatService } from './championnat.service';

describe('Championnat Service', () => {
  let service: ChampionnatService;
  let httpMock: HttpTestingController;
  let elemDefault: IChampionnat;
  let expectedResult: IChampionnat | IChampionnat[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ChampionnatService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      nombreDEquipes: 0,
      logoContentType: 'image/png',
      logo: 'AAAAAAA',
      niveau: 0,
      reputation: 0,
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

    it('should create a Championnat', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Championnat()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Championnat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          nombreDEquipes: 1,
          logo: 'BBBBBB',
          niveau: 1,
          reputation: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Championnat', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
          logo: 'BBBBBB',
          reputation: 1,
        },
        new Championnat()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Championnat', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          nombreDEquipes: 1,
          logo: 'BBBBBB',
          niveau: 1,
          reputation: 1,
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

    it('should delete a Championnat', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addChampionnatToCollectionIfMissing', () => {
      it('should add a Championnat to an empty array', () => {
        const championnat: IChampionnat = { id: 123 };
        expectedResult = service.addChampionnatToCollectionIfMissing([], championnat);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(championnat);
      });

      it('should not add a Championnat to an array that contains it', () => {
        const championnat: IChampionnat = { id: 123 };
        const championnatCollection: IChampionnat[] = [
          {
            ...championnat,
          },
          { id: 456 },
        ];
        expectedResult = service.addChampionnatToCollectionIfMissing(championnatCollection, championnat);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Championnat to an array that doesn't contain it", () => {
        const championnat: IChampionnat = { id: 123 };
        const championnatCollection: IChampionnat[] = [{ id: 456 }];
        expectedResult = service.addChampionnatToCollectionIfMissing(championnatCollection, championnat);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(championnat);
      });

      it('should add only unique Championnat to an array', () => {
        const championnatArray: IChampionnat[] = [{ id: 123 }, { id: 456 }, { id: 11122 }];
        const championnatCollection: IChampionnat[] = [{ id: 123 }];
        expectedResult = service.addChampionnatToCollectionIfMissing(championnatCollection, ...championnatArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const championnat: IChampionnat = { id: 123 };
        const championnat2: IChampionnat = { id: 456 };
        expectedResult = service.addChampionnatToCollectionIfMissing([], championnat, championnat2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(championnat);
        expect(expectedResult).toContain(championnat2);
      });

      it('should accept null and undefined values', () => {
        const championnat: IChampionnat = { id: 123 };
        expectedResult = service.addChampionnatToCollectionIfMissing([], null, championnat, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(championnat);
      });

      it('should return initial array if no Championnat is added', () => {
        const championnatCollection: IChampionnat[] = [{ id: 123 }];
        expectedResult = service.addChampionnatToCollectionIfMissing(championnatCollection, undefined, null);
        expect(expectedResult).toEqual(championnatCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
