// import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RestProvider } from './../providers/rest.provider';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponentComponent } from '../components/login-component/login-component.component';
import { FormsModule } from '@angular/forms';
import { CustomAlertDialogComponent } from '../components/custom-alert-dialog/custom-alert-dialog.component';
import { MatDialogModule, MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RegisterComponent } from '../components/register/register.component';
import { MainPageComponent } from '../components/main-page/main-page.component';
import { MatSidenav } from '@angular/material/sidenav';

import {MatSidenavModule} from '@angular/material/sidenav';
import {MatToolbarModule} from '@angular/material/toolbar';
import { CustomFileDialogComponent } from '../components/custom-file-dialog/custom-file-dialog.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponentComponent,
    CustomAlertDialogComponent,
    RegisterComponent,
    MainPageComponent,
    CustomFileDialogComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpModule,
    FormsModule,
    // MatDialog,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    MatDialogModule,
    BrowserAnimationsModule,
    MatSidenavModule,
    MatToolbarModule,

  ],
  entryComponents: [
    LoginComponentComponent,
    CustomAlertDialogComponent],
  providers: [
    RestProvider,
    HttpModule,
    {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: true}}

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
