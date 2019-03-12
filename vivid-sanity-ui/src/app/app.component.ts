import {Component, OnInit} from '@angular/core';
import {ImageService} from "./services/image.service";
import {DomSanitizer} from "@angular/platform-browser";
import {ImageInfoModel} from "./model/image-info.model";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
  imageData: ImageInfoModel[] = [];
  constructor(private imageService: ImageService) {

  }

  public ngOnInit(): void {
    this.imageService.getImages().subscribe((response: ImageInfoModel[]) => {
      this.imageData = response;
    })
  }
}
