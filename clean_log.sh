#!/bin/bash
LD=/root/flask/log
ERR_LOG=$LD/inno_web.stderr.log
cat $ERR_LOG >> $ERR_LOG\.1;
cat /dev/null > $ERR_LOG
