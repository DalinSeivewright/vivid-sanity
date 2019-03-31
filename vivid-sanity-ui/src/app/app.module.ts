import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {ImageService} from "./services/image.service";
import {RecentImagesService} from "./recent-images-container/recent-images.service"
import {AppInfoService} from "./services/app-info.service";
import { PaletteDisplayerComponent } from './palette-displayer/palette-displayer.component';
import { MainContainerComponent } from './main-container/main-container.component';
import { TagListContainerComponent } from './tag-list-container/tag-list-container.component';
import { RecentImagesContainerComponent } from './recent-images-container/recent-images-container.component';
import {RouterModule, Routes} from "@angular/router";
import { UploadImageContainerComponent } from './upload-image-container/upload-image-container.component';
import { ImageViewContainerComponent } from './image-view-container/image-view-container.component';
import { RelatedImagesContainerComponent } from './related-images-container/related-images-container.component';

import {FontAwesomeModule } from '@fortawesome/angular-fontawesome'


import { library } from '@fortawesome/fontawesome-svg-core';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { far } from '@fortawesome/free-regular-svg-icons';


const appRoutes: Routes = [
  { path: '', component: RecentImagesContainerComponent },
  { path: 'recent', component: RecentImagesContainerComponent },
  { path: 'upload', component: UploadImageContainerComponent },
  { path: 'image/:imageKey', component: ImageViewContainerComponent },
];


@NgModule({
  declarations: [
    AppComponent,
    PaletteDisplayerComponent,
    MainContainerComponent,
    TagListContainerComponent,
    RecentImagesContainerComponent,
    UploadImageContainerComponent,
    ImageViewContainerComponent,
    RelatedImagesContainerComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes, {useHash: false}),
    FontAwesomeModule
  ],
  providers: [ImageService, AppInfoService, RecentImagesService],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
    library.add(fas, far);
  }
}
