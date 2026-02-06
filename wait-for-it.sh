#!/usr/bin/env bash
set -euo pipefail

# wait-for-it.sh - wait for a host:port to become available before executing the given command

HOST="$1"
PORT="${2:-3306}"
shift 2

TIMEOUT=${TIMEOUT:-60}
CMD="$@"

echo "Waiting for ${HOST}:${PORT}..."
start_ts=$(date +%s)
while true; do
  if command -v nc >/dev/null 2>&1; then
    nc -z "$HOST" "$PORT" >/dev/null 2>&1
  else
    # Fallback to bash TCP test
    timeout 1 bash -lc ":" >/dev/null 2>&1 < /dev/tcp/$HOST/$PORT
  fi
  if [ $? -eq 0 ]; then
    echo "Connected to ${HOST}:${PORT}"
    break
  fi
  if [ "${TIMEOUT}" -gt 0 ] && [ $(date +%s) -ge $((start_ts + TIMEOUT)) ]; then
    echo "Timeout after ${TIMEOUT}s waiting for ${HOST}:${PORT}"
    exit 1
  fi
  sleep 1
done

exec ${CMD}
