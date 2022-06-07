"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[987],{3905:function(e,t,n){n.d(t,{Zo:function(){return p},kt:function(){return f}});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var u=r.createContext({}),s=function(e){var t=r.useContext(u),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},p=function(e){var t=s(e.components);return r.createElement(u.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},c=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,u=e.parentName,p=o(e,["components","mdxType","originalType","parentName"]),c=s(n),f=a,m=c["".concat(u,".").concat(f)]||c[f]||d[f]||i;return n?r.createElement(m,l(l({ref:t},p),{},{components:n})):r.createElement(m,l({ref:t},p))}));function f(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,l=new Array(i);l[0]=c;var o={};for(var u in t)hasOwnProperty.call(t,u)&&(o[u]=t[u]);o.originalType=e,o.mdxType="string"==typeof e?e:a,l[1]=o;for(var s=2;s<i;s++)l[s]=n[s];return r.createElement.apply(null,l)}return r.createElement.apply(null,n)}c.displayName="MDXCreateElement"},1799:function(e,t,n){n.r(t),n.d(t,{assets:function(){return p},contentTitle:function(){return u},default:function(){return f},frontMatter:function(){return o},metadata:function(){return s},toc:function(){return d}});var r=n(7462),a=n(3366),i=(n(7294),n(3905)),l=["components"],o={},u="Field",s={unversionedId:"field",id:"field",title:"Field",description:"Library is called Fields solely because it is built around Field data type.",source:"@site/../fields-docs/target/mdoc/field.md",sourceDirName:".",slug:"/field",permalink:"/fields/docs/field",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/field.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"Overview",permalink:"/fields/docs/overview"},next:{title:"FieldPath",permalink:"/fields/docs/fieldpath"}},p={},d=[{value:"Syntax",id:"syntax",level:2}],c={toc:d};function f(e){var t=e.components,n=(0,a.Z)(e,l);return(0,i.kt)("wrapper",(0,r.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"field"},"Field"),(0,i.kt)("p",null,"Library is called Fields solely because it is built around ",(0,i.kt)("inlineCode",{parentName:"p"},"Field")," data type."),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"Field[P]")," has ",(0,i.kt)("em",{parentName:"p"},"path")," of type ",(0,i.kt)("inlineCode",{parentName:"p"},"FieldPath")," and ",(0,i.kt)("em",{parentName:"p"},"value")," of type ",(0,i.kt)("inlineCode",{parentName:"p"},"P"),"."),(0,i.kt)("p",null,"All validations are defined throught syntax available for ",(0,i.kt)("inlineCode",{parentName:"p"},"Field")),(0,i.kt)("p",null,"Various ways to create and transform ",(0,i.kt)("inlineCode",{parentName:"p"},"Field")," is described in ",(0,i.kt)("a",{parentName:"p",href:"#syntax"},"Syntax")," section"),(0,i.kt)("h2",{id:"syntax"},"Syntax"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'val path: FieldPath = ???\nField(path, request.name) //Field(path, request.name) Using path and value\nField(request.name) //Field(FieldPath.root, request.name) Using value without path\nField.from(request.name) //Field(FieldPath("request", "name"), request.name) Innherit path from field selects\n\nval requestF = Field.from(request)\nrequestF.sub(_.name) // Derive subfield using field selector\nrequestF.provideSub("name", request.name) // Manual subfield with provided value\nrequestF.selectSub("name", _.name) // Manual subfield with value selector\nrequestF.map(_.name) //Map only field value\nrequestF.mapPath(_.toUpperCase) //Map only field path\nrequestF.named("apiRequest")//Changes name of field - last FieldPath part\nrequestF.withPath(???)//Set Field path\nrequestF.withValue(???)//Set Field value\n\nval tupleF = Field(1 -> "2")\ntupleF.first//Field(tupleF.path, 1)\ntupleF.second//Field(tupleF.path, "2")\n')))}f.isMDXComponent=!0}}]);