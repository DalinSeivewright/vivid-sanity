import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ImageService} from "../services/image.service";
import {AppInfoService} from "../services/app-info.service";
import {ActivatedRoute, Router} from "@angular/router";
import {VisibilityType} from "../model/visibility.type";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {TagInfoModel} from "../model/tag-info.model";
import {AppInfoModel} from "../model/app-info.model";
import {ServerModeType} from "../model/server-mode.type";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-image-view-container',
  templateUrl: './image-view-container.component.html',
  styleUrls: ['./image-view-container.component.css']
})
export class ImageViewContainerComponent implements OnDestroy {

  formGroup: FormGroup = null;

  imageInfo: ImageInfoModel = null;

  adminFunctions: boolean = false;

  appInfoSubscription: Subscription = Subscription.EMPTY;

  editMode: boolean = false;
  deleteMode: boolean = false;

  constructor(private formBuilder: FormBuilder,
              private imageService: ImageService,
              private appInfoService: AppInfoService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
    this.formGroup = this.formBuilder.group({
      title: this.formBuilder.control(null),
      description: this.formBuilder.control(null),
      tags: this.formBuilder.control(null),
      visibility: VisibilityType.PRIVATE
    });
    this.formGroup.disable();
    this.appInfoSubscription = this.appInfoService.appInfo$.subscribe((appInfo: AppInfoModel) => {
      if (!appInfo || appInfo.serverMode === ServerModeType.EXTERNAL) {
        this.adminFunctions = false;
      } else {
        this.adminFunctions = true;
      }
    });
    this.activatedRoute.paramMap.subscribe((params) => {
      // TODO Get Guards
      if (!params.has("imageKey")) {
        this.navigateRecent();
        return;
      }
      this.refreshInfo(params.get("imageKey"));
    })
  }

  ngOnDestroy(): void {
    this.appInfoSubscription.unsubscribe();
  }


  public enterEdit(): void {
    this.editMode = true;
    this.formGroup.enable();
  }

  public save(): void {
    this.editMode = false;
    this.updateImageInfo();
    this.formGroup.disable();
    this.refreshInfo(this.imageInfo.imageKey);
  }

  public cancel(): void {
    this.editMode = false;
    this.formGroup.disable();
    this.refreshInfo(this.imageInfo.imageKey);
  }

  public enterDelete(): void {
    this.deleteMode = true;
  }

  public confirmDelete(): void {
    this.deleteMode = false;
    this.deleteImageInfo();
  }

  public cancelDelete(): void {
    this.deleteMode = false;
  }



  get inputClass() {
    if (this.editMode) {
      return "";
    }
    return "disabled"
  }

  updateImageInfo() {
    const imageInfoUpdate = this.getImageInfoUpdateObject();
    this.imageService.updateImage(this.imageInfo.imageKey, imageInfoUpdate).subscribe(() => {
      this.refreshInfo(this.imageInfo.imageKey);
    });
  }

  deleteImageInfo() {
    this.imageService.deleteImage(this.imageInfo.imageKey).subscribe(() => {
      this.navigateRecent();
    });
  }

  private getImageInfoUpdateObject(): ImageInfoUpdateModel {
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

  private refreshInfo(imageKey: string) {
    this.imageService.getImage(imageKey).subscribe((imageInfo) => {
      this.imageInfo = imageInfo;
      this.updateFormGroup(this.imageInfo);
    }, () => {
      this.navigateRecent();
    });
  }

  private navigateRecent() {
    this.router.navigate(["/recent"]);
  }

  private updateFormGroup(imageInfo: ImageInfoModel) {
    this.formGroup.patchValue({
      'title': imageInfo.title,
      'description': imageInfo.description,
      'tags': this.toTagString(imageInfo.tags),
      'visibility': imageInfo.visibility
    })
  }

  private toTagString(tags: TagInfoModel[]): string {
    return tags.map(tag => tag.name).join((", "))
  }


  get visibilityOptions() {
    return [{value: VisibilityType.PRIVATE, description: "Private"},
      {value: VisibilityType.PUBLIC, description: "Public"}];
  }


  get specialMode(): boolean {
    return this.editMode || this.deleteMode;
  }
}
