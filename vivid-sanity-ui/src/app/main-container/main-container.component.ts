import {Component, Input, OnInit} from '@angular/core';
import {AppInfoModel} from "../model/app-info.model";
import {TagInfoModel} from "../model/tag-info.model";

@Component({
  selector: 'app-main-container',
  templateUrl: './main-container.component.html',
  styleUrls: ['./main-container.component.css']
})
export class MainContainerComponent implements OnInit {

  @Input()
  appInfo: AppInfoModel = null;
  constructor() { }

  ngOnInit() {
  }

  get tags(): TagInfoModel[] {
    if (this.appInfo) {
      return this.appInfo.tags;
    }
    return []
  }

}
