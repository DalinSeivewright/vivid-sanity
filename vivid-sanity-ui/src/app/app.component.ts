import {Component, OnInit} from '@angular/core';
import {ImageService} from "./services/image.service";
import {DomSanitizer} from "@angular/platform-browser";
import {ImageInfoModel} from "./model/image-info.model";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
  formGroup: FormGroup = null;
  file: File = null;
  imageData: ImageInfoModel[] = [];
  constructor(private imageService: ImageService,
               private formBuilder: FormBuilder) {

  }

  public ngOnInit(): void {
    if (!this.formGroup) {
      this.formGroup = this.formBuilder.group({
        fileInput: this.formBuilder.control("")
      })
    }
    this.imageService.getImages().subscribe((response: ImageInfoModel[]) => {
      this.imageData = response;
    })
  }

  uploadFile() {
    this.imageService.uploadImage(this.file).subscribe(x => {
      console.log("SUCCESS!")
    })
  }

  selectFile(event) {
    this.file = event.target.files[0];
  }
}
