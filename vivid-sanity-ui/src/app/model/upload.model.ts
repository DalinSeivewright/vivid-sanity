import {Observable} from "rxjs";
import {ImageInfoModel} from "./image-info.model";
import {UploadProgressModel} from "./upload-progress.model";

export interface UploadModel {
    uploadProgress: Observable<UploadProgressModel>;
    uploadComplete: Observable<ImageInfoModel>;
}
