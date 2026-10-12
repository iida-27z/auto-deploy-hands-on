#!/usr/bin/env sh
set -eu

APP_USER="$1"
APP_USER_HOME=$(getent passwd "${APP_USER}" | cut -d: -f6)
SSH_DIR="${APP_USER_HOME}/.ssh"
KEY_FILE="${SSH_DIR}/id_ed25519"
CONFIG_FILE="${SSH_DIR}/config"
SHARED_SSH_KEY_DIR="/mnt/devcontainer-ssh-key"
SHARED_SSH_CONFIG_DIR="/mnt/devcontainer-ssh-config"

chmod 755 "${APP_USER_HOME}"

mkdir -p "${SSH_DIR}"
chmod 700 "${SSH_DIR}"


if [ ! -f "${KEY_FILE}" ] && [ -f "${SHARED_SSH_KEY_DIR}/id_ed25519" ]; then
  cp "${SHARED_SSH_KEY_DIR}/id_ed25519" "${KEY_FILE}"
fi

if [ ! -f "${KEY_FILE}" ]; then
  echo "SSH private key was not found: ${KEY_FILE}" >&2
  exit 1
fi

chmod 600 "${KEY_FILE}"

if [ ! -f "${KEY_FILE}.pub" ] && [ -f "${SHARED_SSH_KEY_DIR}/id_ed25519.pub" ]; then
  cp "${SHARED_SSH_KEY_DIR}/id_ed25519.pub" "${KEY_FILE}.pub"
fi

if [ -f "${KEY_FILE}.pub" ]; then
  chmod 644 "${KEY_FILE}.pub"
fi

cp "${SHARED_SSH_CONFIG_DIR}/config" "${CONFIG_FILE}"
chmod 600 "${CONFIG_FILE}"
chown -R "${APP_USER}:${APP_USER}" "${APP_USER_HOME}"

shift
exec "$@"
