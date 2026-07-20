#!/bin/bash
# ============================================================================
# Script de respaldo de base de datos - SGI-FIIS
# Cumple con RNF-51 a RNF-54: Respaldo periódico de BD y archivos
# ============================================================================
# Uso: ./scripts/backup-db.sh [output-dir]
# Ejemplo (crontab - diario a las 2 AM):
#   0 2 * * * /opt/sgi-fiis/scripts/backup-db.sh /opt/sgi-fiis/backups
# ============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Configuración (sobrescribir vía variables de entorno o .env)
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-sgifiis}"
DB_USER="${DB_USER:-sgifiis}"
BACKUP_DIR="${1:-${BACKUP_DIR:-/opt/sgi-fiis/backups}}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"

# Cargar .env si existe
if [ -f "$PROJECT_DIR/.env" ]; then
    set -a
    source "$PROJECT_DIR/.env"
    set +a
fi

TIMESTAMP=$(date +'%Y%m%d_%H%M%S')
BACKUP_FILE="${BACKUP_DIR}/sgifiis_${TIMESTAMP}.sql.gz"
LOG_FILE="${BACKUP_DIR}/backup.log"

mkdir -p "$BACKUP_DIR"

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

log "=== Iniciando respaldo de base de datos ==="
log "Host: ${DB_HOST}:${DB_PORT}"
log "Base de datos: ${DB_NAME}"
log "Archivo: ${BACKUP_FILE}"

# Exportar password para pg_dump (no mostrar en logs)
eval "export PGPASSWORD=\"\${DB_PASSWORD:-}\""

if pg_dump --version > /dev/null 2>&1; then
    pg_dump \
        --host="$DB_HOST" \
        --port="$DB_PORT" \
        --dbname="$DB_NAME" \
        --username="$DB_USER" \
        --no-password \
        --format=custom \
        --verbose \
        2>> "$LOG_FILE" \
    | gzip > "$BACKUP_FILE"

    log "Respaldo completado: $(du -h "$BACKUP_FILE" | cut -f1)"
else
    log "ERROR: pg_dump no está instalado"
    exit 1
fi

unset PGPASSWORD

# Limpiar respaldos antiguos
log "Limpiando respaldos con más de ${RETENTION_DAYS} días..."
find "$BACKUP_DIR" -name "sgifiis_*.sql.gz" -type f -mtime "+${RETENTION_DAYS}" -delete
log "Limpieza completada"

log "=== Respaldo finalizado exitosamente ==="
