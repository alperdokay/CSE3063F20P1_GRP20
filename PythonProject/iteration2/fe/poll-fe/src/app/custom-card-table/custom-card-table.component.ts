import { DataSource } from '@angular/cdk/table';
import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-custom-card-table',
  templateUrl: './custom-card-table.component.html',
  styleUrls: ['./custom-card-table.component.scss']
})
export class CustomCardTableComponent implements OnInit {
  @Input("config") config:any;
  displayedColumns:any;
  rows: any;
  columns: any;
  constructor() { }

  ngOnInit(): void {
  }
  ngAfterViewInit(){
    this.displayedColumns = this.config.config.columns.map((x:any) => x.columnDef);
    this.rows = this.config?.config?.rows;
    this.columns = this.config?.config?.columns
  }
  ngOnChanges(changes:SimpleChanges){
    console.log("CHANGES",changes)
    let config = changes.config.currentValue;
    if(config){
      this.rows = config?.config?.rows;
      this.columns = config?.config?.columns
      console.log("ROWS",this.rows)
    }
    }

}
