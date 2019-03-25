import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ImageService} from "../services/image.service";
import {AppInfoService} from "../services/app-info.service";
import {ActivatedRoute, Router} from "@angular/router";
import {VisibilityType} from "../model/visibility.type";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {TagInfoModel} from "../model/tag-info.model";

@Component({
  selector: 'app-image-view-container',
  templateUrl: './image-view-container.component.html',
  styleUrls: ['./image-view-container.component.css']
})
export class ImageViewContainerComponent implements OnInit {

  formGroup: FormGroup = null;


  imageInfo: ImageInfoModel = null;

  editModeMap: {} = {}

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
    })
    this.activatedRoute.paramMap.subscribe((params) => {
      // TODO Get Guards
      if (!params.has("imageKey")) {
        this.router.navigate(["/recent"]);
        return;
      }

      this.imageService.getImage(params.get("imageKey")).subscribe((imageInfo) => {
        this.imageInfo = imageInfo;
        this.updateFormGroup(this.imageInfo);
      })

    })
  }



  ngOnInit(): void {
    console.log("On Init!")
  }

  private updateFormGroup(imageInfo: ImageInfoModel) {
    this.formGroup.patchValue({
      'title': imageInfo.title,
      'description': imageInfo.description,
      'tags': this.toTagString(imageInfo.tags),
      'visiblity': imageInfo.visibility
    })
  }

  private toTagString(tags: TagInfoModel[]): string {
    return tags.map(tag => tag.name).join((", "))
  }

  get tagString(): string {
    return this.toTagString(this.imageInfo.tags);
  }



  get visibilityOptions() {
    return [{value: VisibilityType.PRIVATE, description: "Private"},
      {value: VisibilityType.PUBLIC, description: "Public"}];
  }

  // TODO This is stupid.  Make it not stupid.
  get visibilityOptionsMap() {
    return {
      "PRIVATE": "Private",
      "PUBLIC": "Public"
    };
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

  isEditMode(key: string): boolean {
    if (this.editModeMap.hasOwnProperty(key)) {
      return this.editModeMap[key];
    } else {
      return false;
    }
  }

}
