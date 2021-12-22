This directory contains a starter project configuration for a new Solicitor project

- "solicitor.cfg" is a basic project configuration - starting point for your own configuration
- folder "input" contains a sample input file (maven format)
- folder "rules" contains (empty) XLS files for defining project specific rules which have higher priority than the
  builtin default rules. Such project specific rules are optional. Just delete some or all of the files if you don't
  need them. They will be dynamically picked up so you can easily add them later.

Use "java -jar solicitor.jar -c file:path/to/your/project/solicitor.cfg" to execute Solicitor 

Output will be produced in directory "output".

For details on the usage and configuration consult the User Guide or contact your nearest "Solicitor Guru"
The User Guide might be obtained by calling Solicitor with command line option "-eug".
The bultin base configuration including all rules and templates can be exported to the filesystem with option "-ec".

