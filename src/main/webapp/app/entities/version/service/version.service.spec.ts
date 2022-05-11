import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IVersion, Version } from '../version.model';

import { VersionService } from './version.service';

describe('Version Service', () => {
  let service: VersionService;
  let httpMock: HttpTestingController;
  let elemDefault: IVersion;
  let expectedResult: IVersion | IVersion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VersionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      version: 'AAAAAAA',
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

    it('should create a Version', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Version()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Version', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          version: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Version', () => {
      const patchObject = Object.assign(
        {
          version: 'BBBBBB',
        },
        new Version()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Version', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          version: 'BBBBBB',
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

    it('should delete a Version', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVersionToCollectionIfMissing', () => {
      it('should add a Version to an empty array', () => {
        const version: IVersion = { id: 123 };
        expectedResult = service.addVersionToCollectionIfMissing([], version);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(version);
      });

      it('should not add a Version to an array that contains it', () => {
        const version: IVersion = { id: 123 };
        const versionCollection: IVersion[] = [
          {
            ...version,
          },
          { id: 456 },
        ];
        expectedResult = service.addVersionToCollectionIfMissing(versionCollection, version);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Version to an array that doesn't contain it", () => {
        const version: IVersion = { id: 123 };
        const versionCollection: IVersion[] = [{ id: 456 }];
        expectedResult = service.addVersionToCollectionIfMissing(versionCollection, version);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(version);
      });

      it('should add only unique Version to an array', () => {
        const versionArray: IVersion[] = [{ id: 123 }, { id: 456 }, { id: 54531 }];
        const versionCollection: IVersion[] = [{ id: 123 }];
        expectedResult = service.addVersionToCollectionIfMissing(versionCollection, ...versionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const version: IVersion = { id: 123 };
        const version2: IVersion = { id: 456 };
        expectedResult = service.addVersionToCollectionIfMissing([], version, version2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(version);
        expect(expectedResult).toContain(version2);
      });

      it('should accept null and undefined values', () => {
        const version: IVersion = { id: 123 };
        expectedResult = service.addVersionToCollectionIfMissing([], null, version, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(version);
      });

      it('should return initial array if no Version is added', () => {
        const versionCollection: IVersion[] = [{ id: 123 }];
        expectedResult = service.addVersionToCollectionIfMissing(versionCollection, undefined, null);
        expect(expectedResult).toEqual(versionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
