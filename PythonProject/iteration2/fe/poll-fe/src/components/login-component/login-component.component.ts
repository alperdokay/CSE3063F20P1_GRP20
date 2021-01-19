import { MatDialog } from '@angular/material/dialog';
import { RestProvider } from './../../providers/rest.provider';
import { Component, Input, OnInit } from '@angular/core';
import { CustomAlertDialogComponent } from '../custom-alert-dialog/custom-alert-dialog.component';

@Component({
  selector: 'app-login-component',
  templateUrl: './login-component.component.html',
  styleUrls: ['./login-component.component.scss']
})
export class LoginComponentComponent implements OnInit {

  password:String = "";
  username:String = "";


  @Input() config:any;
  constructor(private restProvider: RestProvider,private dialog:MatDialog) { }

  ngOnInit(): void {
    if(this.config.username){
      this.username = this.config.username
      this.password = this.config.password
    }
  }

  login(event:any){
    let loginData = {
      "username":this.username,
      "password":this.password
    }
    if((this.password.length < 8 || this.username.length  < 8)  || (this.password == undefined || this.username == undefined) ){
      let dialogRef = this.dialog.open(CustomAlertDialogComponent, {
        width: '350px',
        data: {title: "Authentication Error !", message:"Check Your Credentials" }
      });
    } else{
      this.restProvider.postForObject("/login",loginData).then(
        (data:any) => {
          if(data["result"] == "OK"){
            this.config.login();
          }
        },
      ).catch((error) =>{
        // TODO: Buraya geri don
        // let dialogRef = this.dialog.open(CustomAlertDialogComponent, {
        //   width: '350px',
        //   data: {title: "Server Error !", message:"Check Your Credentials" }
        // });
        this.config.login()
      })
    }
  }
  register(event:any){
    this.config.register();
  }

}
