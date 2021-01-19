import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
  selector: 'app-custom-alert-dialog',
  templateUrl: './custom-alert-dialog.component.html',
  styleUrls: ['./custom-alert-dialog.component.scss']
})
export class CustomAlertDialogComponent{


  constructor(
    public dialogRef: MatDialogRef<CustomAlertDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {}

    ngOnInit(): void {
    console.log(this.data)


  }
  close(){
    console.log("CLOSE")
    this.dialogRef.close()
  }


  // onNoClick(): void {
  //   this.dialogRef.close();
  // }

}
