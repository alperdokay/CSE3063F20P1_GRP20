import { Subject } from 'rxjs';
import { Component, OnInit, Input } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import { DomSanitizer } from '@angular/platform-browser';

import { Interface } from 'readline';

@Component({
  selector: 'app-custom-card-photo-gallery',
  templateUrl: './custom-card-photo-gallery.component.html',
  styleUrls: ['./custom-card-photo-gallery.component.scss']
})
export class CustomCardPhotoGalleryComponent implements OnInit {
  @Input("config") config :any;

  @Input()
  titleListener!: Subject<any>;
  selectedPhoto!: PhotoData;
  photoData:PhotoData[];
  constructor(private sanitizer: DomSanitizer) {
    this.photoData = [];
   }

  ngOnInit(): void {
  }
  ngAfterViewInit(){
    let rawPictureDatas :any[] = this.config.config.pictures;
    rawPictureDatas.forEach((rawPictureData) =>{
      let photoData :PhotoData = {title:rawPictureData?.title,dataUrl : rawPictureData?.dataUrl};
      this.photoData.push(photoData)
    })
    console.log(this.photoData)

    this.selectedPhoto = this.photoData[0];
    this.titleListener.next(this.selectedPhoto.title)
  }
}

export interface PhotoData{

  title:string | any;
  dataUrl:any;
}
