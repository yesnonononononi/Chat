import { BusinessError } from "@/exception/BusinessError";

export class FileUtil{
    static async beforeValidate(file:File):Promise<File>{
        if(file.size > 1024 * 1024 * 10){
            throw new BusinessError('文件大小不能超过10M');
        }
        if(!file.type.startsWith('image/')){
            throw new BusinessError('文件类型只能是图片');
        }
        return file;    
    }
}