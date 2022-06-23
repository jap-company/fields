"use strict";(self.webpackChunkfields_docs=self.webpackChunkfields_docs||[]).push([[886],{3905:function(e,i,t){t.d(i,{Zo:function(){return p},kt:function(){return u}});var n=t(7294);function a(e,i,t){return i in e?Object.defineProperty(e,i,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[i]=t,e}function r(e,i){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);i&&(n=n.filter((function(i){return Object.getOwnPropertyDescriptor(e,i).enumerable}))),t.push.apply(t,n)}return t}function l(e){for(var i=1;i<arguments.length;i++){var t=null!=arguments[i]?arguments[i]:{};i%2?r(Object(t),!0).forEach((function(i){a(e,i,t[i])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):r(Object(t)).forEach((function(i){Object.defineProperty(e,i,Object.getOwnPropertyDescriptor(t,i))}))}return e}function o(e,i){if(null==e)return{};var t,n,a=function(e,i){if(null==e)return{};var t,n,a={},r=Object.keys(e);for(n=0;n<r.length;n++)t=r[n],i.indexOf(t)>=0||(a[t]=e[t]);return a}(e,i);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(n=0;n<r.length;n++)t=r[n],i.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(a[t]=e[t])}return a}var s=n.createContext({}),d=function(e){var i=n.useContext(s),t=i;return e&&(t="function"==typeof e?e(i):l(l({},i),e)),t},p=function(e){var i=d(e.components);return n.createElement(s.Provider,{value:i},e.children)},c={inlineCode:"code",wrapper:function(e){var i=e.children;return n.createElement(n.Fragment,{},i)}},f=n.forwardRef((function(e,i){var t=e.components,a=e.mdxType,r=e.originalType,s=e.parentName,p=o(e,["components","mdxType","originalType","parentName"]),f=d(t),u=a,m=f["".concat(s,".").concat(u)]||f[u]||c[u]||r;return t?n.createElement(m,l(l({ref:i},p),{},{components:t})):n.createElement(m,l({ref:i},p))}));function u(e,i){var t=arguments,a=i&&i.mdxType;if("string"==typeof e||a){var r=t.length,l=new Array(r);l[0]=f;var o={};for(var s in i)hasOwnProperty.call(i,s)&&(o[s]=i[s]);o.originalType=e,o.mdxType="string"==typeof e?e:a,l[1]=o;for(var d=2;d<r;d++)l[d]=t[d];return n.createElement.apply(null,l)}return n.createElement.apply(null,t)}f.displayName="MDXCreateElement"},3796:function(e,i,t){t.r(i),t.d(i,{assets:function(){return p},contentTitle:function(){return s},default:function(){return u},frontMatter:function(){return o},metadata:function(){return d},toc:function(){return c}});var n=t(7462),a=t(3366),r=(t(7294),t(3905)),l=["components"],o={},s="FailWith",d={unversionedId:"fail-with",id:"fail-with",title:"FailWith",description:"Fields has capability for user to use his own error type and this raises question how should library know for standart validation which error to use? FailWith* typeclasses gives this library ability to fail with specific errors.",source:"@site/../fields-docs/target/mdoc/fail-with.md",sourceDirName:".",slug:"/fail-with",permalink:"/fields/docs/fail-with",draft:!1,editUrl:"https://github.com/jap-company/fields/tree/master/docs/fail-with.md",tags:[],version:"current",frontMatter:{},sidebar:"docs",previous:{title:"Error",permalink:"/fields/docs/validation-error"},next:{title:"Field",permalink:"/fields/docs/field"}},p={},c=[{value:"Predefined",id:"predefined",level:2},{value:"ValidationModule",id:"validationmodule",level:2},{value:"Definition",id:"definition",level:2},{value:"Property specific",id:"property-specific",level:2}],f={toc:c};function u(e){var i=e.components,t=(0,a.Z)(e,l);return(0,r.kt)("wrapper",(0,n.Z)({},f,t,{components:i,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"failwith"},"FailWith"),(0,r.kt)("p",null,"Fields has capability for user to use his own error type and this raises question how should library know for standart validation which error to use? ",(0,r.kt)("inlineCode",{parentName:"p"},"FailWith*")," typeclasses gives this library ability to fail with specific errors.\nThis way you do not need to define all mappings to start using library if you do not intend to use some validation syntax.\nFirst parameter ",(0,r.kt)("inlineCode",{parentName:"p"},"E")," for each ",(0,r.kt)("inlineCode",{parentName:"p"},"FailWith*")," is error for which you define capability to fail with and second parameter ",(0,r.kt)("inlineCode",{parentName:"p"},"P")," stands for Field type, so that for different Field types there can be different ",(0,r.kt)("inlineCode",{parentName:"p"},"FailWith*")," instances."),(0,r.kt)("p",null,"Here is list of available typeclasses:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"trait FailWith[E, +P]\n    extends FailWithMessage[E, P]\n    with FailWithCompare[E, P]\n    with FailWithInvalid[E, P]\n    with FailWithEmpty[E, P]\n    with FailWithNonEmpty[E, P]\n    with FailWithMinSize[E, P]\n    with FailWithMaxSize[E, P]\n    with FailWithOneOf[E, P]\n")),(0,r.kt)("h2",{id:"predefined"},"Predefined"),(0,r.kt)("p",null,"There are predefined FailWith instances for:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"FailWithValidationMessageString - String representing ValidationMessage"),(0,r.kt)("li",{parentName:"ul"},"FailWithValidationTypeString - String representing ValidationType"),(0,r.kt)("li",{parentName:"ul"},"FailWithFieldStringValidationType - FieldError","[","String","]"," representing ValidationType"),(0,r.kt)("li",{parentName:"ul"},"FailWithFieldStringValidationMessage - FieldError","[","String","]"," representing ValidationMessage"),(0,r.kt)("li",{parentName:"ul"},"FailWithValidationError - ValidationError"),(0,r.kt)("li",{parentName:"ul"},"FailWithValidationMessage - ValidationMessage"),(0,r.kt)("li",{parentName:"ul"},"FailWithFieldError - wraps any error with FieldError")),(0,r.kt)("h2",{id:"validationmodule"},"ValidationModule"),(0,r.kt)("p",null,"Recommended place for FailWith instance is inside ValidationModule for default FailWith instances there is trait with instance name prefixed with Can that you can mix into your ValidationModule. For custom FailWith instances you can follow same practise."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"object FutureValidation extends AccumulateVM[Future, ValidationMessage] with CanFailWithValidationMessage\n")),(0,r.kt)("h2",{id:"definition"},"Definition"),(0,r.kt)("p",null,"You can can implement only required ",(0,r.kt)("inlineCode",{parentName:"p"},"FailWith*")," types or extends ",(0,r.kt)("inlineCode",{parentName:"p"},"FailWith")," and implement all of them.\nHere is example FailWith for String:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"implicit object FailWithValidationType extends FailWith.Base[String] {\n  def invalid[P](field: Field[P]): String                                                = ValidationTypes.Invalid\n  def empty[P](field: Field[P]): String                                                  = ValidationTypes.Empty\n  def nonEmpty[P](field: Field[P]): String                                               = ValidationTypes.NonEmpty\n  def minSize[P](size: Int)(field: Field[P]): String                                     = ValidationTypes.MinSize\n  def maxSize[P](size: Int)(field: Field[P]): String                                     = ValidationTypes.MaxSize\n  def oneOf[P](variants: Seq[P])(field: Field[P]): String                                = ValidationTypes.OneOf\n  def message[P](error: String, message: Option[String])(field: Field[P]): String        = error\n  def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): String = operation.constraint\n}\n")),(0,r.kt)("h2",{id:"property-specific"},"Property specific"),(0,r.kt)("p",null,"Some sunny day you may find that you want to have custom logic for failing ",(0,r.kt)("inlineCode",{parentName:"p"},"Field[P]"),".\nThe same day you can define Propert type specific ",(0,r.kt)("inlineCode",{parentName:"p"},"FailWith*")," instance:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import jap.fields._\nimport jap.fields.fail._\nimport jap.fields.error._\n\nobject Validation extends DefaultAccumulateVM {\n  implicit object IntFailWith\n      extends FailWithInvalid[ValidationError, Int]\n      with FailWithEmpty[ValidationError, Int] {\n    def invalid[P >: Int](field: Field[P]): ValidationError = ValidationError.Message(field.path, "Invalid int")\n    def empty[P >: Int](field: Field[P]): ValidationError   = ValidationError.Message(field.path, "Empty int")\n  }\n}\nimport Validation._\n\nval intF    = Field(1)\n// intF: Field[Int] = Field(path = FieldPath(parts = List()), value = 1)\nval stringF = Field("1")\n// stringF: Field[String] = Field(\n//   path = FieldPath(parts = List()),\n//   value = "1"\n// )\nintF.failInvalid\n// res0: Accumulate[ValidationError] = Invalid(\n//   errors = List(\n//     Message(\n//       path = FieldPath(parts = List()),\n//       error = "Invalid int",\n//       message = None\n//     )\n//   )\n// )\nintF.failEmpty\n// res1: Accumulate[ValidationError] = Invalid(\n//   errors = List(\n//     Message(\n//       path = FieldPath(parts = List()),\n//       error = "Empty int",\n//       message = None\n//     )\n//   )\n// )\nstringF.failInvalid\n// res2: Accumulate[ValidationError] = Invalid(\n//   errors = List(Invalid(path = FieldPath(parts = List())))\n// )\nstringF.failEmpty\n// res3: Accumulate[ValidationError] = Invalid(\n//   errors = List(Empty(path = FieldPath(parts = List())))\n// )\n')))}u.isMDXComponent=!0}}]);