import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { VersionService } from '../service/version.service';

import { VersionComponent } from './version.component';

describe('Version Management Component', () => {
  let comp: VersionComponent;
  let fixture: ComponentFixture<VersionComponent>;
  let service: VersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [VersionComponent],
    })
      .overrideTemplate(VersionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VersionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(VersionService);

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
    expect(comp.versions?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
