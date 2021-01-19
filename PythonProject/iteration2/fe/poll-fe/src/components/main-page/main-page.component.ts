import { CustomAlertDialogComponent } from './../custom-alert-dialog/custom-alert-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { RestProvider } from 'src/providers/rest.provider';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {

  @ViewChild('FileSelectInputDialog') FileSelectInputDialog: ElementRef | undefined;
  @ViewChild('drawer', { static: true }) sidenav: MatSidenav | undefined;


  files: String[] = [];
  localUrl: any;
  fileTypes = ""
  buttonConfigs = [
    { text: "Import Students", type: "Students", action: (event: any, config: any) => this.importStudents(event, config), done: false, base64: [] },
    { text: "Import Polls", type: "Poll", action: (event: any, config: any) => this.importPolls(event, config), done: false, base64: [] },
    { text: "Import Answer Keys", type: "AnswerKey", action: (event: any, config: any) => this.importAnswerKeys(event, config), done: false, base64: [] },
  ]
  selectedButton: any;
  targetConfig: any;
  eventListeners: any;
  showReportPage = false;

  constructor(private restProvider: RestProvider, private matDialog: MatDialog) {

  }
  get doneOnes() {
    return this.buttonConfigs.filter((data) => {
      if (data.done) {
        return data
      }
    })
  }
  ngOnInit(): void {
    this.eventListeners = []
  }

  importPolls(event: any, config: any) {
    this.targetConfig = { ...config };
    this.files = []
    this.selectedButton = event.path[1]
    this.fileTypes = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    let e: HTMLElement = this.FileSelectInputDialog?.nativeElement;
    this.targetConfig.done = true
    e.click()
    let x = this.fileUpload.bind(this, config)
    this.eventListeners.push(x)
    e.addEventListener("change", x, false)

  }

  importAnswerKeys(event: any, config: any) {
    this.files = []
    this.targetConfig = { ...config };
    this.fileTypes = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    let e: HTMLElement = this.FileSelectInputDialog?.nativeElement;
    e.click();
    let x = this.fileUpload.bind(this, config)
    this.eventListeners.push(x)
    e.addEventListener("change", x, false)

  }

  importStudents(event: any, config: any) {
    this.files = []
    this.fileTypes = ".csv"
    let e: HTMLElement = this.FileSelectInputDialog?.nativeElement;
    this.targetConfig = { ...config };
    e.click()
    let x = this.fileUpload.bind(this, config)
    this.eventListeners.push(x)
    e.addEventListener("change", x, false)
  }


  fileToBase64 = async (file: any) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader()
      console.log("FILE", file)
      reader.readAsDataURL(file)
      reader.onload = () => resolve(reader.result)
      reader.onerror = (e) => reject(e)
      console.log(this.buttonConfigs)
    })

  fileUpload = (event: any, config: any) => {
    console.log(event, config)
    let filelist: FileList = config.target.files
    if (filelist?.length > 0) {
      let files = Array.from(filelist)?.map((file) => {
        return file
      });
      if (files) {
        event.base64 = []
        files.forEach((data) => {
          this.fileToBase64(data).then((base64) => {
            event.base64.push({ name: data.name, base64: base64 });
          })
        })
        event.done = true
      }
    }
    let e: HTMLElement = this.FileSelectInputDialog?.nativeElement;
    this.eventListeners.forEach((data: any) => {
      e.removeEventListener("change", data, false);
    });
  }
  sendData() {
    let dataStorage: data[] = [];
    for (let buttonConfig of this.buttonConfigs) {
      let data: data = { type: "", paths: [] }
      data["type"] = buttonConfig.type
      data["paths"] = buttonConfig.base64
      dataStorage.push(data)
    }
    this.restProvider.postForObject("/upload", dataStorage).then((response) => {

    }).catch((err) => {
      let dialogRef = this.matDialog.open(CustomAlertDialogComponent, {
        width: '350px',
        data: { title: "Upload Succesful !", message: "Go On" }
      })
      dialogRef.afterClosed().subscribe((data) => {
        this.buttonConfigs = this.buttonConfigs.map((bc) => {
          bc.done = false;
          return bc
        })
        this.sidenav?.close()
        this.showReportPage = true;
      })
    })
  }
  openMenu(){
    if(this.showReportPage){
      let dialogRef = this.matDialog.open(CustomAlertDialogComponent, {
        width: '350px',
        data: { title: "Do you want to reset your data!", message: "If you want to reset your data click on OK !" ,cancel:(ref:any)=>this.dialogCancel(ref)}
      })
      dialogRef.afterClosed().subscribe((data:any)=>{
        if(data == "regular"){
          this.buttonConfigs = [
            { text: "Import Students", type: "Students", action: (event: any, config: any) => this.importStudents(event, config), done: false, base64: [] },
            { text: "Import Polls", type: "Poll", action: (event: any, config: any) => this.importPolls(event, config), done: false, base64: [] },
            { text: "Import Answer Keys", type: "AnswerKey", action: (event: any, config: any) => this.importAnswerKeys(event, config), done: false, base64: [] },
          ]
          this.showReportPage = false;
          this.sidenav?.open()
        }
      })
    }else{
      if(this.sidenav?.opened){
        this.sidenav.close()
      }else{
        this.sidenav?.open()
      }

    }


  }
  dialogCancel(ref:any){
    ref.close()
  }
}
interface data {
  type: string
  paths: any[]
}
