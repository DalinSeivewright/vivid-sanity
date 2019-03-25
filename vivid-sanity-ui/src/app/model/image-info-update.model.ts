import {VisibilityType} from "./visibility.type";
import {TagInfoModel} from "./tag-info.model";

export interface ImageInfoUpdateModel {
    title: string;
    description: string;
    tags: TagInfoModel[];
    visibility: VisibilityType;
}


