import {Component, OnDestroy, OnInit} from '@angular/core';
import {FileSystemFileEntry, UploadEvent} from "ngx-file-drop";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageService} from "../services/image.service";
import {Subscription, timer} from "rxjs";

@Component({
  selector: 'app-header-upload',
  templateUrl: './header-upload.component.html',
  styleUrls: ['./header-upload.component.css']
})
export class HeaderUploadComponent implements OnInit, OnDestroy {

  constructor(private imageService: ImageService) { }
  uploadedImages: Map<ImageInfoModel, Subscription> = new Map()
  ngOnInit() {
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
        this.imageService.uploadImage(file).subscribe((imageInfo: ImageInfoModel) => {
          this.addImageComplete(imageInfo);
        });
      })
    })
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



}
