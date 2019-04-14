import {Component, OnDestroy, OnInit} from '@angular/core';
import {FileSystemFileEntry, UploadEvent} from "ngx-file-drop";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageService} from "../services/image.service";
import {Subscription, timer} from "rxjs";
import {UploadModel} from "../model/upload.model";
import {UploadEventType} from "../model/upload-event.type";

@Component({
  selector: 'app-header-upload',
  templateUrl: './header-upload.component.html',
  styleUrls: ['./header-upload.component.css']
})
export class HeaderUploadComponent implements OnInit, OnDestroy {

  constructor(private imageService: ImageService) { }
  uploadedImages: Map<ImageInfoModel, Subscription> = new Map();

  uploads: Map<number, UploadModel> = new Map();
  currentUploadKey: number = 0;
  ngOnInit() {
    this.uploads.set(this.currentUploadKey++, {
      type: UploadEventType.PROGRESS_UPDATE,
      progress: 40,
      index: 0,
      upload: null
    });
    console.log(this.currentUploads);
  }

  ngOnDestroy(): void {
    this.uploadedImages.forEach((sub: Subscription, image: ImageInfoModel) => {
      sub.unsubscribe();
      this.uploadedImages.delete(image);
    });
  }


  fileDrop(event: UploadEvent) {
    const files = event.files
        .filter(uploadFile => uploadFile.fileEntry.isFile)
        .map(uploadFile => (uploadFile.fileEntry as FileSystemFileEntry));
    files.forEach((fileEntry: FileSystemFileEntry) => {
      fileEntry.file( (file:File) => {
        this.imageService.uploadImageNew(this.currentUploadKey++, file).subscribe( (response: UploadModel) => {
          if (!this.uploads.has(response.index)) {
            this.uploads.set(response.index, {...response});
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
        // this.imageService.uploadImage(file).subscribe((imageInfo: ImageInfoModel) => {
        //   this.addImageComplete(imageInfo);
        // });
      })
    })
  }

  get currentUploads(): UploadModel[] {
    return Array.from(this.uploads.values());
  }


  addImageComplete(imageInfo: ImageInfoModel) {
    const imageKillTimer = timer(10000);
    const killTimerSub = imageKillTimer.subscribe(() => {
      this.clearImage(imageInfo);
    });
    this.uploadedImages.set(imageInfo, killTimerSub);
  }

  clearImage(imageInfo: ImageInfoModel) {
    this.uploadedImages.get(imageInfo).unsubscribe();
    this.uploadedImages.delete(imageInfo);
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



}
