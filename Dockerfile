FROM amazoncorretto:24 AS builder

RUN dnf install -y npm
RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -o /usr/bin/lein && chmod +x /usr/bin/lein && /usr/bin/lein

WORKDIR /app

COPY . /app

RUN npm install && npm run build
RUN lein uberjar
RUN mv target/uberjar/*-standalone.jar /app/app.jar

FROM amazoncorretto:24

WORKDIR /app/data
WORKDIR /app

ENV APP_ENV="prod"
ENV APP_PORT="80"
ENV DATABASE_URL="jdbc:sqlite:/app/data/app.db"

COPY --from=builder /app/app.jar /app/app.jar

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl --fail http://localhost/health/up || exit 1

CMD ["java", "-jar", "/app/app.jar"]
