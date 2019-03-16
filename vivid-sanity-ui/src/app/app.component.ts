import {Component, OnInit} from '@angular/core';
import {ImageService} from "./services/image.service";
import {DomSanitizer} from "@angular/platform-browser";
import {ImageInfoModel} from "./model/image-info.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {ServerModeType} from "./model/server-mode.type";
import {AppInfoModel} from "./model/app-info.model";
import {AppInfoService} from "./services/app-info.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
  formGroup: FormGroup = null;
  file: File = null;
  imageData: ImageInfoModel[] = [];
  serverMode: ServerModeType = null;
  constructor(private imageService: ImageService,
               private appInfoService: AppInfoService,
               private formBuilder: FormBuilder) {

  }

  public ngOnInit(): void {
    if (!this.formGroup) {
      this.formGroup = this.formBuilder.group({
        fileInput: this.formBuilder.control("")
      })
    }
    this.imageService.getImages().subscribe((response: ImageInfoModel[]) => {
      this.imageData = response;
    })

    this.appInfoService.getInfo().subscribe((response: AppInfoModel) => {
      if (response) {
        this.serverMode = response.serverMode;
      }
    })

  }

  uploadFile() {
    this.imageService.uploadImage(this.file).subscribe(x => {
      console.log("SUCCESS!")
    })
  }

  selectFile(event) {
    this.file = event.target.files[0];
  }

  get localMode(): boolean {
    console.log(this.serverMode === ServerModeType.LOCAL);
    return this.serverMode === ServerModeType.LOCAL;
  }
}
