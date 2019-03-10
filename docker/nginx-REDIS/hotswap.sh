#!/usr/bin/env bash
#local search=$1
replace=$1
swap="$replace:6379;"
foundServer=false

# Note the double quotes
while read line; do
    for word in $line; do
        if [ "$foundServer" = "true" ]; then
          address="$word"
          if [ "$address" = "$swap" ]; then
            echo You are already using that server
          else
            sed -i "0,/${address}/ s/${address}/${swap}/" /etc/nginx/nginx.conf
            break
          fi
        fi
        if [ "$word" = "server" ]; then
          foundServer=true
        fi
    done
    if [ "$foundServer" = "true" ]; then
        break
    fi
done</etc/nginx/nginx.conf
eval "/usr/sbin/nginx -s reload"