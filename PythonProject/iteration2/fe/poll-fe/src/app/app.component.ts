import { RegisterComponent } from '../components/register/register.component';
import { LoginComponentComponent } from '../components/login-component/login-component.component';
import { RestProvider } from './../providers/rest.provider';
import { Component, ComponentFactoryResolver, ViewChild, ViewContainerRef } from '@angular/core';
import "@angular/compiler";
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {


  @ViewChild("targetArea",{read:ViewContainerRef,static:false}) targetArea: ViewContainerRef | undefined;
  title = 'poll-fe';
  username:any;
  password:any;
  loginRoadMap = {
    "login" : (loginData:any) => this.login(loginData),
    "register":()=>this.loginToRegister()
  };
  registerRoadMap = {
    "register":(registerConfig:any) => this.register(registerConfig)
  }
  public status = "closed";

  constructor(private restProvider:RestProvider,private componentFactoryResolver: ComponentFactoryResolver,private router: Router){

  }

  ngOnInit() {
    // this.status = "loggedIn"
    // this.router.navigateByUrl("/main")
    setTimeout(() => {
      this.status = "open"
      this.createComponent(this.loginRoadMap,LoginComponentComponent,this.targetArea)
    }, 1000);
  }
  ngAfterViewInit(){
    // console.log(this.targetArea)
  }

  createComponent(config: any,component: any,container: any){
    let componentFactory = this.componentFactoryResolver.resolveComponentFactory(component);
    let viewContainerRef:ViewContainerRef = container;
    viewContainerRef.clear();
    let componentRef = viewContainerRef.createComponent<any>(componentFactory);
    componentRef.instance.config = config;
  }
  login(loginConfig:any){
    this.status = "login"

  }

  register(registerConfig:any){
    let config = {"username":registerConfig.username,"password":registerConfig.password,...this.loginRoadMap}
    this.createComponent(config,LoginComponentComponent,this.targetArea)
  }
  loginToRegister() {
    this.createComponent(this.registerRoadMap,RegisterComponent,this.targetArea)
  }
  routeToMain(){
    this.status = "loggedIn"
    this.router.navigateByUrl("/main")
  }

}
