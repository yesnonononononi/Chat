/// <reference types="vite/client" />
/// <reference types="vite/client" />

declare module '*.vue' {
    import type {DefineComponent} from 'vue'
    const component: DefineComponent<{}, {}, any>
  export default component
}

import type {Message} from 'element-plus';

declare module 'vue' {
  interface ComponentCustomProperties {
    $parseMarkdown: (mdText: string) => string;
    $message: typeof Message;
  }
}