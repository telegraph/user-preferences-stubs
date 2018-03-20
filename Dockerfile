FROM rodolpheche/wiremock:2.6.0-alpine

ADD stubs /home/wiremock

EXPOSE 8080