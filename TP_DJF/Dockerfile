FROM gradle:7.6-jdk11 AS builder

WORKDIR /app
COPY . .
RUN gradle clean war --no-daemon

FROM tomcat:9.0-jdk11-temurin

ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH
ENV APP_HOME=/app

USER root


RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/* && \
    mkdir -p /app/logs && \
    chmod 755 /app/logs


RUN ln -sf /app/logs $CATALINA_HOME/logs

COPY --from=builder /app/build/libs/*.war $CATALINA_HOME/webapps/ROOT.war

# Exposer le port pour les logs (optionnel)
VOLUME /app/logs

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

CMD ["catalina.sh", "run"]