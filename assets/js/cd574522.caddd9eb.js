"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[826],{3905:function(e,t,n){n.d(t,{Zo:function(){return p},kt:function(){return f}});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=r.createContext({}),s=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},p=function(e){var t=s(e.components);return r.createElement(c.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},u=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,c=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),u=s(n),f=a,m=u["".concat(c,".").concat(f)]||u[f]||d[f]||i;return n?r.createElement(m,o(o({ref:t},p),{},{components:n})):r.createElement(m,o({ref:t},p))}));function f(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,o=new Array(i);o[0]=u;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:a,o[1]=l;for(var s=2;s<i;s++)o[s]=n[s];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}u.displayName="MDXCreateElement"},7574:function(e,t,n){n.r(t),n.d(t,{assets:function(){return p},contentTitle:function(){return c},default:function(){return f},frontMatter:function(){return l},metadata:function(){return s},toc:function(){return d}});var r=n(7462),a=n(3366),i=(n(7294),n(3905)),o=["components"],l={},c="FieldPath",s={unversionedId:"fieldpath",id:"fieldpath",title:"FieldPath",description:"Stores Field path, that at the end of the day is used to know where some ValidationError was raised.",source:"@site/../fields-docs/target/mdoc/fieldpath.md",sourceDirName:".",slug:"/fieldpath",permalink:"/fields/docs/fieldpath",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/fieldpath.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"Field",permalink:"/fields/docs/field"},next:{title:"ValidationResult",permalink:"/fields/docs/validation-result"}},p={},d=[{value:"Syntax",id:"syntax",level:2}],u={toc:d};function f(e){var t=e.components,n=(0,a.Z)(e,o);return(0,i.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"fieldpath"},"FieldPath"),(0,i.kt)("p",null,"Stores ",(0,i.kt)("inlineCode",{parentName:"p"},"Field")," ",(0,i.kt)("em",{parentName:"p"},"path"),", that at the end of the day is used to know where some ValidationError was raised."),(0,i.kt)("p",null,"Various ways to create and transform ",(0,i.kt)("inlineCode",{parentName:"p"},"FieldPath")," is described in ",(0,i.kt)("a",{parentName:"p",href:"#syntax"},"Syntax")," section"),(0,i.kt)("h2",{id:"syntax"},"Syntax"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'FieldPath.root\nFieldPath("request", "name")\nFieldPath(List("request", "name"))\nFieldPath.raw("request.name")\n\n//Implicit Conversion\nval path: FieldPath = nameF\nval path: FieldPath = "name"\nval path: FieldPath = List("request", "name")\n')))}f.isMDXComponent=!0}}]);