import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams, HttpRequest} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs/index";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {AppInfoModel} from "../model/app-info.model";


@Injectable()
export class RecentImagesService {
    constructor(private http: HttpClient) {}
    private _recentImages$ = new BehaviorSubject<ImageInfoModel[]>(null);
    public recentImages$ = this._recentImages$.asObservable();

    refreshRecentImages(): void {
        this.http.get<ImageInfoModel[]>(`./api/images`).subscribe(images => this._recentImages$.next(images));
    }

}
