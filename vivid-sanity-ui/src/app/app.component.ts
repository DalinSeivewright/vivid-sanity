import {Component, OnInit} from '@angular/core';
import {ImageService} from "./services/image.service";
import {ImageInfoModel} from "./model/image-info.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {ServerModeType} from "./model/server-mode.type";
import {AppInfoModel} from "./model/app-info.model";
import {AppInfoService} from "./services/app-info.service";
import {VisibilityType} from "./model/visibility.type";
import {ImageInfoUpdateModel} from "./model/image-info-update.model";

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
        description: this.formBuilder.control("This is my description"),
        tags: this.formBuilder.control("tag1, tag2, tag3"),
        visibility: VisibilityType.PUBLIC
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
    this.imageService.uploadImage(this.file).subscribe((imageInfo: ImageInfoModel) => {
      this.imageService.updateImage(imageInfo.imageKey, this.getImageInfoUploadObject()).subscribe(response => {
        console.log("UPload and Updated");
      });
    })
  }

  getImageInfoUploadObject(): ImageInfoUpdateModel {
    const tags: string[] = this.formGroup.get("tags").value.toString().replace(" ", "").split(",");
    return {
      "description": this.formGroup.get("description").value,
      "tags": tags,
      "visibility": this.formGroup.get("visibility").value
    }
  }

  selectFile(event) {
    this.file = event.target.files[0];
  }

  get localMode(): boolean {
    console.log(this.serverMode === ServerModeType.LOCAL);
    return this.serverMode === ServerModeType.LOCAL;
  }
}
