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
    ImageViewContainerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes, {useHash: false})
  ],
  providers: [ImageService, AppInfoService, RecentImagesService],
  bootstrap: [AppComponent]
})
export class AppModule { }
