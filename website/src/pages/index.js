import React from 'react';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import styles from './styles.module.css';
import variables from '@site/variables.js'
import CodeBlock from '@theme/CodeBlock';
import Traits from '@site/../docs/traits.md'

const badges = [
  {
    name: 'Scaladex',
    link: `https://index.scala-lang.org/jap-company/fields/${variables.coreModuleName}`,
    src: `https://index.scala-lang.org/jap-company/fields/${variables.coreModuleName}/latest-by-scala-version.svg?platform=jvm`,
  },
  {
    name: 'Maven Central',
    link: `https://maven-badges.herokuapp.com/maven-central/${variables.organization}/${variables.coreModuleName}_2.13`,
    src: `https://maven-badges.herokuapp.com/maven-central/${variables.organization}/${variables.coreModuleName}_2.13/badge.svg`,
  },
  {
    name: 'CI',
    link: `https://github.com/jap-company/fields/actions?query=workflow%3A%22CI%22`,
    src: `https://github.com/jap-company/fields/workflows/CI/badge.svg`,
  },
]

export default function Home() {
  const { siteConfig } = useDocusaurusContext();
  return (
    <Layout
      title={`${siteConfig.title} - ${siteConfig.tagline}`}
      description={siteConfig.tagline}
    >
      <div className={styles.homeContainer}>
        <div className={styles.projectTitle}>
          <span className={styles.projectName}>
            <img className={styles.projectTitleLogo} src={siteConfig.customFields.titleIcon} alt="Jap Logo" />
            <h1>{siteConfig.title}</h1>
          </span>
          <h2>{siteConfig.tagline}</h2>
        </div>

        <div className={styles.quicklinks}>
          <Link to="docs/overview" className="button button--primary" >
            Docs
          </Link>
          <Link href={`${siteConfig.url}${siteConfig.baseUrl}${siteConfig.customFields.apiUrl}/`} className="button button--primary" >
            Scaladoc
          </Link>
          <Link to={siteConfig.customFields.repoUrl} className="button button--primary" >
            GitHub
          </Link>
        </div>
      </div>
      <main className={styles.mainContainer}>
        <div className={styles.index}>
          <p className={styles.badges}>
            {badges.map(badge =>
              <Link href={badge.link} key={badge.name}>
                < img src={badge.src} alt={badge.name} />
              </Link>
            )}
          </p>

          <div>
            <Traits />
          </div>

          <br />
          <h3>Getting Started</h3>

          <p>To get started with <Link href="https://scala-sbt.org">sbt</Link>, simply add the following line to your `build.sbt` file.</p>

          <CodeBlock
            language="scala"
            showLineNumbers
          >
            {
              `libraryDependencies ++= List(
  "${variables.organization}" %% "${variables.coreModuleName}" % "${variables.version}",
  "${variables.organization}" %% "${variables.zioModuleName}" % "${variables.version}",
  "${variables.organization}" %% "${variables.catsModuleName}" % "${variables.version}",
)`
            }
          </CodeBlock>

          <p>Published for Scala {variables.scalaPublishVersions}. For changes, refer to the <Link href="https://github.com/jap-company/fields/releases">release notes</Link>.</p>
          <p>Project is under active development. Feedback and contributions welcome.</p>
        </div>
      </main>
    </Layout>
  );
}