# Solicitor
Solicitor is a tool which helps managing Open Source Software used within projects. Information about Open Source Software used/bundled within applications
might be imported from various technical sources (like the output generated from the maven license plugin). Customizable rules are used for normalizing the
license information, selecting the appropriate licenses in dual-/multilicensing cases and evaluating if the setup complies with the restrictions defined
for the project. Text or spreadsheet (Excel) based reports might be created to document the results of the evaluation.

## Features

TBD

## Usage

### Getting started
Solicitor is a self contained Spring-Boot Command Line Application.

After building Solicitor via `mvn clean install` you might execute it with `java -jar target/solicitor.jar -c classpath:samples/solicitor_sample.cfg`.
This will use some prepackaged internal sample data (taken from Devon4J) and sample rulesets and generates sample reports in the current directory. 

### User Guide
See the [User Guide](src/main/asciidoc/solicitor_userguide.adoc) for further information about usage and configuration. The User Guide is also contained in the executable jar. Calling Solicitor via `java -jar target/solicitor.jar -ug` will store the User Guide in PDF format in the current working directory.

## Implementation Details

The following diagram shows the Spring Beans of Solicitor including their major interconnections. See the Javadoc for mor infomation.

![Solicitor Beans](https://github.com/devonfw-forge/solicitor/raw/master/src/main/java/com/devonfw/tools/solicitor/doc-files/solicitor_beans.png "Solicitor Spring Beans - See the Solicitor Javadoc for more information")

