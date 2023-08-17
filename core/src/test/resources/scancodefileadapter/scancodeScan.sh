scancodeoptions="--processes 16 --copyright --only-findings --license --license-text --consolidate"
beginning=$(pwd)
mkdir scancodeOutput
so=$(pwd)/scancodeOutput
echo {\"artifacts\": [ > scancodeOutput/scancodeOut.json
dir=$(pwd)/Source

		if [ ! -f $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/scancode.json ] || [ ! -f $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/scancodeScan.completed ]
		then
			rm $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/scancodeScan.completed
			rm $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/scancode.json
			if [ -d $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/sources ] && [ -f $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/sources.completed ]
			then
				scancode $scancodeoptions --json-pp $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/scancode.json $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/sources
				touch $dir/pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0/scancodeScan.completed
			fi
		fi
	
printf "END" >> $so/scancodeOut.json
grep -v ",END" $so/scancodeOut.json > $so/scancodeOutput.json
rm $so/scancodeOut.json
echo ]} >> $so/scancodeOutput.json
mv $so/scancodeOutput.json $beginning

