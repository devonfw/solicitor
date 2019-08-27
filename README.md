# Solicitor
Solicitor is a tool which helps managing Open Source Software used within projects. Information about Open Source Software used/bundled within applications
might be imported from various technical sources (like the output generated from the maven license plugin). Customizable rules are used for normalizing the
license information, selecting the appropriate licenses in dual-/multilicensing cases and evaluating if the setup complies with the restrictions defined
for the project. Text or spreadsheet (Excel) based reports might be created to document the results of the evaluation.

## Getting started
Solicitor is a self contained Spring-Boot Command Line Application.

After building solicitor via `mvn clean install` you might execute it with `java -jar target/solicitor.jar classpath:samples/solicitor_sample.cfg`.
This will use some prepackaged internal sample data (taken from Devon4J) and sample rulesets and generates sample reports in the current directory. 

See the [User Guide](src/main/asciidoc/solicitor_userguide.adoc) for further information about usage and configuration.
