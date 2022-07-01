"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[391],{3905:function(e,t,a){a.d(t,{Zo:function(){return c},kt:function(){return m}});var n=a(7294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function i(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function l(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?i(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function s(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},i=Object.keys(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var o=n.createContext({}),u=function(e){var t=n.useContext(o),a=t;return e&&(a="function"==typeof e?e(t):l(l({},t),e)),a},c=function(e){var t=u(e.components);return n.createElement(o.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},p=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,i=e.originalType,o=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),p=u(a),m=r,v=p["".concat(o,".").concat(m)]||p[m]||d[m]||i;return a?n.createElement(v,l(l({ref:t},c),{},{components:a})):n.createElement(v,l({ref:t},c))}));function m(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=a.length,l=new Array(i);l[0]=p;var s={};for(var o in t)hasOwnProperty.call(t,o)&&(s[o]=t[o]);s.originalType=e,s.mdxType="string"==typeof e?e:r,l[1]=s;for(var u=2;u<i;u++)l[u]=a[u];return n.createElement.apply(null,l)}return n.createElement.apply(null,a)}p.displayName="MDXCreateElement"},2791:function(e,t,a){a.r(t),a.d(t,{assets:function(){return c},contentTitle:function(){return o},default:function(){return m},frontMatter:function(){return s},metadata:function(){return u},toc:function(){return d}});var n=a(7462),r=a(3366),i=(a(7294),a(3905)),l=["components"],s={},o="Validated",u={unversionedId:"validated",id:"validated",title:"Validated",description:"Defines Validated capabilities for V[_], so that Fields know how to use it when validating.",source:"@site/../fields-docs/target/mdoc/validated.md",sourceDirName:".",slug:"/validated",permalink:"/fields/docs/validated",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/validated.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"Rule",permalink:"/fields/docs/rule"},next:{title:"Effect",permalink:"/fields/docs/effect"}},c={},d=[{value:"Instances",id:"instances",level:2},{value:"Accumulate",id:"accumulate",level:3},{value:"FailFast",id:"failfast",level:3},{value:"Syntax",id:"syntax",level:2},{value:"Create",id:"create",level:3},{value:"Operations",id:"operations",level:3},{value:"Fail Multiple Fields",id:"fail-multiple-fields",level:3}],p={toc:d};function m(e){var t=e.components,a=(0,r.Z)(e,l);return(0,i.kt)("wrapper",(0,n.Z)({},p,a,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"validated"},"Validated"),(0,i.kt)("p",null,"Defines Validated capabilities for ",(0,i.kt)("inlineCode",{parentName:"p"},"V[_]"),", so that Fields know how to use it when validating.\nAlso this typeclass has ",(0,i.kt)("inlineCode",{parentName:"p"},"strategy")," field that will give a hint for short-circuiting while validating."),(0,i.kt)("h2",{id:"instances"},"Instances"),(0,i.kt)("p",null,"Predefined instances:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"jap.fields.data.Accumulate")," - accumulates errors."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"jap.fields.data.FailFast")," - holds first error that occured. Built using ",(0,i.kt)("inlineCode",{parentName:"li"},"Either")),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"cats.data.ValidatedNel[_, Unit]")," - accumulates. Part of cats module"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"cats.data.ValidatedNec[_, Unit]")," - accumulates. Part of cats module"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"List")," - accumulates errors. If list is empty means result is valid else contains errors."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"Either[_, Unit]")," - fail fast error. ",(0,i.kt)("inlineCode",{parentName:"li"},"Right[Unit]")," is valid, ",(0,i.kt)("inlineCode",{parentName:"li"},"Left[E]")," is invalid holding error."),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"Option")," - fail fast error. ",(0,i.kt)("inlineCode",{parentName:"li"},"None")," is valid, ",(0,i.kt)("inlineCode",{parentName:"li"},"Some[E]")," is invalid holding error")),(0,i.kt)("p",null,"If you need you can use your own Validated data type by creating typeclass instance for it.\nExtend ",(0,i.kt)("inlineCode",{parentName:"p"},"AccumulateLike")," if your type should accumulate errors or if it should fail with first error occured use ",(0,i.kt)("inlineCode",{parentName:"p"},"FailFastLike")),(0,i.kt)("h3",{id:"accumulate"},"Accumulate"),(0,i.kt)("p",null,"Validated data type that accumulates errors."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"sealed trait Accumulate[+E]\nobject Accumulate {\n  case object Valid                       extends Accumulate[Nothing]\n  case class Invalid[+E](errors: List[E]) extends Accumulate[E]\n}\n")),(0,i.kt)("h3",{id:"failfast"},"FailFast"),(0,i.kt)("p",null,"Validated data type that returns first error occured. Declared as ",(0,i.kt)("inlineCode",{parentName:"p"},"Option[E]")," tagged type."),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"None")," is valid"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},"Some[E]")," holds the error")),(0,i.kt)("h2",{id:"syntax"},"Syntax"),(0,i.kt)("p",null,"Having Validated for your ",(0,i.kt)("inlineCode",{parentName:"p"},"V[_]")," in scope you can use such syntax"),(0,i.kt)("h3",{id:"create"},"Create"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'import jap.fields._\nimport jap.fields.data.Accumulate\nimport jap.fields.typeclass.Validated\nimport jap.fields.syntax.ValidatedSyntax._\n\nval V: Validated[Accumulate] = Accumulate\n// V: Validated[[E >: Nothing <: Any] => Accumulate[E]] = jap.fields.data.Accumulate$@739c9fdf\nval vr1 = V.valid\n// vr1: Accumulate[Nothing] = Valid\nval vr2 = V.invalid("ERR01")\n// vr2: Accumulate[String] = Invalid(errors = List("ERR01"))\nval vr3 = "ERR02".invalid[Accumulate]\n// vr3: Accumulate[String] = Invalid(errors = List("ERR02"))\nval vr4 = V.traverse(List("ERR01", "ERR02"))(V.invalid)\n// vr4: Accumulate[String] = Invalid(errors = List("ERR01", "ERR02"))\nV.sequence(List(vr1, vr2, vr3))\n// res0: Accumulate[String] = Invalid(errors = List("ERR01", "ERR02"))\n')),(0,i.kt)("h3",{id:"operations"},"Operations"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'vr1.isValid\n// res1: Boolean = true\nvr2.when(false)\n// res2: Accumulate[String] = Valid\nvr2.unless(true)\n// res3: Accumulate[String] = Valid\nvr2.asError("ERROR02")\n// res4: Accumulate[String] = Invalid(errors = List("ERROR02"))\nvr2.asInvalid(vr4)\n// res5: Accumulate[String] = Invalid(errors = List("ERR01", "ERR02"))\nvr2.isInvalid\n// res6: Boolean = true\nvr2.errors\n// res7: List[String] = List("ERR01")\nvr1 && vr2\n// res8: Accumulate[String] = Invalid(errors = List("ERR01"))\nvr2.and(vr3)\n// res9: Accumulate[String] = Invalid(errors = List("ERR01", "ERR02"))\nvr1 || vr2\n// res10: Accumulate[String] = Valid\nvr2.or(vr3)\n// res11: Accumulate[String] = Invalid(errors = List("ERR01", "ERR02"))\nList(vr1, vr2, vr3).sequence\n// res12: Accumulate[String] = Invalid(errors = List("ERR01", "ERR02"))\nList(vr1, vr1).sequence\n// res13: Accumulate[Nothing] = Valid\n')),(0,i.kt)("h3",{id:"fail-multiple-fields"},"Fail Multiple Fields"),(0,i.kt)("p",null,"V.traverse is very useful when you want to fail multiple Field`s with same error"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'import jap.fields.DefaultAccumulateVM._\nV.traverse(Field(FieldPath("1"), 1), Field(FieldPath("2"), 2))(_.failMessage("ERROR"))\n')))}m.isMDXComponent=!0}}]);