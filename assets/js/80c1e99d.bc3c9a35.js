"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[694],{3905:function(e,n,t){t.d(n,{Zo:function(){return s},kt:function(){return p}});var r=t(7294);function l(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function a(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function i(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?a(Object(t),!0).forEach((function(n){l(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):a(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function u(e,n){if(null==e)return{};var t,r,l=function(e,n){if(null==e)return{};var t,r,l={},a=Object.keys(e);for(r=0;r<a.length;r++)t=a[r],n.indexOf(t)>=0||(l[t]=e[t]);return l}(e,n);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)t=a[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(l[t]=e[t])}return l}var o=r.createContext({}),d=function(e){var n=r.useContext(o),t=n;return e&&(t="function"==typeof e?e(n):i(i({},n),e)),t},s=function(e){var n=d(e.components);return r.createElement(o.Provider,{value:n},e.children)},c={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},f=r.forwardRef((function(e,n){var t=e.components,l=e.mdxType,a=e.originalType,o=e.parentName,s=u(e,["components","mdxType","originalType","parentName"]),f=d(t),p=l,v=f["".concat(o,".").concat(p)]||f[p]||c[p]||a;return t?r.createElement(v,i(i({ref:n},s),{},{components:t})):r.createElement(v,i({ref:n},s))}));function p(e,n){var t=arguments,l=n&&n.mdxType;if("string"==typeof e||l){var a=t.length,i=new Array(a);i[0]=f;var u={};for(var o in n)hasOwnProperty.call(n,o)&&(u[o]=n[o]);u.originalType=e,u.mdxType="string"==typeof e?e:l,i[1]=u;for(var d=2;d<a;d++)i[d]=t[d];return r.createElement.apply(null,i)}return r.createElement.apply(null,t)}f.displayName="MDXCreateElement"},5215:function(e,n,t){t.r(n),t.d(n,{assets:function(){return s},contentTitle:function(){return o},default:function(){return p},frontMatter:function(){return u},metadata:function(){return d},toc:function(){return c}});var r=t(7462),l=t(3366),a=(t(7294),t(3905)),i=["components"],u={},o="Rule",d={unversionedId:"rule",id:"rule",title:"Rule",description:"When using validation syntax result type of validation will be Rule\\[F, V, E\\] where F, V, E is your Effect, Validated and Error respectively.",source:"@site/../fields-docs/target/mdoc/rule.md",sourceDirName:".",slug:"/rule",permalink:"/fields/docs/rule",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/rule.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"FieldPath",permalink:"/fields/docs/fieldpath"},next:{title:"Validated",permalink:"/fields/docs/validated"}},s={},c=[{value:"Syntax",id:"syntax",level:2},{value:"Module",id:"module",level:3},{value:"Create",id:"create",level:3},{value:"Operations",id:"operations",level:3},{value:"For-comprehension",id:"for-comprehension",level:3}],f={toc:c};function p(e){var n=e.components,t=(0,l.Z)(e,i);return(0,a.kt)("wrapper",(0,r.Z)({},f,t,{components:n,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"rule"},"Rule"),(0,a.kt)("p",null,"When using validation syntax result type of validation will be Rule","[","F, V, E","]"," where F, V, E is your Effect, Validated and Error respectively."),(0,a.kt)("p",null,"No need to worry about additional allocations as Rule is tagged type for F","[","V","[","E","]","]"),(0,a.kt)("p",null,"You can easily convert between Rule","[","F, V, E","]"," and F","[","V","[","E","]","]"," back and forth for free"),(0,a.kt)("h2",{id:"syntax"},"Syntax"),(0,a.kt)("h3",{id:"module"},"Module"),(0,a.kt)("p",null,"ValidationModule contains MRule alias that can help with type inference."),(0,a.kt)("h3",{id:"create"},"Create"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.Eval\nimport jap.fields._\nimport jap.fields.error._\nimport jap.fields.fail._\nimport jap.fields.CatsInterop.fromCatsMonadDefer\n\nobject Validation extends AccumulateVM[Eval, ValidationError] with CanFailWithValidationError\nimport Validation._\n\ndef error(path: String) = ValidationError.Invalid(FieldPath(path))\n\nList[MRule](\n    Rule.valid,\n    Rule.invalid(error("Rule.invalid")),\n    Rule.pure(V.invalid(error("Rule.pure"))),\n    Rule.effect(Eval.now(V.invalid(error("Rule.effect")))),\n    Rule.defer(Rule.invalid(error("Rule.defer"))),\n    Rule(Eval.later(V.invalid(error("Rule.apply"))))\n).map(_.effect.value)\n// res0: List[Accumulate[ValidationError]] = List(\n//   Valid,\n//   Invalid(\n//     errors = List(Invalid(path = FieldPath(parts = List("Rule.invalid"))))\n//   ),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.pure"))))),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.effect"))))),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.defer"))))),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.apply")))))\n// )\n')),(0,a.kt)("h3",{id:"operations"},"Operations"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-scala"},'Rule.invalid("Rule.unwrap").unwrap.value\n// res1: Accumulate[String] = Invalid(errors = List("Rule.unwrap"))\nRule.invalid("Rule.effect").effect.value\n// res2: Accumulate[String] = Invalid(errors = List("Rule.effect"))\nRule.and(Rule.invalid("Rule.and.1"), Rule.invalid("Rule.and.2")).effect.value\n// res3: Accumulate[String] = Invalid(\n//   errors = List("Rule.and.1", "Rule.and.2")\n// )\nRule.or(Rule.invalid("Rule.or"), Rule.valid).effect.value\n// res4: Accumulate[String] = Valid\nRule.when(true)(Rule.invalid("Rule.when")).effect.value\n// res5: Accumulate[String] = Invalid(errors = List("Rule.when"))\nRule.whenF(Eval.later(true))(Rule.invalid("Rule.whenF")).effect.value\n// res6: Accumulate[String] = Invalid(errors = List("Rule.whenF"))\nRule.ensure(V.invalid("Rule.ensure"))(false).effect.value\n// res7: Accumulate[String] = Invalid(errors = List("Rule.ensure"))\nRule.ensureF(V.invalid("Rule.ensure"))(Eval.later(false)).effect.value\n// res8: Accumulate[String] = Invalid(errors = List("Rule.ensure"))\nRule.andAll(List(Rule.invalid("Rule.andAll.1"), Rule.invalid("Rule.andAll.2"))).effect.value\n// res9: Accumulate[String] = Invalid(\n//   errors = List("Rule.andAll.1", "Rule.andAll.2")\n// )\nRule.orAll(List(Rule.invalid("Rule.andAll.1"), Rule.valid)).effect.value\n// res10: Accumulate[String] = Valid\nRule.modify(Rule.invalid(""))(_ => V.invalid("Rule.modify")).effect.value\n// res11: Accumulate[String] = Invalid(errors = List("Rule.modify"))\nRule.modifyM(Rule.invalid(""))(_ => Rule.invalid("Rule.modifyM")).effect.value\n// res12: Accumulate[String] = Invalid(errors = List("Rule.modifyM"))\n')),(0,a.kt)("h3",{id:"for-comprehension"},"For-comprehension"),(0,a.kt)("p",null,"Because Rule has custom map and flatMap you can also define validations like this:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-scala"},"val intF = Field(4)\n// intF: Field[Int] = Field(path = FieldPath(parts = List()), value = 4)\nval rule =\n    for {\n        _ <- intF > 4\n        _ <- intF < 4\n        _ <- intF !== 4\n    } yield V.valid\n// rule: Rule[[A >: Nothing <: Any] => Eval[A], [E >: Nothing <: Any] => Accumulate[E], ValidationError] = cats.Eval$$anon$2@65154a0\n")),(0,a.kt)("p",null,"Be aware this is experimental and requires yielding V.valid."))}p.isMDXComponent=!0}}]);