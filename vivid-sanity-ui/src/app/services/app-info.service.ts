import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams, HttpRequest} from "@angular/common/http";
import {BehaviorSubject, Observable, Subject} from "rxjs/index";
import {ImageInfoModel} from "../model/image-info.model";
import {AppInfoModel} from "../model/app-info.model";


@Injectable()
export class AppInfoService {
    constructor(private http: HttpClient) {}

    private _appInfo$ = new BehaviorSubject<AppInfoModel>(null);
    public appInfo$ = this._appInfo$.asObservable();
    refreshInfo(): void {
        this.http.get<AppInfoModel>(`./api/info`).subscribe(appInfo => this._appInfo$.next(appInfo));
    }
}
