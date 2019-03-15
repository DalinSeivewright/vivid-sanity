import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs/index";
import {ImageInfoModel} from "../model/image-info.model";
import {AppInfoModel} from "../model/app-info.model";


@Injectable()
export class AppInfoService {
    constructor(private http: HttpClient) {}

    getInfo(): Observable<AppInfoModel> {
        return this.http.get<AppInfoModel>(`./api/info`);
    }
}
