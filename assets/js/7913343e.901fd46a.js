"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[858,937],{3905:function(e,t,n){n.d(t,{Zo:function(){return u},kt:function(){return m}});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},s=Object.keys(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var l=r.createContext({}),p=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},u=function(e){var t=p(e.components);return r.createElement(l.Provider,{value:t},e.children)},c={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,s=e.originalType,l=e.parentName,u=o(e,["components","mdxType","originalType","parentName"]),d=p(n),m=a,f=d["".concat(l,".").concat(m)]||d[m]||c[m]||s;return n?r.createElement(f,i(i({ref:t},u),{},{components:n})):r.createElement(f,i({ref:t},u))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var s=n.length,i=new Array(s);i[0]=d;var o={};for(var l in t)hasOwnProperty.call(t,l)&&(o[l]=t[l]);o.originalType=e,o.mdxType="string"==typeof e?e:a,i[1]=o;for(var p=2;p<s;p++)i[p]=n[p];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},4363:function(e,t,n){n.r(t),n.d(t,{assets:function(){return c},contentTitle:function(){return p},default:function(){return f},frontMatter:function(){return l},metadata:function(){return u},toc:function(){return d}});var r=n(7462),a=n(3366),s=(n(7294),n(3905)),i=n(5949),o=["components"],l={},p="Overview",u={unversionedId:"overview",id:"overview",title:"Overview",description:"Fields is a Scala validation library that you should use because it is:",source:"@site/../fields-docs/target/mdoc/overview.md",sourceDirName:".",slug:"/overview",permalink:"/fields/docs/overview",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/overview.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",next:{title:"Module",permalink:"/fields/docs/validation-module"}},c={},d=[{value:"Getting started",id:"getting-started",level:2},{value:"Code teaser",id:"code-teaser",level:2},{value:"Adopters",id:"adopters",level:2},{value:"Sponsors",id:"sponsors",level:2},{value:"License",id:"license",level:2}],m={toc:d};function f(e){var t=e.components,n=(0,a.Z)(e,o);return(0,s.kt)("wrapper",(0,r.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("h1",{id:"overview"},"Overview"),(0,s.kt)("p",null,"Fields is a Scala validation library that you should use because it is:"),(0,s.kt)(i.default,{mdxType:"Traits"}),(0,s.kt)("p",null,(0,s.kt)("a",{parentName:"p",href:"https://stand-with-ukraine.pp.ua"},(0,s.kt)("img",{parentName:"a",src:"https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner-direct-single.svg",alt:"Stand With Ukraine"}))),(0,s.kt)("h2",{id:"getting-started"},"Getting started"),(0,s.kt)("p",null,"To get started with ",(0,s.kt)("a",{parentName:"p",href:"https://scala-sbt.org"},"sbt"),", simply add the following line to your ",(0,s.kt)("inlineCode",{parentName:"p"},"build.sbt")," file."),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies ++= List(\n  "company.jap" %% "fields-core" % "0.4.3",\n  "company.jap" %% "fields-zio" % "0.4.3",\n  "company.jap" %% "fields-cats" % "0.4.3",\n)\n')),(0,s.kt)("h2",{id:"code-teaser"},"Code teaser"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-scala"},'import jap.fields._\nimport jap.fields.DefaultAccumulateVM._\n\ncase class User(username: String, password: String, passwordRepeat: Option[String])\ncase class UserFeatures(standsWithUkraine: Boolean)\ncase class Request(user: User, features: UserFeatures)\nobject Request {\n  implicit val policy: Policy[Request] =\n    Policy\n      .builder[Request]\n      .subRule(_.user.username)(_.nonBlank, _.minSize(4))\n      .subRule(_.user.password)(_.nonBlank, _.minSize(8), _.maxSize(30))\n      .subRule(_.user.password, _.user.passwordRepeat)((p, pr) => pr.some(_ === p))\n      .rule { request =>\n        val standsWithUkraineF = request.sub(_.features.standsWithUkraine)\n        standsWithUkraineF.ensure(_ == true, _.failMessage("https://github.com/vshymanskyy/StandWithUkraine/blob/main/docs/README.md"))\n      }\n      .build\n}\n\nval request  = Request(User("Ann", "1234", Some("")), UserFeatures(false))\n// request: Request = Request(\n//   user = User(\n//     username = "Ann",\n//     password = "1234",\n//     passwordRepeat = Some(value = "")\n//   ),\n//   features = UserFeatures(standsWithUkraine = false)\n// )\nField(request).validate\n// res0: Rule[Sync, [E >: Nothing <: Any] => Accumulate[E], ValidationError] = Invalid(\n//   errors = List(\n//     MinSize(path = FieldPath(parts = List("user", "username")), size = 4),\n//     MinSize(path = FieldPath(parts = List("user", "password")), size = 8),\n//     Equal(\n//       path = FieldPath(parts = List("user", "passwordRepeat")),\n//       compared = "user.password"\n//     ),\n//     Message(\n//       path = FieldPath(parts = List("features", "standsWithUkraine")),\n//       error = "https://github.com/vshymanskyy/StandWithUkraine/blob/main/docs/README.md",\n//       message = None\n//     )\n//   )\n// )\n')),(0,s.kt)("p",null,"This is just the basics of Fields, but there is still plenty of syntax to learn, see other Documentation sections."),(0,s.kt)("h2",{id:"adopters"},"Adopters"),(0,s.kt)("p",null,"Is your company using Fields and want to be listed here?"),(0,s.kt)("p",null,"We will be happy to feature your company here, but in order to do that, we'll need written permission to avoid any legal misunderstandings."),(0,s.kt)("p",null,"Please open new ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/jap-company/fields/issues/new"},"Github Issue")," and provide us with your company name, logo and legal permission to add your company as."),(0,s.kt)("h2",{id:"sponsors"},"Sponsors"),(0,s.kt)("p",null,"Development and maintenance of Fields is sponsored by ",(0,s.kt)("a",{parentName:"p",href:"http://jap.company"},"Jap")),(0,s.kt)("p",null,(0,s.kt)("a",{parentName:"p",href:"http://jap.company"},(0,s.kt)("img",{parentName:"a",src:"https://raw.githubusercontent.com/jap-company/fields/master/assets/jap-logo.png",alt:null,title:"Jap"}))),(0,s.kt)("h2",{id:"license"},"License"),(0,s.kt)("p",null,"Licensed under the ",(0,s.kt)("a",{parentName:"p",href:"https://www.apache.org/licenses/LICENSE-2.0.html"},"Apache License 2.0"),". Refer to the ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/jap-company/fields/blob/master/LICENSE"},"license file"),"."))}f.isMDXComponent=!0},5949:function(e,t,n){n.r(t),n.d(t,{assets:function(){return u},contentTitle:function(){return l},default:function(){return m},frontMatter:function(){return o},metadata:function(){return p},toc:function(){return c}});var r=n(7462),a=n(3366),s=(n(7294),n(3905)),i=["components"],o={},l=void 0,p={unversionedId:"traits",id:"traits",title:"traits",description:"- **F**inal Tagless. Choose any Effect, Validated, or Error types.",source:"@site/../fields-docs/target/mdoc/traits.md",sourceDirName:".",slug:"/traits",permalink:"/fields/docs/traits",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/traits.md",tags:[],version:"current",frontMatter:{}},u={},c=[],d={toc:c};function m(e){var t=e.components,n=(0,a.Z)(e,i);return(0,s.kt)("wrapper",(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("ul",null,(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("em",{parentName:"li"},(0,s.kt)("strong",{parentName:"em"},"F"),"inal Tagless"),". Choose any Effect, Validated, or Error types."),(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("em",{parentName:"li"},(0,s.kt)("strong",{parentName:"em"},"I"),"nformative"),". Error paths help understanding where the error occurred."),(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("em",{parentName:"li"},(0,s.kt)("strong",{parentName:"em"},"E"),"xpressive"),". Rich, extendable validation syntax."),(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("em",{parentName:"li"},(0,s.kt)("strong",{parentName:"em"},"L"),"ightweight"),". The core module has no-dependencies."),(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("em",{parentName:"li"},(0,s.kt)("strong",{parentName:"em"},"D"),"auntless"),". Have no fear of complex validations with ",(0,s.kt)("inlineCode",{parentName:"li"},"Rule")," type."),(0,s.kt)("li",{parentName:"ul"},(0,s.kt)("em",{parentName:"li"},(0,s.kt)("strong",{parentName:"em"},"S"),"hort-circuit"),". Avoid running undesired validation side-effects.")))}m.isMDXComponent=!0}}]);