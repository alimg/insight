#!/bin/bash
CHILD=inno_main
SL=3
if [[ $# -gt 0 ]]; then
    CHILD=$1
fi

echo "Reread configs"
supervisorctl reread all

echo -e "\nUpdating $CHILD"
supervisorctl update $CHILD

echo -e "\nStopping $CHILD"
supervisorctl stop $CHILD

#echo -e "\nWaiting $SL secs"
#sleep $SL

echo -e "\nStarting $CHILD"
supervisorctl start $CHILD

echo -e "\nAll done :)"
