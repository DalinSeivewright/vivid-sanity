import {VisibilityType} from "./visibility.type";
import {TagInfoModel} from "./tag-info.model";

export interface ImageInfoModel {
    imageKey: string;
    imageUri: string;
    thumbnailUri: string;
    title: string
    description: string;
    tags: TagInfoModel[];
    palette: string;
    visibility: VisibilityType
}


