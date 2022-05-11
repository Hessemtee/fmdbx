import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { JeuService } from '../service/jeu.service';

import { JeuComponent } from './jeu.component';

describe('Jeu Management Component', () => {
  let comp: JeuComponent;
  let fixture: ComponentFixture<JeuComponent>;
  let service: JeuService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [JeuComponent],
    })
      .overrideTemplate(JeuComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JeuComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(JeuService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.jeus?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
