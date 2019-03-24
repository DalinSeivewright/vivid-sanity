import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {ImageService} from "./services/image.service";
import {AppInfoService} from "./services/app-info.service";
import { PaletteDisplayerComponent } from './palette-displayer/palette-displayer.component';

@NgModule({
  declarations: [
    AppComponent,
    PaletteDisplayerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [ImageService, AppInfoService],
  bootstrap: [AppComponent]
})
export class AppModule { }
