#!/bin/bash

set -euo pipefail

PASSWORD='AVNSHiB7Cg0kpa4D8JOESP'
REMOTE="root@213.199.36.174"
REMOTE_DIR="/root/rent-tech"
ARCHIVE="/tmp/rent-tech.tar.gz"
LOG_FILE="deploy.log"

SSH="sshpass -p $PASSWORD ssh -o StrictHostKeyChecking=no $REMOTE"
SCP="sshpass -p $PASSWORD scp -o StrictHostKeyChecking=no"

exec > >(tee -a "$LOG_FILE") 2>&1

echo "===================================="
echo "Deployment started at $(date)"
echo "===================================="

# -----------------------------
# Package source
# -----------------------------
echo "Packaging source..."
tar --exclude='.git' \
    --exclude='target' \
    --exclude='*.log' \
    -czf "$ARCHIVE" .

# -----------------------------
# Upload
# -----------------------------
echo "Uploading to server..."
$SSH "mkdir -p $REMOTE_DIR"
$SCP "$ARCHIVE" "$REMOTE:$REMOTE_DIR/rent-tech.tar.gz"
rm -f "$ARCHIVE"

# -----------------------------
# Build and deploy
# -----------------------------
echo "Building and deploying..."
$SSH "
  cd $REMOTE_DIR
  tar -xzf rent-tech.tar.gz
  rm -f rent-tech.tar.gz
  docker compose down
  docker compose up --build -d
"

echo "===================================="
echo "Deployment finished at $(date)"
echo "===================================="
