import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JoueurService } from '../service/joueur.service';
import { IJoueur, Joueur } from '../joueur.model';
import { IClub } from 'app/entities/club/club.model';
import { ClubService } from 'app/entities/club/service/club.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

import { JoueurUpdateComponent } from './joueur-update.component';

describe('Joueur Management Update Component', () => {
  let comp: JoueurUpdateComponent;
  let fixture: ComponentFixture<JoueurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let joueurService: JoueurService;
  let clubService: ClubService;
  let abonneService: AbonneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [JoueurUpdateComponent],
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
      .overrideTemplate(JoueurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JoueurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    joueurService = TestBed.inject(JoueurService);
    clubService = TestBed.inject(ClubService);
    abonneService = TestBed.inject(AbonneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Club query and add missing value', () => {
      const joueur: IJoueur = { id: 456 };
      const club: IClub = { id: 58045 };
      joueur.club = club;

      const clubCollection: IClub[] = [{ id: 16762 }];
      jest.spyOn(clubService, 'query').mockReturnValue(of(new HttpResponse({ body: clubCollection })));
      const additionalClubs = [club];
      const expectedCollection: IClub[] = [...additionalClubs, ...clubCollection];
      jest.spyOn(clubService, 'addClubToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(clubService.query).toHaveBeenCalled();
      expect(clubService.addClubToCollectionIfMissing).toHaveBeenCalledWith(clubCollection, ...additionalClubs);
      expect(comp.clubsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Abonne query and add missing value', () => {
      const joueur: IJoueur = { id: 456 };
      const favorises: IAbonne[] = [{ id: 60128 }];
      joueur.favorises = favorises;

      const abonneCollection: IAbonne[] = [{ id: 80771 }];
      jest.spyOn(abonneService, 'query').mockReturnValue(of(new HttpResponse({ body: abonneCollection })));
      const additionalAbonnes = [...favorises];
      const expectedCollection: IAbonne[] = [...additionalAbonnes, ...abonneCollection];
      jest.spyOn(abonneService, 'addAbonneToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(abonneService.query).toHaveBeenCalled();
      expect(abonneService.addAbonneToCollectionIfMissing).toHaveBeenCalledWith(abonneCollection, ...additionalAbonnes);
      expect(comp.abonnesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const joueur: IJoueur = { id: 456 };
      const club: IClub = { id: 71018 };
      joueur.club = club;
      const favorises: IAbonne = { id: 82886 };
      joueur.favorises = [favorises];

      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(joueur));
      expect(comp.clubsSharedCollection).toContain(club);
      expect(comp.abonnesSharedCollection).toContain(favorises);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Joueur>>();
      const joueur = { id: 123 };
      jest.spyOn(joueurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: joueur }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(joueurService.update).toHaveBeenCalledWith(joueur);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Joueur>>();
      const joueur = new Joueur();
      jest.spyOn(joueurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: joueur }));
      saveSubject.complete();

      // THEN
      expect(joueurService.create).toHaveBeenCalledWith(joueur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Joueur>>();
      const joueur = { id: 123 };
      jest.spyOn(joueurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ joueur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(joueurService.update).toHaveBeenCalledWith(joueur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
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

  describe('Getting selected relationships', () => {
    describe('getSelectedAbonne', () => {
      it('Should return option if no Abonne is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedAbonne(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Abonne for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedAbonne(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Abonne is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedAbonne(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
