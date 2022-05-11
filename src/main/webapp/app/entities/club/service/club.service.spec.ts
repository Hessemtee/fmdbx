import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClub, Club } from '../club.model';

import { ClubService } from './club.service';

describe('Club Service', () => {
  let service: ClubService;
  let httpMock: HttpTestingController;
  let elemDefault: IClub;
  let expectedResult: IClub | IClub[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClubService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      logoContentType: 'image/png',
      logo: 'AAAAAAA',
      ville: 'AAAAAAA',
      balance: 0,
      masseSalariale: 0,
      budgetSalaires: 0,
      budgetTransferts: 0,
      infrastructuresEntrainement: 'AAAAAAA',
      infrastructuresJeunes: 'AAAAAAA',
      recrutementJeunes: 'AAAAAAA',
      nomStade: 'AAAAAAA',
      capaciteStade: 0,
      previsionMedia: 0,
      indiceContinental: 0,
      competitionContinentale: false,
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

    it('should create a Club', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Club()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Club', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          logo: 'BBBBBB',
          ville: 'BBBBBB',
          balance: 1,
          masseSalariale: 1,
          budgetSalaires: 1,
          budgetTransferts: 1,
          infrastructuresEntrainement: 'BBBBBB',
          infrastructuresJeunes: 'BBBBBB',
          recrutementJeunes: 'BBBBBB',
          nomStade: 'BBBBBB',
          capaciteStade: 1,
          previsionMedia: 1,
          indiceContinental: 1,
          competitionContinentale: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Club', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
          logo: 'BBBBBB',
          ville: 'BBBBBB',
          budgetSalaires: 1,
          budgetTransferts: 1,
          infrastructuresEntrainement: 'BBBBBB',
          previsionMedia: 1,
          indiceContinental: 1,
          competitionContinentale: true,
        },
        new Club()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Club', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          logo: 'BBBBBB',
          ville: 'BBBBBB',
          balance: 1,
          masseSalariale: 1,
          budgetSalaires: 1,
          budgetTransferts: 1,
          infrastructuresEntrainement: 'BBBBBB',
          infrastructuresJeunes: 'BBBBBB',
          recrutementJeunes: 'BBBBBB',
          nomStade: 'BBBBBB',
          capaciteStade: 1,
          previsionMedia: 1,
          indiceContinental: 1,
          competitionContinentale: true,
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

    it('should delete a Club', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addClubToCollectionIfMissing', () => {
      it('should add a Club to an empty array', () => {
        const club: IClub = { id: 123 };
        expectedResult = service.addClubToCollectionIfMissing([], club);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(club);
      });

      it('should not add a Club to an array that contains it', () => {
        const club: IClub = { id: 123 };
        const clubCollection: IClub[] = [
          {
            ...club,
          },
          { id: 456 },
        ];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, club);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Club to an array that doesn't contain it", () => {
        const club: IClub = { id: 123 };
        const clubCollection: IClub[] = [{ id: 456 }];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, club);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(club);
      });

      it('should add only unique Club to an array', () => {
        const clubArray: IClub[] = [{ id: 123 }, { id: 456 }, { id: 14668 }];
        const clubCollection: IClub[] = [{ id: 123 }];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, ...clubArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const club: IClub = { id: 123 };
        const club2: IClub = { id: 456 };
        expectedResult = service.addClubToCollectionIfMissing([], club, club2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(club);
        expect(expectedResult).toContain(club2);
      });

      it('should accept null and undefined values', () => {
        const club: IClub = { id: 123 };
        expectedResult = service.addClubToCollectionIfMissing([], null, club, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(club);
      });

      it('should return initial array if no Club is added', () => {
        const clubCollection: IClub[] = [{ id: 123 }];
        expectedResult = service.addClubToCollectionIfMissing(clubCollection, undefined, null);
        expect(expectedResult).toEqual(clubCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
