import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JeuService } from '../service/jeu.service';
import { IJeu, Jeu } from '../jeu.model';

import { JeuUpdateComponent } from './jeu-update.component';

describe('Jeu Management Update Component', () => {
  let comp: JeuUpdateComponent;
  let fixture: ComponentFixture<JeuUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jeuService: JeuService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [JeuUpdateComponent],
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
      .overrideTemplate(JeuUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JeuUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jeuService = TestBed.inject(JeuService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const jeu: IJeu = { id: 456 };

      activatedRoute.data = of({ jeu });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(jeu));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Jeu>>();
      const jeu = { id: 123 };
      jest.spyOn(jeuService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jeu });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jeu }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(jeuService.update).toHaveBeenCalledWith(jeu);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Jeu>>();
      const jeu = new Jeu();
      jest.spyOn(jeuService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jeu });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jeu }));
      saveSubject.complete();

      // THEN
      expect(jeuService.create).toHaveBeenCalledWith(jeu);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Jeu>>();
      const jeu = { id: 123 };
      jest.spyOn(jeuService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jeu });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jeuService.update).toHaveBeenCalledWith(jeu);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
