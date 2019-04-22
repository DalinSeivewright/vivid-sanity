import {Component} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {ImageService} from "../services/image.service";
import {Router} from "@angular/router";
import {VisibilityType} from "../model/visibility.type";
import {TagInfoModel} from "../model/tag-info.model";
import {AppInfoService} from "../services/app-info.service";
import {UploadModel} from "../model/upload.model";
import {FileSystemFileEntry, UploadEvent} from "ngx-file-drop";
import {UploadEventType} from "../model/upload-event.type";
import {AppInfoModel} from "../model/app-info.model";
import {ServerModeType} from "../model/server-mode.type";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-upload-image-container',
  templateUrl: './upload-image-container.component.html',
  styleUrls: ['./upload-image-container.component.css']
})
export class UploadImageContainerComponent {

  formGroup: FormGroup = null;
  files: File[] = [];

  uploads: Map<number, UploadModel> = new Map();
  currentUploadKey: number = 0;
  appInfoSubscription: Subscription = Subscription.EMPTY;
  adminFunctions: boolean = false;

  constructor(private formBuilder: FormBuilder,
              private imageService: ImageService,
              private appInfoService: AppInfoService,
              private router: Router) {
    this.formGroup = this.formBuilder.group({});

    this.appInfoSubscription = this.appInfoService.appInfo$.subscribe((appInfo: AppInfoModel) => {
      if (!appInfo || appInfo.serverMode === ServerModeType.EXTERNAL) {
        this.adminFunctions = false;
      } else {
        this.adminFunctions = true;
      }
    });
  }

  createUploadFormGroup(uploadModel: UploadModel) {
    this.formGroup.addControl(uploadModel.index.toString(), new FormGroup({
      title: this.formBuilder.control(null),
      description: this.formBuilder.control(null),
      tags: this.formBuilder.control(null),
      visibility: this.formBuilder.control(VisibilityType.PRIVATE)
    }));
  }


  get visibilityOptions() {
    return [{value: VisibilityType.PRIVATE, description: "Private"},
              {value: VisibilityType.PUBLIC, description: "Public"}];
  }

  // uploadFile() {
  //   this.imageService.uploadImage(this.file).subscribe((imageInfo: ImageInfoModel) => {
  //     console.log("Triggering update.")
  //     this.imageService.updateImage(imageInfo.imageKey, this.getImageInfoUploadObject()).subscribe(response => {
  //       // TODO Turn this into events so we can trigger events and have components listen to them
  //       this.appInfoService.refreshInfo()
  //       this.router.navigate(['/recent']);
  //     });
  //   })
  // }



  selectFiles(event) {
    this.files = Array.from(event.target.files) || [];
  }


  fileDrop(event: UploadEvent) {
    const files = event.files
        .filter(uploadFile => uploadFile.fileEntry.isFile)
        .map(uploadFile => (uploadFile.fileEntry as FileSystemFileEntry));
    files.forEach((fileEntry: FileSystemFileEntry) => {
      fileEntry.file( (file: File) => {
        this.uploadFile(file);
      })
    })
  }

  uploadFiles() {
    this.files.forEach((file: File) => {
      this.uploadFile(file);
    });
  }

  uploadFile(file: File) {
    this.imageService.uploadImageNew(this.currentUploadKey++, file).subscribe( (response: UploadModel) => {
      if (!this.uploads.has(response.index)) {
        this.uploads.set(response.index, {...response});
        this.createUploadFormGroup(response);
      }
      if (response.type === UploadEventType.PROGRESS_UPDATE) {
        console.log("Progress", response.index, response.progress);
        this.uploads.get(response.index).progress = response.progress;
      }

      if (response.type === UploadEventType.COMPLETED) {
        console.log("Complete", response.index, response.upload);
        this.uploads.get(response.index).upload = response.upload;
      }
    });
  }



  get currentUploads(): UploadModel[] {
    return Array.from(this.uploads.values()).sort( (a: UploadModel, b: UploadModel) => {
      if (a.index > b.index) {
        return -1;
      } else if (a.index < b.index) {
        return 1;
      }
      return 0;
    });
  }

  getCompletedClass(upload: UploadModel) {
    if (upload.upload == null) {
      return "progressUpload";
    }
    return "completedUpload";
  }

  getUploadClass(upload: UploadModel) {
    if (upload.progress >= 100) {
      return "upload100";
    } else if (upload.progress >= 90) {
      return "upload90";
    } else if (upload.progress >= 80) {
      return "upload80";
    } else if (upload.progress >= 70) {
      return "upload70";
    } else if (upload.progress >= 60) {
      return "upload60";
    } else if (upload.progress >= 50) {
      return "upload50";
    } else if (upload.progress >= 40) {
      return "upload40";
    } else if (upload.progress >= 30) {
      return "upload30";
    } else if (upload.progress >= 20) {
      return "upload20";
    } else if (upload.progress >= 10) {
      return "upload10";
    } else {
      return "upload0";
    }
  }


  getImageInfoUpdateObject(uploadModel: UploadModel): ImageInfoUpdateModel {
    const uploadFormGroup: FormGroup = (this.formGroup.get(uploadModel.index.toString()) as FormGroup);
    const tagString: string = uploadFormGroup.get("tags").value == null ? "" : uploadFormGroup.get("tags").value;
    const tags: string[] = tagString.replace(" ", "").split(",");
    const tagInfos: TagInfoModel[] = tags.map((tag) => {
      return {
        'name': tag,
        'description': ''
      }
    });
    return {
      "title": uploadFormGroup.get("title").value,
      "description": uploadFormGroup.get("description").value,
      "tags": tagInfos,
      "visibility": uploadFormGroup.get("visibility").value
    }
  }

  updateImageInfo(uploadModel: UploadModel) {
    const imageInfoUpdate = this.getImageInfoUpdateObject(uploadModel);
    this.imageService.updateImage(uploadModel.upload.imageKey, imageInfoUpdate).subscribe(() => {
      this.refreshInfo(uploadModel);
    });
  }

  private refreshInfo(uploadModel: UploadModel) {
    this.imageService.getImage(uploadModel.upload.imageKey).subscribe((imageInfo) => {
      const updatedModel = {...uploadModel};
      updatedModel.upload = imageInfo;
      this.uploads.set(uploadModel.index, updatedModel);
      this.updateFormGroup(updatedModel);
    });
  }

  private updateFormGroup(uploadModel: UploadModel) {
    this.formGroup.get(uploadModel.index.toString()).patchValue({
      'title': uploadModel.upload.title,
      'description': uploadModel.upload.description,
      'tags': this.toTagString(uploadModel.upload.tags),
      'visibility': uploadModel.upload.visibility
    })
  }

  private toTagString(tags: TagInfoModel[]): string {
    return tags.map(tag => tag.name).join((", "))
  }

  deleteImageInfo(uploadModel: UploadModel) {
    this.imageService.deleteImage(uploadModel.upload.imageKey).subscribe(() => {
      this.uploads.delete(uploadModel.index);
      this.formGroup.removeControl(uploadModel.index.toString())
    });
  }


  public save(uploadModel: UploadModel): void {
    this.updateImageInfo(uploadModel);
  }

  public delete(uploadModel: UploadModel): void {
    this.deleteImageInfo(uploadModel);
  }


}
