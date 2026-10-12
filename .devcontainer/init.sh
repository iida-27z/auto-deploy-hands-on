#!/usr/bin/env sh
set -eu

# .env の作成
[ -f .devcontainer/.env ] || cp .devcontainer/.env.example .devcontainer/.env

# SSH 鍵の作成
mkdir -p .devcontainer/ssh/keys
KEY_PATH=".devcontainer/ssh/keys/id_ed25519"
PUBKEY_PATH="${KEY_PATH}.pub"
if [ ! -f "${KEY_PATH}" ] || [ ! -f "${PUBKEY_PATH}" ]; then
  rm -f "${KEY_PATH}" "${PUBKEY_PATH}"
  ssh-keygen -t ed25519 -N '' -C 'devcontainer' -f "${KEY_PATH}"
fi

# SSH 鍵の権限を設定
chmod 600 "${KEY_PATH}"
chmod 644 "${PUBKEY_PATH}"
