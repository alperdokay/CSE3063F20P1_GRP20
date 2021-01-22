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
import {MatIconModule} from '@angular/material/icon'
import { CustomFileDialogComponent } from '../components/custom-file-dialog/custom-file-dialog.component';
import { CustomCardComponentComponent } from './custom-card-component/custom-card-component.component';
import { GridsterModule } from 'angular-gridster2';
import { DashboardItemComponent } from './dashboard-item/dashboard-item.component';
import { CustomCardTableComponent } from './custom-card-table/custom-card-table.component';
import { CustomCardPhotoGalleryComponent } from './custom-card-photo-gallery/custom-card-photo-gallery.component';
import {MatTableModule} from '@angular/material/table'
import {CdkTableModule} from "@angular/cdk/table"

@NgModule({
  declarations: [
    AppComponent,
    LoginComponentComponent,
    CustomAlertDialogComponent,
    RegisterComponent,
    MainPageComponent,
    CustomFileDialogComponent,
    CustomCardComponentComponent,
    DashboardItemComponent,
    CustomCardTableComponent,
    CustomCardPhotoGalleryComponent,

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
    GridsterModule,
    MatIconModule,
    MatTableModule,
    CdkTableModule

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
