import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppInfoModel} from "../model/app-info.model";
import {ServerModeType} from "../model/server-mode.type";
import {Subscription} from "rxjs";
import {AppInfoService} from "../services/app-info.service";

@Component({
  selector: 'app-header-links',
  templateUrl: './header-links.component.html',
  styleUrls: ['./header-links.component.css']
})
export class HeaderLinksComponent implements OnDestroy {
  adminFunctions: boolean = false;

  appInfoSubscription: Subscription = Subscription.EMPTY;
  constructor(private appInfoService: AppInfoService) {
    this.appInfoSubscription = this.appInfoService.appInfo$.subscribe((appInfo: AppInfoModel) => {
      if (!appInfo || appInfo.serverMode === ServerModeType.EXTERNAL) {
        this.adminFunctions = false;
      } else {
        this.adminFunctions = true;
      }
    });
  }

  ngOnDestroy(): void {
    this.appInfoSubscription.unsubscribe();
  }


}
