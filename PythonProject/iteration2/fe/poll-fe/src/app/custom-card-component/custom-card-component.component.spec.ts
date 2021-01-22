import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomCardComponentComponent } from './custom-card-component.component';

describe('CustomCardComponentComponent', () => {
  let component: CustomCardComponentComponent;
  let fixture: ComponentFixture<CustomCardComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomCardComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomCardComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
