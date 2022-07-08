"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[694],{3905:(e,l,n)=>{n.d(l,{Zo:()=>s,kt:()=>f});var t=n(7294);function r(e,l,n){return l in e?Object.defineProperty(e,l,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[l]=n,e}function a(e,l){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);l&&(t=t.filter((function(l){return Object.getOwnPropertyDescriptor(e,l).enumerable}))),n.push.apply(n,t)}return n}function i(e){for(var l=1;l<arguments.length;l++){var n=null!=arguments[l]?arguments[l]:{};l%2?a(Object(n),!0).forEach((function(l){r(e,l,n[l])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(l){Object.defineProperty(e,l,Object.getOwnPropertyDescriptor(n,l))}))}return e}function u(e,l){if(null==e)return{};var n,t,r=function(e,l){if(null==e)return{};var n,t,r={},a=Object.keys(e);for(t=0;t<a.length;t++)n=a[t],l.indexOf(n)>=0||(r[n]=e[n]);return r}(e,l);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(t=0;t<a.length;t++)n=a[t],l.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var o=t.createContext({}),d=function(e){var l=t.useContext(o),n=l;return e&&(n="function"==typeof e?e(l):i(i({},l),e)),n},s=function(e){var l=d(e.components);return t.createElement(o.Provider,{value:l},e.children)},c={inlineCode:"code",wrapper:function(e){var l=e.children;return t.createElement(t.Fragment,{},l)}},p=t.forwardRef((function(e,l){var n=e.components,r=e.mdxType,a=e.originalType,o=e.parentName,s=u(e,["components","mdxType","originalType","parentName"]),p=d(n),f=r,v=p["".concat(o,".").concat(f)]||p[f]||c[f]||a;return n?t.createElement(v,i(i({ref:l},s),{},{components:n})):t.createElement(v,i({ref:l},s))}));function f(e,l){var n=arguments,r=l&&l.mdxType;if("string"==typeof e||r){var a=n.length,i=new Array(a);i[0]=p;var u={};for(var o in l)hasOwnProperty.call(l,o)&&(u[o]=l[o]);u.originalType=e,u.mdxType="string"==typeof e?e:r,i[1]=u;for(var d=2;d<a;d++)i[d]=n[d];return t.createElement.apply(null,i)}return t.createElement.apply(null,n)}p.displayName="MDXCreateElement"},5215:(e,l,n)=>{n.r(l),n.d(l,{assets:()=>o,contentTitle:()=>i,default:()=>c,frontMatter:()=>a,metadata:()=>u,toc:()=>d});var t=n(7462),r=(n(7294),n(3905));const a={},i="Rule",u={unversionedId:"rule",id:"rule",title:"Rule",description:"When using validation syntax result type of validation will be Rule\\[F, V, E\\] where F, V, E is your Effect, Validated and Error respectively.",source:"@site/../fields-docs/target/mdoc/rule.md",sourceDirName:".",slug:"/rule",permalink:"/fields/docs/rule",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/rule.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"FieldPath",permalink:"/fields/docs/fieldpath"},next:{title:"Validated",permalink:"/fields/docs/validated"}},o={},d=[{value:"Syntax",id:"syntax",level:2},{value:"Module",id:"module",level:3},{value:"Create",id:"create",level:3},{value:"Operations",id:"operations",level:3},{value:"For-comprehension",id:"for-comprehension",level:3}],s={toc:d};function c(e){let{components:l,...n}=e;return(0,r.kt)("wrapper",(0,t.Z)({},s,n,{components:l,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"rule"},"Rule"),(0,r.kt)("p",null,"When using validation syntax result type of validation will be Rule","[","F, V, E","]"," where F, V, E is your Effect, Validated and Error respectively."),(0,r.kt)("p",null,"No need to worry about additional allocations as Rule is tagged type for F","[","V","[","E","]","]"),(0,r.kt)("p",null,"You can easily convert between Rule","[","F, V, E","]"," and F","[","V","[","E","]","]"," back and forth for free"),(0,r.kt)("h2",{id:"syntax"},"Syntax"),(0,r.kt)("h3",{id:"module"},"Module"),(0,r.kt)("p",null,"ValidationModule contains MRule alias that can help with type inference."),(0,r.kt)("h3",{id:"create"},"Create"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.Eval\nimport jap.fields._\nimport jap.fields.error._\nimport jap.fields.fail._\nimport jap.fields.CatsInterop.fromCatsMonadDefer\n\nobject Validation extends AccumulateVM[Eval, ValidationError] with CanFailWithValidationError\nimport Validation._\n\ndef error(path: String) = ValidationError.Invalid(FieldPath(path))\n\nList[MRule](\n    Rule.valid,\n    Rule.invalid(error("Rule.invalid")),\n    Rule.pure(V.invalid(error("Rule.pure"))),\n    Rule.effect(Eval.now(V.invalid(error("Rule.effect")))),\n    Rule.defer(Rule.invalid(error("Rule.defer"))),\n    Rule(Eval.later(V.invalid(error("Rule.apply"))))\n).map(_.effect.value)\n// res0: List[Accumulate[ValidationError]] = List(\n//   Valid,\n//   Invalid(\n//     errors = List(Invalid(path = FieldPath(parts = List("Rule.invalid"))))\n//   ),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.pure"))))),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.effect"))))),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.defer"))))),\n//   Invalid(errors = List(Invalid(path = FieldPath(parts = List("Rule.apply")))))\n// )\n')),(0,r.kt)("h3",{id:"operations"},"Operations"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'Rule.invalid("Rule.unwrap").unwrap.value\n// res1: Accumulate[String] = Invalid(errors = List("Rule.unwrap"))\nRule.invalid("Rule.effect").effect.value\n// res2: Accumulate[String] = Invalid(errors = List("Rule.effect"))\nRule.and(Rule.invalid("Rule.and.1"), Rule.invalid("Rule.and.2")).effect.value\n// res3: Accumulate[String] = Invalid(\n//   errors = List("Rule.and.1", "Rule.and.2")\n// )\nRule.or(Rule.invalid("Rule.or"), Rule.valid).effect.value\n// res4: Accumulate[String] = Valid\nRule.when(true)(Rule.invalid("Rule.when")).effect.value\n// res5: Accumulate[String] = Invalid(errors = List("Rule.when"))\nRule.whenF(Eval.later(true))(Rule.invalid("Rule.whenF")).effect.value\n// res6: Accumulate[String] = Invalid(errors = List("Rule.whenF"))\nRule.ensure(V.invalid("Rule.ensure"))(false).effect.value\n// res7: Accumulate[String] = Invalid(errors = List("Rule.ensure"))\nRule.ensureF(V.invalid("Rule.ensure"))(Eval.later(false)).effect.value\n// res8: Accumulate[String] = Invalid(errors = List("Rule.ensure"))\nRule.andAll(List(Rule.invalid("Rule.andAll.1"), Rule.invalid("Rule.andAll.2"))).effect.value\n// res9: Accumulate[String] = Invalid(\n//   errors = List("Rule.andAll.1", "Rule.andAll.2")\n// )\nRule.orAll(List(Rule.invalid("Rule.andAll.1"), Rule.valid)).effect.value\n// res10: Accumulate[String] = Valid\nRule.modify(Rule.invalid(""))(_ => V.invalid("Rule.modify")).effect.value\n// res11: Accumulate[String] = Invalid(errors = List("Rule.modify"))\nRule.modifyM(Rule.invalid(""))(_ => Rule.invalid("Rule.modifyM")).effect.value\n// res12: Accumulate[String] = Invalid(errors = List("Rule.modifyM"))\n')),(0,r.kt)("h3",{id:"for-comprehension"},"For-comprehension"),(0,r.kt)("p",null,"Because Rule has custom map and flatMap you can also define validations like this:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"val intF = Field(4)\n// intF: Field[Int] = Field(path = FieldPath(parts = List()), value = 4)\nval rule =\n    for {\n        _ <- intF > 4\n        _ <- intF < 4\n        _ <- intF !== 4\n    } yield V.valid\n// rule: Rule[[A >: Nothing <: Any] => Eval[A], [E >: Nothing <: Any] => Accumulate[E], ValidationError] = cats.Eval$$anon$2@6c9919ca\n")),(0,r.kt)("p",null,"Be aware this is experimental and requires yielding V.valid."))}c.isMDXComponent=!0}}]);