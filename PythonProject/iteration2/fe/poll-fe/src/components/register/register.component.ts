import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { RestProvider } from 'src/providers/rest.provider';
import { CustomAlertDialogComponent } from '../custom-alert-dialog/custom-alert-dialog.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  password:String = "";
  username:String = "";


  @Input() config:any;
  constructor(private restProvider: RestProvider,private dialog:MatDialog) { }

  ngOnInit(): void {
  }

  register(event:any){
    let loginData = {
      "username":this.username,
      "password":this.password
    }
    if((this.password.length < 8 || this.username.length  < 8)  || (this.password == undefined || this.username == undefined) ){
      let dialogRef = this.dialog.open(CustomAlertDialogComponent, {
        width: '350px',
        data: {title: "Register Error !", message:"Check Your Credentials" }
      });
    } else{
      this.restProvider.postForObject("/register",loginData).then(
        (data:any) => {
          if(data["result"] == "OK"){
            this.config.register();
          }
        },
      ).catch((error) =>{
        // TODO: Buraya geri don
        // let dialogRef = this.dialog.open(CustomAlertDialogComponent, {
        //   width: '350px',
        //   data: {title: "Server Error !", message:"Check Your Credentials" }
        // });
        this.config.register({"username":this.username,"password":this.password})
      })
    }
  }


}
