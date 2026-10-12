#!/usr/bin/env sh
set -eu

APP_USER="$1"
APP_USER_HOME=$(getent passwd "${APP_USER}" | cut -d: -f6)
SSH_DIR="${APP_USER_HOME}/.ssh"
PUBKEY_FILE="/mnt/devcontainer-ssh-key/id_ed25519.pub"
AUTHORIZED_KEYS_FILE="${SSH_DIR}/authorized_keys"

mkdir -p "${SSH_DIR}"
chmod 700 "${SSH_DIR}"

if [ ! -s "${PUBKEY_FILE}" ]; then
  echo "SSH public key was not found: ${PUBKEY_FILE}" >&2
  exit 1
fi

passwd -d "${APP_USER}" >/dev/null
cat "${PUBKEY_FILE}" > "${AUTHORIZED_KEYS_FILE}"
chmod 600 "${AUTHORIZED_KEYS_FILE}"
chown -R "${APP_USER}:${APP_USER}" "${APP_USER_HOME}"

shift
exec "$@"
