import {Component, OnInit} from '@angular/core';
import {ImageService} from "./services/image.service";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
  imageData: any = null;
  constructor(private imageService: ImageService,
              private domSanitizer: DomSanitizer) {

  }

  public ngOnInit(): void {
    this.imageService.getImage("ar326121.jpg").subscribe(response => {
      this.imageData = response;
    })
  }

  get imageEncoded() {
    return this.domSanitizer.bypassSecurityTrustUrl("data:image/png;base64," + this.imageData);
  }
}
