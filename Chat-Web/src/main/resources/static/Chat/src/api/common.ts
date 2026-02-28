import type {data} from "@/types/user";
import {ApiHelper} from "@/utils/ApiHelper";
import request from "@/utils/axios";

export const uploadFile = (file: File,type?:string):Promise<data<string>> => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("directory", type || "file");
    return request.post("/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    });
}

export const getSign = ():Promise<string> =>{
    return ApiHelper.handle(request.get("/sign"));
}

 
