#!/usr/bin/env bash
set -euo pipefail

# Ensure newly created secret files are owner-readable/writable only.
umask 077

ENV_FILE=".env"
FORCE="${1:-}"

if [[ -f "$ENV_FILE" && "$FORCE" != "--force" ]]; then
  echo "$ENV_FILE already exists. Use --force to regenerate secrets."
  exit 0
fi

if command -v openssl >/dev/null 2>&1; then
  JWT_SECRET="$(openssl rand -base64 32 | tr -d '\n')"
else
  JWT_SECRET="$(head -c 32 /dev/urandom | base64 | tr -d '\n')"
fi

JWT_SECRET_BASE64="$(printf '%s' "$JWT_SECRET" | base64 | tr -d '\n')"

cat > "$ENV_FILE" <<EOF
JWT_SECRET=$JWT_SECRET
JWT_SECRET_BASE64=$JWT_SECRET_BASE64
EOF

chmod 600 "$ENV_FILE"

echo "Generated $ENV_FILE with JWT_SECRET and JWT_SECRET_BASE64."
