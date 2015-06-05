#!/bin/sh
# Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0.
set -e

# settings
basedir=`dirname $0`
basedir=$(cd "${basedir}" && pwd)
currDir=`pwd`

project="vid"
srcDir="${basedir}/src"
webDir="${basedir}/web"
jsDir="js"
jsTarget="min.js"
testDir="${basedir}/test"
testRptDir="${currDir}/test-output"
libDir="${basedir}/lib"
distDir="${currDir}/out"
classDir="${distDir}/production/vid"
artifactDir="${distDir}/artifacts/web_war_exploded"
testClassDir="${distDir}/test/vid"
apiDocDir="${distDir}/doc/api"
watcherDir="${basedir}/watcher"
jettyDir="${basedir}/jetty-runner"
jettyVersion="9.1.1.v20140108"
closureVersion="20140110"
CLASSPATH="${CLASSPATH:-}"
CLICOLOR="${CLICOLOR:-}"

javaVersion="1.7"
jdkJavadoc="http://docs.oracle.com/javase/7/docs/api/"

export TRACE="${TRACE:-0}"
export DEBUG="${DEBUG:-0}"
verbose=""
javacArgs=""
jettyJavaArgs="-Dorg.eclipse.jetty.LEVEL=WARN -Dorg.eclipse.jetty.server.handler.RequestLogHandler.LEVEL=OFF"
jettyArgs=""
javadocArgs="-quiet"
closureArgs="--logging_level WARNING --warning_level QUIET --summary_detail_level 1"
if [[ "$1" == "-v" ]]; then
    set -x
    shift
    verbose="-v"
    javacArgs="-g"
    jettyJavaArgs="-Dorg.eclipse.jetty.LEVEL=INFO"
    jettyConfig="${jettyDir}/jetty-requestlog.xml"
    jettyArgs="--config ${jettyConfig}"
    javadocArgs=""
    closureArgs="--logging_level FINEST --warning_level VERBOSE --summary_detail_level 3 --debug"
    export TRACE=1
fi

if [[ "$1" == "-d" ]]; then
    jettyJavaArgs="${jettyJavaArgs} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
    export DEBUG=1
    shift
fi

RED='\033[1;31m'
GREEN='\033[1;32m'
BLUE='\033[1;34m'
RESET='\033[0m'
if [[ "x${CLICOLOR}" == "x" ]]; then
    RED=""
    GREEN=""
    BLUE=""
    RESET=""
fi

read version < "${basedir}/VERSION" || : echo ignored
cmd="${1:-build}"

commands="help clean js-compile compile test integration-test war javadoc dist build run"
commands="$commands js-watch compile-watch test-watch integration-test-watch war-watch run-watch"

usage() {
    echo "./build.sh [-v [-d]] [command]"
    echo "Valid commands include:"
    for okCmd in ${commands}; do
        echo "  ${okCmd}"
    done
}

# validate
cmdOk=0
for okCmd in ${commands}; do
    if [[ "${cmd}" == "${okCmd}" ]]; then
        cmdOk=1
        break
    fi
done
[[ ${cmdOk} -ne 1 ]] && echo ${RED} && usage && echo ${RESET} && exit 1
[[ "${cmd}" == "help" ]] && usage && exit 0
if [[ "${cmd}" == "clean" || "${cmd}" == "dist" ]]; then
    echo ${BLUE} "cleaning..." ${RESET}
    rm -rf ${verbose} "${distDir}" "${webDir}/${jsTarget}" "${webDir}/${jsTarget}.map"
fi
[[ "${cmd}" == "clean" ]] && echo ${GREEN} "...done" ${RESET} && exit 0


if [[ "${cmd}" == *-watch ]]; then
    "${watcherDir}/build.sh" ${verbose} "${cmd}"
     echo ${GREEN} "...done" ${RESET}
     exit 0
fi

echo ${BLUE} "compiling javascript..." ${RESET}
cd "${webDir}"
rm -f ${verbose} "${jsTarget}" "${jsTarget}.map"
JS_FILES=""
for l in `find "${jsDir}" -type f -name '*.js' | sort`; do
    JS_FILES="$JS_FILES --js ${l}"
done
java -jar "${libDir}/closure-compiler-${closureVersion}.jar" \
    ${closureArgs} \
    --compilation_level ADVANCED_OPTIMIZATIONS \
    --create_source_map "${jsTarget}.map" \
    --js_output_file "${jsTarget}.tmp" \
    ${JS_FILES}
echo "// Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0." > "${jsTarget}"
cat "${jsTarget}.tmp" >> "${jsTarget}"
rm -f ${verbose} "${jsTarget}.tmp"
echo "//@ sourceMappingURL=${jsTarget}.map" >> "${jsTarget}"
    
[[ "${cmd}" == "js-compile" ]] && echo ${GREEN} "...done" ${RESET} && exit 0

echo ${BLUE} "compiling..." ${RESET}
CP="${CLASSPATH}"
for l in `find "${libDir}" -type f -name '*.jar' | sort -r`; do
    CP="${l}:${CP}"
done

rm -rf ${verbose} "${classDir}"
mkdir -p ${verbose} "${classDir}"
cd "${srcDir}"
set +e
javac ${javacArgs} -source "${javaVersion}" -target "${javaVersion}" \
        -d "${classDir}" \
        -cp "${classDir}:${CP}" \
        `find . -type f -name '*.java'`
if [[ $? -ne 0 ]]; then
    echo ${RED} "BUILD FAILED! (compile error)" ${RESET}
    exit 1
fi
set -e

rsync -a ${verbose} --exclude="*.java" ./ "${classDir}/"


[[ "${cmd}" == "compile" ]] && echo ${GREEN} "...done" ${RESET} && exit 0

if [[ "${cmd}" == "run" ]]; then
    echo ${BLUE} "skipping tests..." ${RESET}
else
    echo ${BLUE} "compiling tests..." ${RESET}
    rm -rf ${verbose} "${testClassDir}"
    mkdir -p ${verbose} "${testClassDir}"
    cd "${testDir}"
    TCP="${CP}"
    
    set +e
    javac ${javacArgs} -source "${javaVersion}" -target "${javaVersion}" \
            -d "${testClassDir}" \
            -cp "${testClassDir}:${classDir}:${TCP}" \
            `find . -type f -name '*.java'`
    if [[ $? -ne 0 ]]; then
        echo ${RED} "BUILD FAILED! (test compile error)" ${RESET}
        exit 1
    fi
    set -e
    
    rsync -a ${verbose} --exclude="*.java" ./ "${testClassDir}/"
    
    
    echo ${BLUE} "testing..." ${RESET}
    
    cd "${distDir}"
    testType="checkin"
    [[ "${cmd}" == "test" \
        || "${cmd}" == "dist" \
        || "${cmd}" == "build" ]] && testType="functional"
    [[ "${cmd}" == "integration-test" ]] && testType="integration"
    testNgXml="${testDir}/testng-${testType}.xml"
    
    rm -rf ${verbose} "${testRptDir}"
    set +e
    java -ea -cp "${testClassDir}:${classDir}:${TCP}" \
            org.testng.TestNG \
            -threadcount 8 \
            "${testNgXml}"
    if [[ $? -ne 0 ]]; then
        echo ${BLUE} "test report is ${currDir}/test-output/index.html" ${RESET}
        echo ${RED} "BUILD FAILED! (test failure)" ${RESET}
        exit 1
    fi
    set -e
    echo ${BLUE} "test report is ${currDir}/build/test-output/index.html" ${RESET}
    echo ${GREEN} "...${testType} tests ok" ${RESET}
fi

[[ "${cmd}" == "test" \
    || "${cmd}" == "integration-test" \
    || "${cmd}" == "build" ]] && echo ${GREEN} "...done" ${RESET} && exit 0

echo ${BLUE} "archiving..." ${RESET}
mkdir -p ${verbose} "${artifactDir}"
cd "${artifactDir}"
rsync -a ${verbose} "${webDir}/" ./
mkdir -p ${verbose} "${artifactDir}/META-INF"
cp "${basedir}/WAR_LICENSE.txt" "${artifactDir}/META-INF/LICENSE.txt"
mkdir -p ${verbose} "${artifactDir}/WEB-INF/classes"
rsync -a ${verbose} "${classDir}/" "${artifactDir}/WEB-INF/classes/"
mkdir -p ${verbose} "${artifactDir}/WEB-INF/lib"
rsync -a ${verbose} "${libDir}/include/" "${artifactDir}/WEB-INF/lib/"
echo "vid.version=${version}" >> "${artifactDir}/WEB-INF/classes/vid.properties"
cp "${basedir}/VERSION" "${artifactDir}/WEB-INF/VERSION"

mkdir -p ${verbose} "${distDir}"
jar ${verbose}cf "${distDir}/${project}-${version}.war" *

[[ "${cmd}" == "war" ]] && echo ${GREEN} "...done" ${RESET} && exit 0

if [[ "${cmd}" == "run" ]]; then
    echo ${BLUE} "running jetty on port 8080..." ${RESET}
    java ${jettyJavaArgs} \
        -jar "${jettyDir}/jetty-runner-${jettyVersion}.jar" \
        ${jettyArgs} \
        "${distDir}/${project}-${version}.war" &
    javaPid=$!
    trap "kill -KILL ${javaPid}" SIGKILL SIGTERM SIGINT
    wait
    echo ${GREEN} "...done" ${RESET}
    exit 0
fi

echo ${BLUE} "generating javadoc..." ${RESET}
javadoc ${javadocArgs} \
    -classpath "${CP}" \
    -sourcepath "${srcDir}" \
    -d "${apiDocDir}" \
    -windowtitle "${project} ${version} API" \
    -doctitle "${project} ${version} API" \
    -link "${jdkJavadoc}" \
    -subpackages io

echo ${BLUE} "javadocs in ${apiDocDir}" ${RESET}
[[ "${cmd}" == "javadoc" ]] && echo ${GREEN} "...done" ${RESET} && exit 0


echo ${BLUE} "packaging..." ${RESET}
rm -rf ${verbose} "${distDir}/${project}-${version}"
mkdir -p ${verbose} "${distDir}/${project}-${version}"
cp ${verbose} "${distDir}/${project}-${version}.war" "${distDir}/${project}-${version}"
cd "${basedir}"
rsync -a \
    ./ \
    "${distDir}/${project}-${version}/" \
    --exclude="/out" \
    --exclude="/.git" \
    --exclude="/.idea"
mkdir -p ${verbose} "${distDir}/${project}-${version}/doc"
rsync -a ${verbose} \
    "${distDir}/doc/" \
    "${distDir}/${project}-${version}/doc/"

cd "${distDir}/${project}-${version}/src"
mkdir -p ${verbose} META-INF
cp "${basedir}/WAR_LICENSE.txt" "META-INF/LICENSE.txt"
jar ${verbose}cf "../${project}-${version}-src.jar" *

cd "${distDir}"
#jar ${verbose}cf "${project}-${version}.zip" "${project}-${version}"
tar ${verbose}czf "${project}-${version}.tar.gz" "${project}-${version}"
echo ${BLUE} "distribution is ${distDir}/${project}-${version}.tar.gz" ${RESET}

echo ${GREEN} "...done" ${RESET}
