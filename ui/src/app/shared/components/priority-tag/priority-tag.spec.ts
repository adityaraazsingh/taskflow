import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PriorityTag } from './priority-tag';

describe('PriorityTag', () => {
  let component: PriorityTag;
  let fixture: ComponentFixture<PriorityTag>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriorityTag],
    }).compileComponents();

    fixture = TestBed.createComponent(PriorityTag);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
