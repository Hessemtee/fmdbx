import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VersionService } from '../service/version.service';
import { IVersion, Version } from '../version.model';
import { IJeu } from 'app/entities/jeu/jeu.model';
import { JeuService } from 'app/entities/jeu/service/jeu.service';

import { VersionUpdateComponent } from './version-update.component';

describe('Version Management Update Component', () => {
  let comp: VersionUpdateComponent;
  let fixture: ComponentFixture<VersionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let versionService: VersionService;
  let jeuService: JeuService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VersionUpdateComponent],
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
      .overrideTemplate(VersionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VersionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    versionService = TestBed.inject(VersionService);
    jeuService = TestBed.inject(JeuService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Jeu query and add missing value', () => {
      const version: IVersion = { id: 456 };
      const jeu: IJeu = { id: 98136 };
      version.jeu = jeu;

      const jeuCollection: IJeu[] = [{ id: 51086 }];
      jest.spyOn(jeuService, 'query').mockReturnValue(of(new HttpResponse({ body: jeuCollection })));
      const additionalJeus = [jeu];
      const expectedCollection: IJeu[] = [...additionalJeus, ...jeuCollection];
      jest.spyOn(jeuService, 'addJeuToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ version });
      comp.ngOnInit();

      expect(jeuService.query).toHaveBeenCalled();
      expect(jeuService.addJeuToCollectionIfMissing).toHaveBeenCalledWith(jeuCollection, ...additionalJeus);
      expect(comp.jeusSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const version: IVersion = { id: 456 };
      const jeu: IJeu = { id: 47147 };
      version.jeu = jeu;

      activatedRoute.data = of({ version });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(version));
      expect(comp.jeusSharedCollection).toContain(jeu);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Version>>();
      const version = { id: 123 };
      jest.spyOn(versionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ version });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: version }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(versionService.update).toHaveBeenCalledWith(version);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Version>>();
      const version = new Version();
      jest.spyOn(versionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ version });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: version }));
      saveSubject.complete();

      // THEN
      expect(versionService.create).toHaveBeenCalledWith(version);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Version>>();
      const version = { id: 123 };
      jest.spyOn(versionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ version });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(versionService.update).toHaveBeenCalledWith(version);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackJeuById', () => {
      it('Should return tracked Jeu primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackJeuById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
