import {Injectable} from "@angular/core";
import {
    HttpClient, HttpEvent,
    HttpEventType,
    HttpParams,
    HttpProgressEvent,
    HttpRequest,
    HttpResponse
} from "@angular/common/http";
import {from, Observable, Observer, Subscriber} from "rxjs";
import {ImageInfoModel} from "../model/image-info.model";
import {ImageInfoUpdateModel} from "../model/image-info-update.model";
import {UploadModel} from "../model/upload.model";
import {UploadProgressModel} from "../model/upload-progress.model";
import {filter, map, mapTo, partition} from "rxjs/operators";


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

    uploadImageNew(index: number, file: File): UploadModel {
        const formData: FormData = new FormData();
        formData.append("file", file, "file");
        const uploadRequest = new HttpRequest('POST', './api/images', formData, {
            reportProgress: true,
        });
        const source = from(this.http.request(uploadRequest).pipe());
        
        const [progress, completed] = partition((event: HttpEvent<any>) => (event.type === HttpEventType.UploadProgress))(source);

        const progressObservable = progress.pipe(map( (event: HttpProgressEvent) => {
            return {
                progress: Math.round(100 * event.loaded / event.total),
                progressIndex: index
            }
        }));

        const uploadedObservable = completed.pipe(filter(event => event.type === HttpEventType.Response))
            .pipe(map( (event: HttpResponse<any>) => {
            return event.body as ImageInfoModel;
        }));

        const uploadModel: UploadModel = {
            uploadProgress: progressObservable,
            uploadComplete: uploadedObservable
        };
        source.subscribe();
        return uploadModel
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
