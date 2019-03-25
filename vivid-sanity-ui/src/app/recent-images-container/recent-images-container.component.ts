import {Component, OnInit} from '@angular/core';
import {RecentImagesService} from "./recent-images.service";
import {ImageInfoModel} from "../model/image-info.model";
import {Observable} from "rxjs";

@Component({
  selector: 'app-recent-images-container',
  templateUrl: './recent-images-container.component.html',
  styleUrls: ['./recent-images-container.component.css']
})
export class RecentImagesContainerComponent implements OnInit {

  recentImages$: Observable<ImageInfoModel[]> = this.recentImagesService.recentImages$;
  constructor(private recentImagesService: RecentImagesService) { }

  ngOnInit() {
    this.recentImagesService.refreshRecentImages()
  }

  getRouterImageLink(imageInfo: ImageInfoModel): string[] {
    return ["/image", imageInfo.imageKey];
  }

}
