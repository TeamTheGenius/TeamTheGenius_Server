# health_check.sh

#!/bin/bash

# Crawl current connected port of WAS
CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

# Toggle port Number
if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081
else
    echo "> No WAS is connected to nginx"
    exit 1
fi

echo "> Start health check of WAS at http://localhost:${TARGET_PORT}/api/auth/health-check ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
    echo "> #${RETRY_COUNT} trying..."
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}"  http://localhost:${TARGET_PORT}/api/auth/health-check)

    if [ ${RESPONSE_CODE} -eq 200 ]; then
        echo "> New WAS successfully running"
        exit 0
    elif [ ${RETRY_COUNT} -eq 10 ]; then
        echo "> Health check failed."
        exit 1
    fi
    sleep 10

#    if [ ${CURRENT_PORT} -eq ${TARGET_PORT} ]; then
#      echo "> Health Check Failed."
#      exit 1
#    else
#      echo "> New WAS Successfully running."
#      exit 0
#    fi
done
