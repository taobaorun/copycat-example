#!/bin/sh

BASEDIR=`dirname $0`/..
BASEDIR=`(cd "$BASEDIR"; pwd)`
echo current path:$BASEDIR

BASEBIN_DIR=$BASEDIR"/bin"
cd $BASEBIN_DIR

COPYCAT_APP="copycat-example"
COPYCAT_LOGPATH="server1"
COPYCAT_PIDPATH="$BASEBIN_DIR"

if [ "$1" != "" ]; then
    COPYCAT_APP="$1"
fi

if [ "$2" != "" ]; then
    COPYCAT_PIDPATH="$2"
fi


# ------ check if server is already running
PIDFILE=$COPYCAT_PIDPATH"/"$COPYCAT_APP"_startup.pid"
#if [ -f $PIDFILE ]; then
#    if kill -0 `cat $PIDFILE` > /dev/null 2>&1; then
#        echo server already running as process `cat $PIDFILE`.
#        exit 0
#    fi
#fi

# ------ set JAVACMD
if [ -r "$BASEBIN_DIR/setenv.sh" ]; then
  . "$BASEBIN_DIR/setenv.sh"
fi
# If a specific java binary isn't specified search for the standard 'java' binary
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java`
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

# ------ set CLASSPATH
CLASSPATH="$BASEDIR"/conf/:"$BASEDIR"/lib/*
echo "$CLASSPATH"

# ------ set jvm memory
if [ -z "$OPTS_MEMORY" ] ; then
    OPTS_MEMORY="-Xms1024m -Xmx1024m -Xmn512m"
fi

DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
#JPDA_OPTS="-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"
# ------ run proxy
#nohup "$JAVACMD" $JAVA_OPTS $JPDA_OPTS \
nohup "$JAVACMD" \
  $OPTS_MEMORY  \
  -classpath "$CLASSPATH" \
  -Dbasedir="$BASEDIR" \
  -Dfile.encoding="UTF-8" \
  -DCOPYCAT_APP="$COPYCAT_APP" \
  -DCOPYCAT_LOGPATH="$COPYCAT_LOGPATH" \
  -Dio.netty.leakDetectionLevel=advanced \
  com.jiaxy.copycat.example.cluster.collection.AsyncMapExample \
  > /dev/null &


# ------ wirte pid to file
if [ $? -eq 0 ]
then
    if /bin/echo -n $! > "$PIDFILE"
    then
        sleep 1
        echo STARTED SUCCESS
    else
        echo FAILED TO WRITE PID
        exit 1
    fi
#    tail -100f $LOGFILE
else
    echo SERVER DID NOT START
    exit 1
fi
