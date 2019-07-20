 #!/usr/bin/env bash
cd ../
mvn release:rollback
mvn release:prepare
mvn release:perform -DuseReleaseProfile=false