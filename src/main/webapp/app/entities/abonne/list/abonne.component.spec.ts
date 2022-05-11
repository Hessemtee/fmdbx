import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AbonneService } from '../service/abonne.service';

import { AbonneComponent } from './abonne.component';

describe('Abonne Management Component', () => {
  let comp: AbonneComponent;
  let fixture: ComponentFixture<AbonneComponent>;
  let service: AbonneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [AbonneComponent],
    })
      .overrideTemplate(AbonneComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AbonneComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AbonneService);

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
    expect(comp.abonnes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
