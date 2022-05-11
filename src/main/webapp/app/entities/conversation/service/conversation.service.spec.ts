import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IConversation, Conversation } from '../conversation.model';

import { ConversationService } from './conversation.service';

describe('Conversation Service', () => {
  let service: ConversationService;
  let httpMock: HttpTestingController;
  let elemDefault: IConversation;
  let expectedResult: IConversation | IConversation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ConversationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      objet: 'AAAAAAA',
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

    it('should create a Conversation', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Conversation()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Conversation', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          objet: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Conversation', () => {
      const patchObject = Object.assign(
        {
          objet: 'BBBBBB',
        },
        new Conversation()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Conversation', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          objet: 'BBBBBB',
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

    it('should delete a Conversation', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addConversationToCollectionIfMissing', () => {
      it('should add a Conversation to an empty array', () => {
        const conversation: IConversation = { id: 123 };
        expectedResult = service.addConversationToCollectionIfMissing([], conversation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conversation);
      });

      it('should not add a Conversation to an array that contains it', () => {
        const conversation: IConversation = { id: 123 };
        const conversationCollection: IConversation[] = [
          {
            ...conversation,
          },
          { id: 456 },
        ];
        expectedResult = service.addConversationToCollectionIfMissing(conversationCollection, conversation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Conversation to an array that doesn't contain it", () => {
        const conversation: IConversation = { id: 123 };
        const conversationCollection: IConversation[] = [{ id: 456 }];
        expectedResult = service.addConversationToCollectionIfMissing(conversationCollection, conversation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conversation);
      });

      it('should add only unique Conversation to an array', () => {
        const conversationArray: IConversation[] = [{ id: 123 }, { id: 456 }, { id: 93933 }];
        const conversationCollection: IConversation[] = [{ id: 123 }];
        expectedResult = service.addConversationToCollectionIfMissing(conversationCollection, ...conversationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conversation: IConversation = { id: 123 };
        const conversation2: IConversation = { id: 456 };
        expectedResult = service.addConversationToCollectionIfMissing([], conversation, conversation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conversation);
        expect(expectedResult).toContain(conversation2);
      });

      it('should accept null and undefined values', () => {
        const conversation: IConversation = { id: 123 };
        expectedResult = service.addConversationToCollectionIfMissing([], null, conversation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conversation);
      });

      it('should return initial array if no Conversation is added', () => {
        const conversationCollection: IConversation[] = [{ id: 123 }];
        expectedResult = service.addConversationToCollectionIfMissing(conversationCollection, undefined, null);
        expect(expectedResult).toEqual(conversationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
