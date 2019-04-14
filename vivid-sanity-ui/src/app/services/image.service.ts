import {Injectable} from "@angular/core";
import {HttpClient, HttpEvent, HttpEventType, HttpRequest, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {filter, map} from "rxjs/operators";
import {UploadEventType} from "../model/upload-event.type";
import {UploadModel} from "../model/upload.model";


@Injectable()
export class ImageService {
    constructor(private http: HttpClient) {}

    getRelatedImages(imageKey: string): Observable<ImageInfoModel[]> {
        return this.http.get<ImageInfoModel[]>(`./api/images/similar/${imageKey}`);
    }

    getImages(): Observable<ImageInfoModel[]> {
        return this.http.get<ImageInfoModel[]>(`./api/images`);
    }

    getImage(imageKey: string): Observable<ImageInfoModel> {
        return this.http.get<ImageInfoModel>(`./api/images/${imageKey}`);
    }

    uploadImageNew(index: number, file: File): Observable<UploadModel> {
        const formData: FormData = new FormData();
        formData.append("file", file, "file");
        const uploadRequest = new HttpRequest('POST', './api/images', formData, {
            reportProgress: true,
        });
        return this.http.request(uploadRequest)
            .pipe(filter(event => event.type === HttpEventType.Response || event.type === HttpEventType.UploadProgress))
            .pipe(map((event: HttpEvent<ImageInfoModel>) => {
                if (event.type === HttpEventType.UploadProgress) {
                    return {
                        type: UploadEventType.PROGRESS_UPDATE,
                        index: index,
                        progress: Math.round(100 * event.loaded / event.total),
                        upload: null,
                    }
                }
                const uploadedImage = (event as HttpResponse<ImageInfoModel>).body;
                return {
                    type: UploadEventType.COMPLETED,
                    index: index,
                    progress: null,
                    upload: uploadedImage
                };
            }));
    }

    uploadImage(file: File): Observable<ImageInfoModel> {
        const formData: FormData = new FormData();
        formData.append("file", file, "file");
        return this.http.post<ImageInfoModel>("./api/images", formData);
    }

    updateImage(imageKey: string, imageInfoUpdateModel: ImageInfoUpdateModel) {
        return this.http.put<ImageInfoModel>(`./api/images/${imageKey}`, imageInfoUpdateModel);
    }

    deleteImage(imageKey: string): Observable<void> {
        return this.http.delete<void>(`./api/images/${imageKey}`);
    }
}
