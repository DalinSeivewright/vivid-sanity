import {ServerModeType} from "./server-mode.type";
import {TagInfoModel} from "./tag-info.model";

export interface AppInfoModel {
    serverMode: ServerModeType;
    tags: TagInfoModel[]
}


