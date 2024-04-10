#!/bin/bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_NAME="GitGetApplication.jar"
BUILD_JAR="$PROJECT_ROOT/$JAR_NAME"

TIME_NOW=$(date +%c)

#BUILD_JAR=$(ls /home/ubuntu/app/build/libs/*.jar)
#JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log

echo ">>> build 파일 복사" >> /home/ubuntu/deploy.log
#DEPLOY_PATH="$PROJECT_ROOT/build/libs"
cp $PROJECT_ROOT/build/libs/*.jar $BUILD_JAR

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> /home/ubuntu/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/deploy.log
else
  echo ">>> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi


DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포"    >> /home/ubuntu/deploy.log
nohup java -jar BUILD_JAR >> /home/ubuntu/deploy.log 2> /home/ubuntu/deploy_err.log &

# backup
#DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
#echo ">>> DEPLOY_JAR 배포"    >> /home/ubuntu/deploy.log
#nohup java -jar $DEPLOY_JAR >> /home/ubuntu/deploy.log 2> /home/ubuntu/deploy_err.log &
#
#BUILD_JAR=$(ls /home/ubuntu/app/build/libs/*.jar)
#JAR_NAME=$(basename $BUILD_JAR)
#echo ">>> build 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log
#
#echo ">>> build 파일 복사" >> /home/ubuntu/deploy.log
#DEPLOY_PATH=/home/ubuntu/
#cp $BUILD_JAR $DEPLOY_PATH
#
#echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> /home/ubuntu/deploy.log
#CURRENT_PID=$(pgrep -f $JAR_NAME)
#
#if [ -z $CURRENT_PID ]
#then
#  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/deploy.log
#else
#  echo ">>> kill -15 $CURRENT_PID"
#  kill -15 $CURRENT_PID
#  sleep 5
#fi
#
#DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
#echo ">>> DEPLOY_JAR 배포"    >> /home/ubuntu/deploy.log
#nohup java -jar $DEPLOY_JAR >> /home/ubuntu/deploy.log 2> /home/ubuntu/deploy_err.log &