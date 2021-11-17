# Solicitor
Solicitor is a tool which helps managing Open Source Software used within projects. Information about Open Source Software used/bundled within applications
might be imported from various technical sources (like the output generated from the maven license plugin). Customizable rules are used for normalizing the
license information, selecting the appropriate licenses in dual-/multilicensing cases and evaluating if the setup complies with the restrictions defined
for the project. Text or spreadsheet (Excel) based reports might be created to document the results of the evaluation.

## Features

* Standalone Command Line Java Tool
* Importers for component/license information from
  * Maven
  * Gradle
  * NPM
  * Yarn
  * Pip
  * CSV (e.g. for manual entry of data)
* Rules processing (using Drools Rule Engine) controls the the different phases:
  * Normalizing / Enhancing of license information
  * Handling of multilicensing (including selection of applicable licenses) and re-licensing
  * Legal evaluation
* Rules to be defined as Decision Tables
* Sample Decision Tables included
* Automatic download and file based caching of license texts
  * Allows manual editing / reformatting of license text
* Output processing
  * Template based text (Velocity) and XLS generation
  * SQL based pre-processor (e.g. for filtering, aggregation)
  * Audit log which documents all applied rules for every item might be included in report
  * "Diff Mode" allows to mark data which has changed as compared to a previous run of Solicitor (in Velocity and XLS reporting)
* Customization
  * Project specific configuration (containing e.g. reporting templates, decision tables) allows to override/amend builtin configuration
  * Builtin configuration might be overridden/extended by configuration data contained in a single extension file (ZIP format)
  * This allows to safely provide organization specific rules and reporting templates to all projects of an organization (e.g. to reflect the specific OSS usage policy of the organization) 

## Usage

### Getting started
Solicitor is a self contained Spring-Boot Command Line Application.

After building Solicitor via `mvn clean install` you might execute it with `java -jar target/solicitor.jar -c classpath:samples/solicitor_sample.cfg`.
This will use some prepackaged internal sample data (taken from Devon4J) and sample rulesets and generates sample reports in the current directory. 

### User Guide
See the [User Guide](documentation/master-solicitor.asciidoc) for further information about usage and configuration. The User Guide is also contained in the executable jar. Calling Solicitor via `java -jar target/solicitor.jar -eug` will store the User Guide in PDF format in the current working directory.

## Tutorials
On the [devonfw Youtube Channel](https://www.youtube.com/channel/UCtb1p-24jus-QoXy49t9Xzg) you will find video tutorials on the usage of Solicitor:
* [Part 1: Quick Start](https://www.youtube.com/watch?v=cGZWR_KDdZo)
* [Part 2: Working with Projects - Basics](https://www.youtube.com/watch?v=jN7zaPrc3UM&)
* [Part 3: Working with Projects - Advanced](https://www.youtube.com/watch?v=BF7plNnPb44)
* [Part 4: Creating an Extension](https://www.youtube.com/watch?v=oswZ5l7mrO8)

## Implementation Details

The following diagram shows the Spring Beans of Solicitor including their major interconnections. See the Javadoc for more infomation.

![Solicitor Beans](https://github.com/devonfw-forge/solicitor/raw/master/src/main/java/com/devonfw/tools/solicitor/doc-files/solicitor_beans.png "Solicitor Spring Beans - See the Solicitor Javadoc for more information")

