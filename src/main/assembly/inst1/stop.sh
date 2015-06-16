#!/bin/sh
SAF_INSTANCE="saf01"
SAF_PIDPATH=`pwd`
sh /export/data/tomcatRoot/saf21-registry/bin/stop.sh $SAF_INSTANCE $SAF_PIDPATH
