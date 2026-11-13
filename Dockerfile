# Debian is used (not Ubuntu) because Chromium there is a real package, not a snap stub.
# Everything needed to run the tests - JDK, Maven, Chromium, chromedriver - lives in the image.
FROM debian:bookworm-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        openjdk-17-jdk-headless maven chromium chromium-driver ca-certificates fonts-liberation \
    && rm -rf /var/lib/apt/lists/*

ENV CHROME_BIN=/usr/bin/chromium \
    CHROMEDRIVER_BIN=/usr/bin/chromedriver

WORKDIR /app

# Resolve dependencies first so this layer is cached until pom.xml changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q test-compile

# Default: run everything. Override the command to run a subset, e.g.
#   docker run --rm qa-automation-tests mvn -B test -Dgroups=api
CMD ["mvn", "-B", "test"]
