import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ChampionnatService } from '../service/championnat.service';

import { ChampionnatComponent } from './championnat.component';

describe('Championnat Management Component', () => {
  let comp: ChampionnatComponent;
  let fixture: ComponentFixture<ChampionnatComponent>;
  let service: ChampionnatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ChampionnatComponent],
    })
      .overrideTemplate(ChampionnatComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChampionnatComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ChampionnatService);

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
    expect(comp.championnats?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
