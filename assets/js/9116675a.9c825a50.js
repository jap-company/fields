"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[333],{3905:function(e,t,n){n.d(t,{Zo:function(){return u},kt:function(){return p}});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=r.createContext({}),f=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},u=function(e){var t=f(e.components);return r.createElement(c.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,c=e.parentName,u=l(e,["components","mdxType","originalType","parentName"]),d=f(n),p=a,m=d["".concat(c,".").concat(p)]||d[p]||s[p]||i;return n?r.createElement(m,o(o({ref:t},u),{},{components:n})):r.createElement(m,o({ref:t},u))}));function p(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,o=new Array(i);o[0]=d;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:a,o[1]=l;for(var f=2;f<i;f++)o[f]=n[f];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},117:function(e,t,n){n.r(t),n.d(t,{assets:function(){return u},contentTitle:function(){return c},default:function(){return p},frontMatter:function(){return l},metadata:function(){return f},toc:function(){return s}});var r=n(7462),a=n(3366),i=(n(7294),n(3905)),o=["components"],l={},c="ValidationEffect",f={unversionedId:"validation-effect",id:"validation-effect",title:"ValidationEffect",description:"Defines Monad/Defer like capabilities for your F[_].",source:"@site/../fields-docs/target/mdoc/validation-effect.md",sourceDirName:".",slug:"/validation-effect",permalink:"/fields/docs/validation-effect",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/validation-effect.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"ValidationResult",permalink:"/fields/docs/validation-result"},next:{title:"ValidationPolicy",permalink:"/fields/docs/validation-policy"}},u={},s=[{value:"Instances",id:"instances",level:2}],d={toc:s};function p(e){var t=e.components,n=(0,a.Z)(e,o);return(0,i.kt)("wrapper",(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"validationeffect"},"ValidationEffect"),(0,i.kt)("p",null,"Defines Monad/Defer like capabilities for your ",(0,i.kt)("inlineCode",{parentName:"p"},"F[_]"),"."),(0,i.kt)("p",null,"This is here just to not rely on cats library if you dont need it."),(0,i.kt)("p",null,"If you want Fields to correctly handle short-circuiting you should use lazy ValidationEffect like ",(0,i.kt)("inlineCode",{parentName:"p"},"zio.ZIO")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"cats.effect.IO")),(0,i.kt)("p",null,"If you don","`","t need async validation, but want short-circuiting stick to ",(0,i.kt)("inlineCode",{parentName:"p"},"cats.Eval")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"trait ValidationEffect[F[_]] {\n  def pure[A](a: A): F[A]\n  def suspend[A](a: => A): F[A]\n  def defer[A](a: => F[A]): F[A]\n  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]\n  def map[A, B](fa: F[A])(f: A => B): F[B]\n}\n")),(0,i.kt)("h2",{id:"instances"},"Instances"),(0,i.kt)("p",null,"Predefined instances:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"Anything that has ",(0,i.kt)("inlineCode",{parentName:"li"},"cats.Monad"),"/",(0,i.kt)("inlineCode",{parentName:"li"},"cats.Defer")," instances"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"zio.ZIO[R, E, _]")),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"scala.concurrent.Future"),", requires ",(0,i.kt)("inlineCode",{parentName:"li"},"ExectionContext")),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"ValidationEffect.Sync")," same as ",(0,i.kt)("inlineCode",{parentName:"li"},"cats.Id"))))}p.isMDXComponent=!0}}]);