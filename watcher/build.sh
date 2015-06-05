#!/bin/sh
# Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0.
set -e

# settings
basedir=`dirname $0`
basedir=$(cd "${basedir}" && pwd)
currDir=`pwd`

project="watcher"
srcDir="${basedir}/src"
libDir="${basedir}/barbarywatchservice/lib"
binDir="${basedir}/barbarywatchservice/bin"
distDir="${currDir}/out"
classDir="${distDir}/production/watcher"
CLASSPATH="${CLASSPATH:-}"
CLICOLOR="${CLICOLOR:-}"

javaVersion="1.7"

export DEBUG="${DEBUG:-0}"
verbose=""
javacArgs=""
verboseArgs=""
if [[ "$1" == "-v" ]]; then
    set -x
    shift
    verbose="-v"
    javacArgs="-g"
    verboseArgs="-Dverbose=true"
fi

if [[ "$1" == "-d" ]]; then
    shift
    export DEBUG=1
fi

colorArgs="-Dclicolor=true"
RED='\033[1;31m'
GREEN='\033[1;32m'
BLUE='\033[1;34m'
RESET='\033[0m'
if [[ "x${CLICOLOR}" == "x" ]]; then
    colorArgs="-Dclicolor=false"
    RED=""
    GREEN=""
    BLUE=""
    RESET=""
fi

cmd="${1:-war-watch}"

echo ${BLUE} "compiling watcher..." ${RESET}
CP="${CLASSPATH}"
for l in `find "${libDir}" "${binDir}" -type f -name '*.jar' | sort -r`; do
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

echo ${BLUE} "watching for changes.." ${RESET}
cd "${currDir}"
java -cp "${classDir}:${CP}" ${colorArgs} ${verboseArgs} MainWatcher "${cmd}"


echo ${GREEN} "...done" ${RESET}
