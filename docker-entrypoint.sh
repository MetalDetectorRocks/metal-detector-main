#!/bin/bash

file_env() {
	local var="$1"
	local fileVar="${var}_FILE"
	local def="${2:-}"
	if [ "${!var:-}" ] && [ "${!fileVar:-}" ]; then
		echo >&2 "error: both $var and $fileVar are set (but are exclusive)"
		exit 1
	fi
	local val="$def"
	if [ "${!var:-}" ]; then
		val="${!var}"
	elif [ "${!fileVar:-}" ]; then
		val="$(< "${!fileVar}")"
	fi

	export "$var"="$val"
	unset "$fileVar"
}

envs=(
  DATASOURCE_URL
  DATASOURCE_USERNAME
  DATASOURCE_PASSWORD
  BUTLER_ACCESS_TOKEN
  SPOTIFY_CLIENT_ID
  SPOTIFY_CLIENT_SECRET
  DISCOGS_ACCESS_TOKEN
  JWT_SECRET
  MAIL_HOST
  MAIL_USERNAME
  MAIL_PASSWORD
  REMEMBER_ME_SECRET
  ACTUATOR_INFO_PATH
  ACTUATOR_FLYWAY_PATH
  ACTUATOR_METRICS_PATH
  ACTUATOR_PROMETHEUS_PATH
  REGISTRATION_CODE
  GOOGLE_CLIENT_ID
  GOOGLE_CLIENT_SECRET
)

for e in "${envs[@]}"; do
  file_env "$e"
done

exec "$@"
