import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotficationMessage } from './notfication-message';

describe('NotficationMessage', () => {
  let component: NotficationMessage;
  let fixture: ComponentFixture<NotficationMessage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotficationMessage],
    }).compileComponents();

    fixture = TestBed.createComponent(NotficationMessage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
