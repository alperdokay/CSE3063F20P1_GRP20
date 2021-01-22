import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomCardTableComponent } from './custom-card-table.component';

describe('CustomCardTableComponent', () => {
  let component: CustomCardTableComponent;
  let fixture: ComponentFixture<CustomCardTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomCardTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomCardTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
