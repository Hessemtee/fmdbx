import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ChampionnatService } from '../service/championnat.service';
import { IChampionnat, Championnat } from '../championnat.model';
import { IPays } from 'app/entities/pays/pays.model';
import { PaysService } from 'app/entities/pays/service/pays.service';

import { ChampionnatUpdateComponent } from './championnat-update.component';

describe('Championnat Management Update Component', () => {
  let comp: ChampionnatUpdateComponent;
  let fixture: ComponentFixture<ChampionnatUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let championnatService: ChampionnatService;
  let paysService: PaysService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ChampionnatUpdateComponent],
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
      .overrideTemplate(ChampionnatUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChampionnatUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    championnatService = TestBed.inject(ChampionnatService);
    paysService = TestBed.inject(PaysService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Pays query and add missing value', () => {
      const championnat: IChampionnat = { id: 456 };
      const pays: IPays = { id: 89240 };
      championnat.pays = pays;

      const paysCollection: IPays[] = [{ id: 52847 }];
      jest.spyOn(paysService, 'query').mockReturnValue(of(new HttpResponse({ body: paysCollection })));
      const additionalPays = [pays];
      const expectedCollection: IPays[] = [...additionalPays, ...paysCollection];
      jest.spyOn(paysService, 'addPaysToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ championnat });
      comp.ngOnInit();

      expect(paysService.query).toHaveBeenCalled();
      expect(paysService.addPaysToCollectionIfMissing).toHaveBeenCalledWith(paysCollection, ...additionalPays);
      expect(comp.paysSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const championnat: IChampionnat = { id: 456 };
      const pays: IPays = { id: 45128 };
      championnat.pays = pays;

      activatedRoute.data = of({ championnat });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(championnat));
      expect(comp.paysSharedCollection).toContain(pays);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Championnat>>();
      const championnat = { id: 123 };
      jest.spyOn(championnatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ championnat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: championnat }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(championnatService.update).toHaveBeenCalledWith(championnat);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Championnat>>();
      const championnat = new Championnat();
      jest.spyOn(championnatService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ championnat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: championnat }));
      saveSubject.complete();

      // THEN
      expect(championnatService.create).toHaveBeenCalledWith(championnat);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Championnat>>();
      const championnat = { id: 123 };
      jest.spyOn(championnatService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ championnat });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(championnatService.update).toHaveBeenCalledWith(championnat);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPaysById', () => {
      it('Should return tracked Pays primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPaysById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
