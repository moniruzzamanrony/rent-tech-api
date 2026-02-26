#!/bin/bash

set -euo pipefail  # strict mode

APP_NAME='rent_tech'
PASSWORD='SuSeba@00#SuSeba'
JAR_NAME="rent-tech-api-0.0.1-SNAPSHOT.jar"
JAR_LOCAL="./target/$JAR_NAME"
REMOTE="root@209.97.161.90"
REMOTE_PATH="/home/jar/$JAR_NAME"
LOG_FILE="deploy.log"

SSH_OPTIONS="-o StrictHostKeyChecking=no"

# Redirect ALL output (stdout + stderr) to log file AND console
exec > >(tee -a "$LOG_FILE") 2>&1

echo "===================================="
echo "Deployment started at $(date)"
echo "===================================="

# -----------------------------
# Stop old application
# -----------------------------
echo "Stopping old application..."
RUNNING=$(sshpass -p "$PASSWORD" ssh $SSH_OPTIONS $REMOTE "pgrep -f $JAR_NAME" || true)

if [ -n "$RUNNING" ]; then
    sshpass -p "$PASSWORD" ssh $SSH_OPTIONS $REMOTE "pkill -f $JAR_NAME"
    echo "✅ Application stopped (PID: $RUNNING)"
else
    echo "ℹ No running application found"
fi

# -----------------------------
# Delete old JAR
# -----------------------------
echo "Deleting old JAR..."
sshpass -p "$PASSWORD" ssh $SSH_OPTIONS $REMOTE "rm -f $REMOTE_PATH"

# -----------------------------
# Upload new JAR
# -----------------------------
echo "Uploading new JAR..."
sshpass -p "$PASSWORD" scp $SSH_OPTIONS "$JAR_LOCAL" $REMOTE:$REMOTE_PATH

# -----------------------------
# Start application
# -----------------------------
echo "Starting application..."
sshpass -p "$PASSWORD" ssh $SSH_OPTIONS $REMOTE "
nohup java -jar $REMOTE_PATH > /home/jar/$APP_NAME.log 2>&1 &
"

echo "===================================="
echo "Deployment finished at $(date)"
echo "===================================="
