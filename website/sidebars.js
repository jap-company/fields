/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
  // docsSidebar: [{type: 'autogenerated', dirName: '.'}],
  docs: [
    "overview",
    "validation-module",
    "validation-syntax",
    "validation-error",
    "fail-with",
    {
      "Data Types": [
        "field",
        "fieldpath",
        "rule",
      ]
    },
    {
      "Type Classes": [
        "validated",
        "effect",
        "validation-policy",
      ]
    },
    "goals",
    "contributing",
  ]
};

module.exports = sidebars;