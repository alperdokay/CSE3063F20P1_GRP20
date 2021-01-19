import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomFileDialogComponent } from './custom-file-dialog.component';

describe('CustomFileDialogComponent', () => {
  let component: CustomFileDialogComponent;
  let fixture: ComponentFixture<CustomFileDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomFileDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomFileDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
