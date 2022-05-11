import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CommentaireService } from '../service/commentaire.service';
import { ICommentaire, Commentaire } from '../commentaire.model';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { JoueurService } from 'app/entities/joueur/service/joueur.service';
import { IClub } from 'app/entities/club/club.model';
import { ClubService } from 'app/entities/club/service/club.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

import { CommentaireUpdateComponent } from './commentaire-update.component';

describe('Commentaire Management Update Component', () => {
  let comp: CommentaireUpdateComponent;
  let fixture: ComponentFixture<CommentaireUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let commentaireService: CommentaireService;
  let joueurService: JoueurService;
  let clubService: ClubService;
  let abonneService: AbonneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CommentaireUpdateComponent],
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
      .overrideTemplate(CommentaireUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CommentaireUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commentaireService = TestBed.inject(CommentaireService);
    joueurService = TestBed.inject(JoueurService);
    clubService = TestBed.inject(ClubService);
    abonneService = TestBed.inject(AbonneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Joueur query and add missing value', () => {
      const commentaire: ICommentaire = { id: 456 };
      const joueurConcerne: IJoueur = { id: 71938 };
      commentaire.joueurConcerne = joueurConcerne;

      const joueurCollection: IJoueur[] = [{ id: 10625 }];
      jest.spyOn(joueurService, 'query').mockReturnValue(of(new HttpResponse({ body: joueurCollection })));
      const additionalJoueurs = [joueurConcerne];
      const expectedCollection: IJoueur[] = [...additionalJoueurs, ...joueurCollection];
      jest.spyOn(joueurService, 'addJoueurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      expect(joueurService.query).toHaveBeenCalled();
      expect(joueurService.addJoueurToCollectionIfMissing).toHaveBeenCalledWith(joueurCollection, ...additionalJoueurs);
      expect(comp.joueursSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Club query and add missing value', () => {
      const commentaire: ICommentaire = { id: 456 };
      const clubConcerne: IClub = { id: 9441 };
      commentaire.clubConcerne = clubConcerne;

      const clubCollection: IClub[] = [{ id: 35328 }];
      jest.spyOn(clubService, 'query').mockReturnValue(of(new HttpResponse({ body: clubCollection })));
      const additionalClubs = [clubConcerne];
      const expectedCollection: IClub[] = [...additionalClubs, ...clubCollection];
      jest.spyOn(clubService, 'addClubToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      expect(clubService.query).toHaveBeenCalled();
      expect(clubService.addClubToCollectionIfMissing).toHaveBeenCalledWith(clubCollection, ...additionalClubs);
      expect(comp.clubsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Abonne query and add missing value', () => {
      const commentaire: ICommentaire = { id: 456 };
      const abonne: IAbonne = { id: 59360 };
      commentaire.abonne = abonne;

      const abonneCollection: IAbonne[] = [{ id: 87939 }];
      jest.spyOn(abonneService, 'query').mockReturnValue(of(new HttpResponse({ body: abonneCollection })));
      const additionalAbonnes = [abonne];
      const expectedCollection: IAbonne[] = [...additionalAbonnes, ...abonneCollection];
      jest.spyOn(abonneService, 'addAbonneToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      expect(abonneService.query).toHaveBeenCalled();
      expect(abonneService.addAbonneToCollectionIfMissing).toHaveBeenCalledWith(abonneCollection, ...additionalAbonnes);
      expect(comp.abonnesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const commentaire: ICommentaire = { id: 456 };
      const joueurConcerne: IJoueur = { id: 52392 };
      commentaire.joueurConcerne = joueurConcerne;
      const clubConcerne: IClub = { id: 55323 };
      commentaire.clubConcerne = clubConcerne;
      const abonne: IAbonne = { id: 26263 };
      commentaire.abonne = abonne;

      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(commentaire));
      expect(comp.joueursSharedCollection).toContain(joueurConcerne);
      expect(comp.clubsSharedCollection).toContain(clubConcerne);
      expect(comp.abonnesSharedCollection).toContain(abonne);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Commentaire>>();
      const commentaire = { id: 123 };
      jest.spyOn(commentaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commentaire }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(commentaireService.update).toHaveBeenCalledWith(commentaire);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Commentaire>>();
      const commentaire = new Commentaire();
      jest.spyOn(commentaireService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: commentaire }));
      saveSubject.complete();

      // THEN
      expect(commentaireService.create).toHaveBeenCalledWith(commentaire);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Commentaire>>();
      const commentaire = { id: 123 };
      jest.spyOn(commentaireService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commentaire });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commentaireService.update).toHaveBeenCalledWith(commentaire);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackJoueurById', () => {
      it('Should return tracked Joueur primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackJoueurById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackClubById', () => {
      it('Should return tracked Club primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClubById(0, entity);
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
