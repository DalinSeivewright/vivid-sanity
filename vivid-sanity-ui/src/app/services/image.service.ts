import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/index";
import {HttpParams} from "@angular/common/http/src/params";


@Injectable()
export class ImageService {
    constructor(private http: HttpClient) {}

    getImage(imageId: string): Observable<string> {
        return this.http.get(`./api/image/${imageId}`, {responseType: 'text'});
    }
}
