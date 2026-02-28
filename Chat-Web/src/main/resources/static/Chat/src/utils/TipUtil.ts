import {ElMessage} from "element-plus";


export let log = (options:any)=>{
return ElMessage({
  duration:1000,
  ...options
})
};




interface Logger {
  info: any;
  error: any;
  warn:any;
  ok:any;
  primary:any
}
export const Log: Logger = {
  info:(message:any)=>log({type:"info",message}),
  error: (message:any)=>log({type:"error",message}),
  warn:(message:any)=>log({type:"warning",message}),
  ok:(message:any)=>log({type:"success",message}),
  primary:(message:any)=>log({type:"primary",message})
};


