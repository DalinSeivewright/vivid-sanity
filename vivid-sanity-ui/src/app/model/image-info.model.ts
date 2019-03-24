import {VisibilityType} from "./visibility.type";

export class ImageInfoModel {
    imageKey: string;
    imageUri: string;
    thumbnailUri: string;
    description: string;
    tags: string[];
    palette: string;
    visibilityStatus: VisibilityType
}


