import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';
import { Router } from '@angular/router';
import 'rxjs/add/operator/map'

@Injectable()
export class RestProvider {

    IP_ADDRESS: string = "localhost";
    PORT: string = "80";

    public static ORIGIN: string;
    public static isLoggedIn: boolean = false;
    public static LOADING: boolean = false;
    public static BEARER_TOKEN: any;

    constructor(public http: Http, private router: Router) {
        RestProvider.ORIGIN = "LOCAL";
    }

    public getForObject(endpoint: any, params: any) {
        // RestProvider.LOADING = true;
        return new Promise((resolve, reject) => {
            let url = this.createUrl(endpoint, params);
            let headers = new Headers();
            let token = `Bearer ${RestProvider.BEARER_TOKEN}`;
            headers.append('Authorization', token);
            this.http.get(url, { headers: headers })
                .map((res: { json: () => any; }) => res.json())
                .subscribe((data: unknown) => {
                    resolve(data);
                }, (err: { status: number; }) => {
                    console.log(err)
                    if ((err.status == 401) && (RestProvider.isLoggedIn == true)) {
                        this.logout();
                    };
                    reject(err);
                });
        });
    }

    public postForObject(endpoint: any, data: any) {
        // RestProvider.LOADING = true;
        return new Promise((resolve, reject) => {
            let url = this.createBasicUrl(endpoint);
            let _data = this.arrangeData(data);
            let headers = new Headers();
            let token = `Bearer ${RestProvider.BEARER_TOKEN}`;
            headers.append('Authorization', token);
            this.http.post(url, _data, { headers: headers })
                .map((res: { json: () => any; }) => res.json())
                .subscribe((data: unknown) => {
                    resolve(data);
                }, (err: any) => {
                    reject(err);
                });

        });
    }

    public createBasicUrl(endpoint: any) {
        let url = `http://${this.IP_ADDRESS}:${this.PORT}/${endpoint}`;
        return url;
    }

    public createUrl(endpoint: any, params: string | any[]) {
        let url = `http://${this.IP_ADDRESS}:${this.PORT}/${endpoint}`;
        let queryStr = `?origin=${RestProvider.ORIGIN}`;
        if (params.length > 0) {
            for (let idx = 0; idx < params.length; idx++) {
                queryStr += `&${params[idx].key}=${params[idx].value}`;
            }
        }
        url += queryStr;
        return url;
    }

    public arrangeData(data: { origin: string; }) {
        data.origin = RestProvider.ORIGIN;
        return data;
    }
    public static setBearerToken(token: any) {
        RestProvider.BEARER_TOKEN = token;
    }
    public logout() {
        localStorage.setItem("token", "");
        localStorage.setItem("bypassSplash", "onLogout");
        RestProvider.BEARER_TOKEN = undefined;
        // RestProvider.isLoggedIn = false;
        window.location.reload();
        // this.router.navigateByUrl("/");
    }
}
