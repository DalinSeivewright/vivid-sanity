import {Component, Input, OnInit} from '@angular/core';
import {AppInfoModel} from "../model/app-info.model";
import {TagInfoModel} from "../model/tag-info.model";
import {ServerModeType} from "../model/server-mode.type";

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

  get showUploadLink(): boolean {
    return this.appInfo != null && this.appInfo.serverMode === ServerModeType.LOCAL
  }

}
