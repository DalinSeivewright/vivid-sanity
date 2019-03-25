import {Component} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {ImageService} from "../services/image.service";
import {Router} from "@angular/router";
import {VisibilityType} from "../model/visibility.type";
import {TagInfoModel} from "../model/tag-info.model";
import {AppInfoService} from "../services/app-info.service";

@Component({
  selector: 'app-upload-image-container',
  templateUrl: './upload-image-container.component.html',
  styleUrls: ['./upload-image-container.component.css']
})
export class UploadImageContainerComponent {

  formGroup: FormGroup = null;
  file: File = null;

  constructor(private formBuilder: FormBuilder,
              private imageService: ImageService,
              private appInfoService: AppInfoService,
              private router: Router) {
    this.formGroup = this.formBuilder.group({
      title: this.formBuilder.control(null),
      description: this.formBuilder.control(null),
      tags: this.formBuilder.control(null),
      visibility: VisibilityType.PRIVATE
    })
  }


  get visibilityOptions() {
    return [{value: VisibilityType.PRIVATE, description: "Private"},
              {value: VisibilityType.PUBLIC, description: "Public"}];
  }

  uploadFile() {
    this.imageService.uploadImage(this.file).subscribe((imageInfo: ImageInfoModel) => {
      this.imageService.updateImage(imageInfo.imageKey, this.getImageInfoUploadObject()).subscribe(response => {
        // TODO Turn this into events so we can trigger events and have components listen to them
        this.appInfoService.refreshInfo()
        this.router.navigate(['/recent']);
      });
    })
  }

  getImageInfoUploadObject(): ImageInfoUpdateModel {
    const tags: string[] = this.formGroup.get("tags").value.toString().replace(" ", "").split(",");
    const tagInfos: TagInfoModel[] = tags.map((tag) => {
      return {
        'name': tag,
        'description': ''
      }
    });
    return {
      "title": this.formGroup.get("title").value,
      "description": this.formGroup.get("description").value,
      "tags": tagInfos,
      "visibility": this.formGroup.get("visibility").value
    }
  }

  selectFile(event) {
    this.file = event.target.files[0];
  }
}
