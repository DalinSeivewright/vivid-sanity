import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/index";
import {ImageInfoModel} from "../model/image-info.model";


@Injectable()
export class ImageService {
    constructor(private http: HttpClient) {}

    getImages(): Observable<ImageInfoModel[]> {
        return this.http.get<ImageInfoModel[]>(`./api/images`);
    }
}
