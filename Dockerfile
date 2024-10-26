# Fase di build
FROM quay.io/keycloak/keycloak:22.0.3 as builder

# Installazione del web server nginx (se richiesto per Keycloak)
RUN dnf install -y nginx \
    && dnf clean all \
    && rm -rf /var/cache/yum

# Configura il database per Postgres
ENV KC_DB=postgres

# Directory di lavoro
WORKDIR /opt/keycloak

# Generazione di una chiave per il certificato di sviluppo
RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore

# Fase finale dell'immagine
FROM quay.io/keycloak/keycloak:22.0.3
COPY --from=builder /opt/keycloak/ /opt/keycloak/

# Copia il tema personalizzato, se esiste (opzionale)
COPY ./themes/my-theme /opt/keycloak/themes/my-theme

# Comando di avvio di Keycloak
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]
