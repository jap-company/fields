// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const {themes} = require('prism-react-renderer');
const lightCodeTheme = themes.github;
const darkCodeTheme = themes.dracula;

const apiUrl = 'api';
const benchmarksUrl = 'benchmarks';
const repoUrl = 'https://github.com/jap-company/fields'

const site = {
  baseUrl: '/fields/',
  url: 'https://jap-company.github.io',
}
const title = 'Fields'
const tagline = 'Scala validation library written in Tagless Final.'
const keywords = ['fields', 'scala', 'validation', 'library', 'macros', 'zio', 'cats', 'short-circuit', 'scala 3', 'scala 2.12', 'scala 2.13', 'tagless-final', 'error paths']

/** @type {import('@docusaurus/types').Config} */
const config = {
  title,
  tagline,
  url: site.url,
  baseUrl: site.baseUrl,
  onBrokenLinks: 'throw',
  markdown: {
    hooks: {
      onBrokenMarkdownLinks: 'throw',
    },
  },
  favicon: 'img/favicon.svg',
  organizationName: 'jap-company',
  projectName: 'fields',
  trailingSlash: true,

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace 'en' with 'zh-Hans'.
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
          path: 'generated-docs',
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: params => 'https://github.com/jap-company/fields/edit/v2/docs/' + params.docPath,
        },
        theme: {
          customCss: [
            require.resolve('./src/css/custom.css'),
          ],
        },
        gtag: {
          trackingID: 'G-GQTXEF7061',
          anonymizeIP: true,
        },
      }),
    ],
  ],
  customFields: {
    titleIcon: 'img/logo.svg',
    apiUrl,
    repoUrl,
  },
  themeConfig:
  /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      metadata: [
        {name: 'keywords', content: keywords.join(', ')},
        {name: 'description', content: `${title} - ${tagline}`},
      ],
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
            label: 'Documentation',
          },
          {
            to: 'benchmarks',
            label: 'Benchmarks',
            position: 'left'
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
      announcementBar: {
        id: 'support_ukraine',
        content:
          'Support Ukraine 🇺🇦 <a target="_blank" rel="noopener noreferrer" \
            href="https://u24.gov.ua/"> Help Provide Aid to Ukraine</a>.',
        backgroundColor: '#20232a',
        textColor: '#fff',
        isCloseable: false,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['java', 'scala']
      },
      algolia: {
        appId: '0C7XECP0Q2',
        apiKey: '49b43131bbbdf6f5fe3a5d78d76843f5',
        indexName: 'fields',
      },
    }),
};

module.exports = config;
