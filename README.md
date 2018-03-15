# User preferences stubs
Pretty much a wiremock container running on port 10000 with some canned json as responses.

# HOWTO

## Have the mocks up and running
```sh
rm -rf user-preferences-stubs
git clone git@github.com:telegraph/user-preferences-stubs.git
cd user-preferences-stubs
docker-compose down
docker-compose up -d
cd -
```

# Endpoints

Please, check the preferences [swagger file](https://github.com/telegraph/platforms-swagger-specs/blob/master/user-preferences-swagger.json).