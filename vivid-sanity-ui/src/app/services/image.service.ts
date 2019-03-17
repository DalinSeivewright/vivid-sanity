import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs/index";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";


@Injectable()
export class ImageService {
    constructor(private http: HttpClient) {}

    getImages(): Observable<ImageInfoModel[]> {
        return this.http.get<ImageInfoModel[]>(`./api/images`);
    }

    uploadImage(file: File): Observable<ImageInfoModel> {
        const formData: FormData = new FormData();
        formData.append("file", file, "file");
        return this.http.post<ImageInfoModel>("./api/images", formData);
    }

    updateImage(imageKey: string, imageInfoUpdateModel: ImageInfoUpdateModel) {
        return this.http.put<ImageInfoModel>(`./api/images/${imageKey}`, imageInfoUpdateModel);
    }
}
