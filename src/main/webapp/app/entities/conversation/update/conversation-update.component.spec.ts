import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ConversationService } from '../service/conversation.service';
import { IConversation, Conversation } from '../conversation.model';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

import { ConversationUpdateComponent } from './conversation-update.component';

describe('Conversation Management Update Component', () => {
  let comp: ConversationUpdateComponent;
  let fixture: ComponentFixture<ConversationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let conversationService: ConversationService;
  let abonneService: AbonneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ConversationUpdateComponent],
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
      .overrideTemplate(ConversationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConversationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conversationService = TestBed.inject(ConversationService);
    abonneService = TestBed.inject(AbonneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Abonne query and add missing value', () => {
      const conversation: IConversation = { id: 456 };
      const emetteur: IAbonne = { id: 7261 };
      conversation.emetteur = emetteur;
      const recepteur: IAbonne = { id: 16464 };
      conversation.recepteur = recepteur;

      const abonneCollection: IAbonne[] = [{ id: 64323 }];
      jest.spyOn(abonneService, 'query').mockReturnValue(of(new HttpResponse({ body: abonneCollection })));
      const additionalAbonnes = [emetteur, recepteur];
      const expectedCollection: IAbonne[] = [...additionalAbonnes, ...abonneCollection];
      jest.spyOn(abonneService, 'addAbonneToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ conversation });
      comp.ngOnInit();

      expect(abonneService.query).toHaveBeenCalled();
      expect(abonneService.addAbonneToCollectionIfMissing).toHaveBeenCalledWith(abonneCollection, ...additionalAbonnes);
      expect(comp.abonnesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const conversation: IConversation = { id: 456 };
      const emetteur: IAbonne = { id: 673 };
      conversation.emetteur = emetteur;
      const recepteur: IAbonne = { id: 35061 };
      conversation.recepteur = recepteur;

      activatedRoute.data = of({ conversation });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(conversation));
      expect(comp.abonnesSharedCollection).toContain(emetteur);
      expect(comp.abonnesSharedCollection).toContain(recepteur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conversation>>();
      const conversation = { id: 123 };
      jest.spyOn(conversationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conversation }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(conversationService.update).toHaveBeenCalledWith(conversation);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conversation>>();
      const conversation = new Conversation();
      jest.spyOn(conversationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conversation }));
      saveSubject.complete();

      // THEN
      expect(conversationService.create).toHaveBeenCalledWith(conversation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Conversation>>();
      const conversation = { id: 123 };
      jest.spyOn(conversationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conversationService.update).toHaveBeenCalledWith(conversation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackAbonneById', () => {
      it('Should return tracked Abonne primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackAbonneById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
