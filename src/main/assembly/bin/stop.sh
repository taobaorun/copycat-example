#!/bin/sh
BASEDIR=`dirname $0`
BASEDIR=`(cd "$BASEDIR"; pwd)`
echo current path $BASEDIR

COPYCAT_APP="copycat-example"
COPYCAT_PIDPATH="$BASEDIR"

if [ "$1" != "" ]; then
    PROVIDER_APP="$1"
fi

if [ "$2" != "" ]; then
    COPYCAT_PIDPATH="$2"
fi

PIDFILE=$COPYCAT_PIDPATH"/"$COPYCAT_APP"_startup.pid"
echo $PIDFILE

if [ ! -f "$PIDFILE" ]
then
    echo "no registry to stop (could not find file $PIDFILE)"
else
    kill $(cat "$PIDFILE")
    rm -f "$PIDFILE"
    echo STOPPED
fi
exit 0

echo stop finished.
