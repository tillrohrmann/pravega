#!/bin/bash
#
# Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
host="$(hostname)"
####copy all container logs in a given host.
containersResult="$(docker ps -a -q|xargs)"
echo "Containers in $host are ${containersResult}"
containers=(${containersResult/%$'\r'/})
for container in "${containers[@]}"
do
   echo "Executing commands for $container"
   mkdir -p logs/$container
   docker cp $container:/opt/pravega/logs/. logs/$container/
   if [ $? -ne 0 ]; then
     echo "$container is not a controller/pravega node"
     continue
   fi
   tar -zcvf logs_$container.tar.gz ./logs/$container/

done
index=1
find / -name logs |grep "/merged/opt/pravega/logs"| while read -r path ; do
    echo "Copying logs from $path $index"
    mkdir -p logs/$host-container-$index
    cp -r $path/. logs/$host-container-$index/
    index=$((index+1))
done
if [ -d logs ]; then
  tar -zcvf logs_$host.tar.gz logs/
else
  echo "No pravega containers running on $host"
fi
#### copy all test logs on the given mesos host.
find / -name 'server.log' -print0 | tar -czvf testLogsIn-$host.tar.gz --null -T -

aws s3 cp /tmp/*.tar.gz  s3://my-tf-test-bucket-in-us-east-2/sample-run-1

