import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { JeuDetailComponent } from './jeu-detail.component';

describe('Jeu Management Detail Component', () => {
  let comp: JeuDetailComponent;
  let fixture: ComponentFixture<JeuDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [JeuDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ jeu: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(JeuDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(JeuDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load jeu on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.jeu).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
