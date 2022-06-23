"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[129],{3905:function(e,t,n){n.d(t,{Zo:function(){return u},kt:function(){return m}});var i=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);t&&(i=i.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,i)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?r(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):r(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,i,a=function(e,t){if(null==e)return{};var n,i,a={},r=Object.keys(e);for(i=0;i<r.length;i++)n=r[i],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(i=0;i<r.length;i++)n=r[i],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=i.createContext({}),s=function(e){var t=i.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},u=function(e){var t=s(e.components);return i.createElement(c.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return i.createElement(i.Fragment,{},t)}},p=i.forwardRef((function(e,t){var n=e.components,a=e.mdxType,r=e.originalType,c=e.parentName,u=o(e,["components","mdxType","originalType","parentName"]),p=s(n),m=a,f=p["".concat(c,".").concat(m)]||p[m]||d[m]||r;return n?i.createElement(f,l(l({ref:t},u),{},{components:n})):i.createElement(f,l({ref:t},u))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var r=n.length,l=new Array(r);l[0]=p;var o={};for(var c in t)hasOwnProperty.call(t,c)&&(o[c]=t[c]);o.originalType=e,o.mdxType="string"==typeof e?e:a,l[1]=o;for(var s=2;s<r;s++)l[s]=n[s];return i.createElement.apply(null,l)}return i.createElement.apply(null,n)}p.displayName="MDXCreateElement"},562:function(e,t,n){n.r(t),n.d(t,{assets:function(){return u},contentTitle:function(){return c},default:function(){return m},frontMatter:function(){return o},metadata:function(){return s},toc:function(){return d}});var i=n(7462),a=n(3366),r=(n(7294),n(3905)),l=["components"],o={},c="ValidationPolicy",s={unversionedId:"validation-policy",id:"validation-policy",title:"ValidationPolicy",description:"Encapsulates Field validation logic. Also there is ValidationPolicyBuilder which provides convenient syntax to define Field validation logic",source:"@site/../fields-docs/target/mdoc/validation-policy.md",sourceDirName:".",slug:"/validation-policy",permalink:"/fields/docs/validation-policy",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/validation-policy.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"Effect",permalink:"/fields/docs/effect"},next:{title:"Goals of the project",permalink:"/fields/docs/goals"}},u={},d=[{value:"Syntax",id:"syntax",level:2}],p={toc:d};function m(e){var t=e.components,n=(0,a.Z)(e,l);return(0,r.kt)("wrapper",(0,i.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"validationpolicy"},"ValidationPolicy"),(0,r.kt)("p",null,"Encapsulates ",(0,r.kt)("inlineCode",{parentName:"p"},"Field")," validation logic. Also there is ",(0,r.kt)("inlineCode",{parentName:"p"},"ValidationPolicyBuilder")," which provides convenient syntax to define ",(0,r.kt)("inlineCode",{parentName:"p"},"Field")," validation logic"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"trait ValidationPolicy[P, F[_], V[_], E] { self =>\n  def validate(field: Field[P]): Rule[F, V, E]\n}\n")),(0,r.kt)("h2",{id:"syntax"},"Syntax"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import jap.fields._\nimport jap.fields.DefaultAccumulateVM._\n\ncase class Email(value: String) extends AnyVal\nobject Email {\n    //Policy is interface with 1 validate method, so you can do so\n    implicit val policy: Policy[Email] = _.map(_.value).all(_.nonEmpty, _.maxSize(40))\n}\ncase class Request(name: String, email: Email, age: Int, hasParrot: Boolean)\nobject Request {\n  implicit val policy: Policy[Request] =\n      Policy\n        .builder[Request]\n        .subRule(_.name)(_.minSize(4), _.maxSize(48)) //runs all validations combining using and\n        .subRule(_.email)(_.validate) //Reuse Email Policy\n        .subRule(_.age, _.hasParrot)((age, hasParrot) => age > 48 || (age > 22 && hasParrot.isTrue)) // 2 fields rule\n        .build\n}\nField(Request("", Email(""), 23, true)).validate.effect // This will use implicit policy to validate\n// res1: Accumulate[ValidationError] = Invalid(\n//   errors = List(\n//     MinSize(path = FieldPath(parts = List("name")), size = 4),\n//     Empty(path = FieldPath(parts = List("email")))\n//   )\n// ) // This will use implicit policy to validate\nField(Request("1234", Email("ann@gmail.com"), 23, true)).validateEither\n// res2: Either[Accumulate[ValidationError], Request] = Right(\n//   value = Request(\n//     name = "1234",\n//     email = Email(value = "ann@gmail.com"),\n//     age = 23,\n//     hasParrot = true\n//   )\n// )\n')))}m.isMDXComponent=!0}}]);