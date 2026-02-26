#!/bin/bash

set -e  # stop script if any command fails

PASSWORD='SuSeba@00#SuSeba'
JAR_LOCAL="./target/rent-tech-api-0.0.1-SNAPSHOT.jar"
REMOTE="root@209.97.161.90"
REMOTE_PATH="/home/jar/rent-tech-api-0.0.1-SNAPSHOT.jar"
LOG_FILE="deploy.log"

# Redirect ALL output (stdout + stderr) to log file
exec > >(tee -a $LOG_FILE) 2>&1

echo "===================================="
echo "Deployment started at $(date)"
echo "===================================="

echo "Stopping old application..."
if ! sshpass -p "$PASSWORD" ssh $REMOTE "pkill -f rent-tech-api-0.0.1-SNAPSHOT.jar || true"; then
  echo "⚠ Failed to stop old application"
fi

echo "Deleting old JAR..."
if ! sshpass -p "$PASSWORD" ssh $REMOTE "rm -f $REMOTE_PATH"; then
  echo "⚠ Failed to delete old JAR"
fi

echo "Uploading new JAR..."
if ! sshpass -p "$PASSWORD" scp $JAR_LOCAL $REMOTE:$REMOTE_PATH; then
  echo "❌ Upload failed!"
  exit 1
fi

echo "Starting application..."
if ! sshpass -p "$PASSWORD" ssh $REMOTE "
nohup java -jar $REMOTE_PATH > /home/jar/app.log 2>&1 &
"; then
  echo "❌ Failed to start application"
  exit 1
fi

echo "===================================="
echo "Deployment finished at $(date)"
echo "===================================="
