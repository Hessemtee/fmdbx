import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MessageService } from '../service/message.service';
import { IMessage, Message } from '../message.model';
import { IConversation } from 'app/entities/conversation/conversation.model';
import { ConversationService } from 'app/entities/conversation/service/conversation.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

import { MessageUpdateComponent } from './message-update.component';

describe('Message Management Update Component', () => {
  let comp: MessageUpdateComponent;
  let fixture: ComponentFixture<MessageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let messageService: MessageService;
  let conversationService: ConversationService;
  let abonneService: AbonneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MessageUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(MessageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MessageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    messageService = TestBed.inject(MessageService);
    conversationService = TestBed.inject(ConversationService);
    abonneService = TestBed.inject(AbonneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Conversation query and add missing value', () => {
      const message: IMessage = { id: 456 };
      const conversation: IConversation = { id: 86247 };
      message.conversation = conversation;

      const conversationCollection: IConversation[] = [{ id: 10966 }];
      jest.spyOn(conversationService, 'query').mockReturnValue(of(new HttpResponse({ body: conversationCollection })));
      const additionalConversations = [conversation];
      const expectedCollection: IConversation[] = [...additionalConversations, ...conversationCollection];
      jest.spyOn(conversationService, 'addConversationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ message });
      comp.ngOnInit();

      expect(conversationService.query).toHaveBeenCalled();
      expect(conversationService.addConversationToCollectionIfMissing).toHaveBeenCalledWith(
        conversationCollection,
        ...additionalConversations
      );
      expect(comp.conversationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Abonne query and add missing value', () => {
      const message: IMessage = { id: 456 };
      const redacteur: IAbonne = { id: 9914 };
      message.redacteur = redacteur;

      const abonneCollection: IAbonne[] = [{ id: 96605 }];
      jest.spyOn(abonneService, 'query').mockReturnValue(of(new HttpResponse({ body: abonneCollection })));
      const additionalAbonnes = [redacteur];
      const expectedCollection: IAbonne[] = [...additionalAbonnes, ...abonneCollection];
      jest.spyOn(abonneService, 'addAbonneToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ message });
      comp.ngOnInit();

      expect(abonneService.query).toHaveBeenCalled();
      expect(abonneService.addAbonneToCollectionIfMissing).toHaveBeenCalledWith(abonneCollection, ...additionalAbonnes);
      expect(comp.abonnesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const message: IMessage = { id: 456 };
      const conversation: IConversation = { id: 66805 };
      message.conversation = conversation;
      const redacteur: IAbonne = { id: 33727 };
      message.redacteur = redacteur;

      activatedRoute.data = of({ message });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(message));
      expect(comp.conversationsSharedCollection).toContain(conversation);
      expect(comp.abonnesSharedCollection).toContain(redacteur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Message>>();
      const message = { id: 123 };
      jest.spyOn(messageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ message });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: message }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(messageService.update).toHaveBeenCalledWith(message);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Message>>();
      const message = new Message();
      jest.spyOn(messageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ message });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: message }));
      saveSubject.complete();

      // THEN
      expect(messageService.create).toHaveBeenCalledWith(message);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Message>>();
      const message = { id: 123 };
      jest.spyOn(messageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ message });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(messageService.update).toHaveBeenCalledWith(message);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackConversationById', () => {
      it('Should return tracked Conversation primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackConversationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackAbonneById', () => {
      it('Should return tracked Abonne primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackAbonneById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
