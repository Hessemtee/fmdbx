import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PaysService } from '../service/pays.service';
import { IPays, Pays } from '../pays.model';
import { IJoueur } from 'app/entities/joueur/joueur.model';
import { JoueurService } from 'app/entities/joueur/service/joueur.service';

import { PaysUpdateComponent } from './pays-update.component';

describe('Pays Management Update Component', () => {
  let comp: PaysUpdateComponent;
  let fixture: ComponentFixture<PaysUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paysService: PaysService;
  let joueurService: JoueurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PaysUpdateComponent],
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
      .overrideTemplate(PaysUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaysUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paysService = TestBed.inject(PaysService);
    joueurService = TestBed.inject(JoueurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Joueur query and add missing value', () => {
      const pays: IPays = { id: 456 };
      const joueurs: IJoueur[] = [{ id: 46948 }];
      pays.joueurs = joueurs;

      const joueurCollection: IJoueur[] = [{ id: 83871 }];
      jest.spyOn(joueurService, 'query').mockReturnValue(of(new HttpResponse({ body: joueurCollection })));
      const additionalJoueurs = [...joueurs];
      const expectedCollection: IJoueur[] = [...additionalJoueurs, ...joueurCollection];
      jest.spyOn(joueurService, 'addJoueurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pays });
      comp.ngOnInit();

      expect(joueurService.query).toHaveBeenCalled();
      expect(joueurService.addJoueurToCollectionIfMissing).toHaveBeenCalledWith(joueurCollection, ...additionalJoueurs);
      expect(comp.joueursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pays: IPays = { id: 456 };
      const joueurs: IJoueur = { id: 59810 };
      pays.joueurs = [joueurs];

      activatedRoute.data = of({ pays });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pays));
      expect(comp.joueursSharedCollection).toContain(joueurs);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pays>>();
      const pays = { id: 123 };
      jest.spyOn(paysService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pays });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pays }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(paysService.update).toHaveBeenCalledWith(pays);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pays>>();
      const pays = new Pays();
      jest.spyOn(paysService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pays });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pays }));
      saveSubject.complete();

      // THEN
      expect(paysService.create).toHaveBeenCalledWith(pays);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pays>>();
      const pays = { id: 123 };
      jest.spyOn(paysService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pays });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paysService.update).toHaveBeenCalledWith(pays);
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
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedJoueur', () => {
      it('Should return option if no Joueur is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedJoueur(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Joueur for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedJoueur(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Joueur is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedJoueur(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
