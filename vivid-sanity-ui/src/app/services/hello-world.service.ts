import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/index";
import {HttpParams} from "@angular/common/http/src/params";


@Injectable()
export class HelloWorldService {
    constructor(private http: HttpClient) {}

    helloWorld(): Observable<string> {
        return this.http.get("./api/hello", {responseType: 'text'});
    }


    /*
    get<T>(url: string, options?: {
        headers?: HttpHeaders | {
            [header: string]: string | string[];
        };
        observe?: 'body';
        params?: HttpParams | {
            [param: string]: string | string[];
        };
        reportProgress?: boolean;
        responseType?: 'json';
        withCredentials?: boolean;
    }):
*/
}