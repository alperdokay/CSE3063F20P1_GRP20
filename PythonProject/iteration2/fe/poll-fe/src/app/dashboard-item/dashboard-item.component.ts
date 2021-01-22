import { CustomCardPhotoGalleryComponent } from './../custom-card-photo-gallery/custom-card-photo-gallery.component';
import { CustomCardTableComponent } from './../custom-card-table/custom-card-table.component';
import { Component, ComponentFactoryResolver, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild, ViewContainerRef } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'dashboard-item',
  templateUrl: './dashboard-item.component.html',
  styleUrls: ['./dashboard-item.component.scss']
})
export class DashboardItemComponent implements OnInit {
  @Input("config") config:any;
  @ViewChild("contentArea",{read:ViewContainerRef}) componentArea:ViewContainerRef|any;
  @Output() onItemDelete: EventEmitter<any> = new EventEmitter();

  types = {
    "table":CustomCardTableComponent,
    "picture":CustomCardPhotoGalleryComponent
  }
  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit(): void {
  }
  ngOnChanges(changes:SimpleChanges){
    let config = changes.config.currentValue;
    if(config&&changes.config.firstChange == false){
      console.log("CONFIG CARD",config)
      this.createComponent(config,this.componentArea,config.type)
    }
  }
  ngAfterViewInit(){
    this.createComponent(this.config,this.componentArea,this.config.type)
    console.log(this.componentArea)
    this.componentArea.element.nativeElement.previousElementSibling.style.height = "100%"
  }
  delete(){
    this.onItemDelete.emit(this.config);
  }

  createComponent(config:any,container:any,component:any){
    console.log(config,container,component)
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(component);
    const componentRef = container.createComponent(componentFactory);
    componentRef.instance.config = config;

    if(config.title == undefined){
      let subject =new Subject<any>();
      componentRef.instance.titleListener = subject;
      subject.subscribe((title) =>{
        this.config.title = title;
      })
    }
    // Push the component so that we can keep track of which components are created

  }
}
