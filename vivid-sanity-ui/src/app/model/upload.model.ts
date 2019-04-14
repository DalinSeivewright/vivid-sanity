import {ImageInfoModel} from "./image-info.model";
import {UploadEventType} from "./upload-event.type";

export interface UploadModel {
    index: number;
    type: UploadEventType
    progress: number;
    upload: ImageInfoModel;
}
