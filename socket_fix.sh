#!/bin/bash

ar=(5000 5013 5014)
for p in ${ar[*]}; do
    echo "Port: $p"
    netstat -ltnp | grep ":$p"
done
