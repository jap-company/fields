"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[777],{3905:(e,r,t)=>{t.d(r,{Zo:()=>p,kt:()=>y});var n=t(7294);function o(e,r,t){return r in e?Object.defineProperty(e,r,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[r]=t,e}function a(e,r){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);r&&(n=n.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),t.push.apply(t,n)}return t}function i(e){for(var r=1;r<arguments.length;r++){var t=null!=arguments[r]?arguments[r]:{};r%2?a(Object(t),!0).forEach((function(r){o(e,r,t[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):a(Object(t)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(t,r))}))}return e}function l(e,r){if(null==e)return{};var t,n,o=function(e,r){if(null==e)return{};var t,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)t=a[n],r.indexOf(t)>=0||(o[t]=e[t]);return o}(e,r);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)t=a[n],r.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var s=n.createContext({}),c=function(e){var r=n.useContext(s),t=r;return e&&(t="function"==typeof e?e(r):i(i({},r),e)),t},p=function(e){var r=c(e.components);return n.createElement(s.Provider,{value:r},e.children)},d={inlineCode:"code",wrapper:function(e){var r=e.children;return n.createElement(n.Fragment,{},r)}},u=n.forwardRef((function(e,r){var t=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),u=c(t),y=o,f=u["".concat(s,".").concat(y)]||u[y]||d[y]||a;return t?n.createElement(f,i(i({ref:r},p),{},{components:t})):n.createElement(f,i({ref:r},p))}));function y(e,r){var t=arguments,o=r&&r.mdxType;if("string"==typeof e||o){var a=t.length,i=new Array(a);i[0]=u;var l={};for(var s in r)hasOwnProperty.call(r,s)&&(l[s]=r[s]);l.originalType=e,l.mdxType="string"==typeof e?e:o,i[1]=l;for(var c=2;c<a;c++)i[c]=t[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,t)}u.displayName="MDXCreateElement"},9279:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>s,contentTitle:()=>i,default:()=>d,frontMatter:()=>a,metadata:()=>l,toc:()=>c});var n=t(7462),o=(t(7294),t(3905));const a={},i="Error",l={unversionedId:"validation-error",id:"validation-error",title:"Error",description:"This library comes with some predefined error types and some of them may suit you needs.",source:"@site/../fields-docs/target/mdoc/validation-error.md",sourceDirName:".",slug:"/validation-error",permalink:"/fields/docs/validation-error",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/validation-error.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"Syntax",permalink:"/fields/docs/validation-syntax"},next:{title:"FailWith",permalink:"/fields/docs/fail-with"}},s={},c=[{value:"ValidationError",id:"validationerror",level:2},{value:"ValidationMessage",id:"validationmessage",level:2},{value:"FieldError",id:"fielderror",level:2}],p={toc:c};function d(e){let{components:r,...t}=e;return(0,o.kt)("wrapper",(0,n.Z)({},p,t,{components:r,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"error"},"Error"),(0,o.kt)("p",null,"This library comes with some predefined error types and some of them may suit you needs.\nThey all are in ",(0,o.kt)("inlineCode",{parentName:"p"},"jap.fields.error")," package check them out."),(0,o.kt)("p",null,"Don`t worry if none of them is what you need you can use your error type"),(0,o.kt)("h2",{id:"validationerror"},"ValidationError"),(0,o.kt)("p",null,"Error type that has path, error type and optional message.\nEvery error type is separate class so you can easily match on it."),(0,o.kt)("h2",{id:"validationmessage"},"ValidationMessage"),(0,o.kt)("p",null,"Error type that has path, error type and optional message. Thats all nothing special here."),(0,o.kt)("h2",{id:"fielderror"},"FieldError"),(0,o.kt)("p",null,"Error type that has path and generic error that can be anything you want.\nFor example your errors are ussualy just error codes you may use ",(0,o.kt)("inlineCode",{parentName:"p"},"FieldError[Int]")," as your error type to carry both path and error code."))}d.isMDXComponent=!0}}]);