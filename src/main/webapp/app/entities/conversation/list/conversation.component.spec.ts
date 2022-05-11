import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ConversationService } from '../service/conversation.service';

import { ConversationComponent } from './conversation.component';

describe('Conversation Management Component', () => {
  let comp: ConversationComponent;
  let fixture: ComponentFixture<ConversationComponent>;
  let service: ConversationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ConversationComponent],
    })
      .overrideTemplate(ConversationComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConversationComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ConversationService);

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
    expect(comp.conversations?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
