import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ClubService } from '../service/club.service';
import { IClub, Club } from '../club.model';
import { IChampionnat } from 'app/entities/championnat/championnat.model';
import { ChampionnatService } from 'app/entities/championnat/service/championnat.service';
import { IJeu } from 'app/entities/jeu/jeu.model';
import { JeuService } from 'app/entities/jeu/service/jeu.service';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { AbonneService } from 'app/entities/abonne/service/abonne.service';

import { ClubUpdateComponent } from './club-update.component';

describe('Club Management Update Component', () => {
  let comp: ClubUpdateComponent;
  let fixture: ComponentFixture<ClubUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clubService: ClubService;
  let championnatService: ChampionnatService;
  let jeuService: JeuService;
  let abonneService: AbonneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClubUpdateComponent],
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
      .overrideTemplate(ClubUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClubUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clubService = TestBed.inject(ClubService);
    championnatService = TestBed.inject(ChampionnatService);
    jeuService = TestBed.inject(JeuService);
    abonneService = TestBed.inject(AbonneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Championnat query and add missing value', () => {
      const club: IClub = { id: 456 };
      const championnat: IChampionnat = { id: 5448 };
      club.championnat = championnat;

      const championnatCollection: IChampionnat[] = [{ id: 7214 }];
      jest.spyOn(championnatService, 'query').mockReturnValue(of(new HttpResponse({ body: championnatCollection })));
      const additionalChampionnats = [championnat];
      const expectedCollection: IChampionnat[] = [...additionalChampionnats, ...championnatCollection];
      jest.spyOn(championnatService, 'addChampionnatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ club });
      comp.ngOnInit();

      expect(championnatService.query).toHaveBeenCalled();
      expect(championnatService.addChampionnatToCollectionIfMissing).toHaveBeenCalledWith(championnatCollection, ...additionalChampionnats);
      expect(comp.championnatsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Jeu query and add missing value', () => {
      const club: IClub = { id: 456 };
      const jeuxes: IJeu[] = [{ id: 16663 }];
      club.jeuxes = jeuxes;

      const jeuCollection: IJeu[] = [{ id: 19632 }];
      jest.spyOn(jeuService, 'query').mockReturnValue(of(new HttpResponse({ body: jeuCollection })));
      const additionalJeus = [...jeuxes];
      const expectedCollection: IJeu[] = [...additionalJeus, ...jeuCollection];
      jest.spyOn(jeuService, 'addJeuToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ club });
      comp.ngOnInit();

      expect(jeuService.query).toHaveBeenCalled();
      expect(jeuService.addJeuToCollectionIfMissing).toHaveBeenCalledWith(jeuCollection, ...additionalJeus);
      expect(comp.jeusSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Abonne query and add missing value', () => {
      const club: IClub = { id: 456 };
      const bookmarks: IAbonne[] = [{ id: 54878 }];
      club.bookmarks = bookmarks;

      const abonneCollection: IAbonne[] = [{ id: 74180 }];
      jest.spyOn(abonneService, 'query').mockReturnValue(of(new HttpResponse({ body: abonneCollection })));
      const additionalAbonnes = [...bookmarks];
      const expectedCollection: IAbonne[] = [...additionalAbonnes, ...abonneCollection];
      jest.spyOn(abonneService, 'addAbonneToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ club });
      comp.ngOnInit();

      expect(abonneService.query).toHaveBeenCalled();
      expect(abonneService.addAbonneToCollectionIfMissing).toHaveBeenCalledWith(abonneCollection, ...additionalAbonnes);
      expect(comp.abonnesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const club: IClub = { id: 456 };
      const championnat: IChampionnat = { id: 20941 };
      club.championnat = championnat;
      const jeuxes: IJeu = { id: 46694 };
      club.jeuxes = [jeuxes];
      const bookmarks: IAbonne = { id: 71985 };
      club.bookmarks = [bookmarks];

      activatedRoute.data = of({ club });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(club));
      expect(comp.championnatsSharedCollection).toContain(championnat);
      expect(comp.jeusSharedCollection).toContain(jeuxes);
      expect(comp.abonnesSharedCollection).toContain(bookmarks);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Club>>();
      const club = { id: 123 };
      jest.spyOn(clubService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ club });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: club }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(clubService.update).toHaveBeenCalledWith(club);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Club>>();
      const club = new Club();
      jest.spyOn(clubService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ club });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: club }));
      saveSubject.complete();

      // THEN
      expect(clubService.create).toHaveBeenCalledWith(club);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Club>>();
      const club = { id: 123 };
      jest.spyOn(clubService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ club });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clubService.update).toHaveBeenCalledWith(club);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackChampionnatById', () => {
      it('Should return tracked Championnat primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackChampionnatById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackJeuById', () => {
      it('Should return tracked Jeu primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackJeuById(0, entity);
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
    describe('getSelectedJeu', () => {
      it('Should return option if no Jeu is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedJeu(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Jeu for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedJeu(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Jeu is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedJeu(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });

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
