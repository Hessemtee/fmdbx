import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PaysService } from '../service/pays.service';

import { PaysComponent } from './pays.component';

describe('Pays Management Component', () => {
  let comp: PaysComponent;
  let fixture: ComponentFixture<PaysComponent>;
  let service: PaysService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PaysComponent],
    })
      .overrideTemplate(PaysComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaysComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PaysService);

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
    expect(comp.pays?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
