#!/bin/bash
set -e

TARGET_VARS=(
  "PATH"
  "JAVA_HOME"
)

ENV_SCRIPT="/etc/profile.d/docker_env.sh"
> "$ENV_SCRIPT"
for var in "${TARGET_VARS[@]}"; do
  if [ -n "${!var+x}" ] && [ -n "${!var}" ]; then
    printf 'export %s="%s"\n' "$var" "${!var}" >> "$ENV_SCRIPT"
  fi
done

chmod 0644 "$ENV_SCRIPT"

exec "$@"
