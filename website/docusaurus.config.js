// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

const apiUrl = "api";

const local = {
  baseUrl: '/',
  url: 'http://localhost:3000',
}

const github = {
  baseUrl: '/fields/',
  url: 'https://jap-company.github.io',
}

const repoUrl = 'https://github.com/jap-company/fields'

// Uncomment for local development. Do not forget to comment back
// const site = local
const site = github

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Fields',
  tagline: 'Lightweight, enjoyable and extendable Scala validation library',
  url: site.url,
  baseUrl: site.baseUrl,
  onBrokenLinks: 'log',
  onBrokenMarkdownLinks: 'warn',
  favicon: '/img/favicon.svg',
  organizationName: 'jap-company',
  projectName: 'fields',

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          path: "../fields-docs/target/mdoc",
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: params => 'https://github.com/jap-company/fields/tree/master/docs/' + params.docPath,
        },
        theme: {
          customCss: [
            require.resolve('./src/css/custom.css'),
          ],
        },
      }),
    ],
  ],
  customFields: {
    titleIcon: "img/logo.svg",
    apiUrl,
    repoUrl,
  },
  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'Fields',
        logo: {
          alt: 'Fields Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'overview',
            position: 'left',
            label: 'Documention',
          },
          {
            label: 'Scaladoc',
            href: `${site.url}${site.baseUrl}${apiUrl}/`,
            position: 'left'
          },
          {
            label: 'GitHub',
            href: 'https://github.com/jap-company/fields',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Docs',
            items: [
              {
                label: 'Overview',
                to: '/docs/overview',
              },
              {
                label: 'Contributing',
                to: '/docs/contributing',
              },
              {
                label: 'Goals',
                to: '/docs/goals',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Stack Overflow',
                href: 'https://stackoverflow.com/questions/tagged/jap-fields',
              },
              {
                label: 'Telegram',
                href: 'https://t.me/jap_fields',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'GitHub',
                href: 'https://github.com/jap-company/fields',
              },
              {
                label: 'Scaladoc',
                href: `${site.url}${site.baseUrl}${apiUrl}/`,
                position: 'left'
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Fields, Jap Company. Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ["java", "scala"]
      },
    }),
};

module.exports = config;
