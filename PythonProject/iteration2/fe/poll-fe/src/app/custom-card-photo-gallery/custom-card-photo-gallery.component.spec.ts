import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomCardPhotoGalleryComponent } from './custom-card-photo-gallery.component';

describe('CustomCardPhotoGalleryComponent', () => {
  let component: CustomCardPhotoGalleryComponent;
  let fixture: ComponentFixture<CustomCardPhotoGalleryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomCardPhotoGalleryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomCardPhotoGalleryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
