import {Component, Input, OnChanges} from '@angular/core';
import {ImageInfoModel} from "../model/image-info.model";
import {ImageService} from "../services/image.service";
import {Observable, EMPTY} from "rxjs";
@Component({
  selector: 'app-related-images-container',
  templateUrl: './related-images-container.component.html',
  styleUrls: ['./related-images-container.component.css']
})
export class RelatedImagesContainerComponent implements OnChanges {

  @Input()
  imageKey: string;

  relatedImages$: Observable<ImageInfoModel[]> = EMPTY;

  constructor(private imageService: ImageService) { }

  ngOnChanges(): void {
    this.relatedImages$ = this.imageService.getRelatedImages(this.imageKey)
  }

  getRouterImageLink(imageInfo: ImageInfoModel): string[] {
    return ["/image", imageInfo.imageKey];
  }

}
