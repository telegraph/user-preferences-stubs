# User preferences stubs
Pretty much a wiremock container running on port 10000 with some canned json as responses.

## Have the mocks up and running
```sh
rm -rf user-preferences-stubs
git clone git@github.com:telegraph/user-preferences-stubs.git
cd user-preferences-stubs
docker-compose down
docker-compose up -d
cd -
```

## Endpoints

[Mock server](https://user-preferences-stubs-preprod.api-platforms-preprod.telegraph.co.uk/__admin/mappings).

[Example](https://user-preferences-stubs-preprod.api-platforms-preprod.telegraph.co.uk/mytelegraph/v0.4.0/user/my2ws23gnjvhkntngr2dcytmgeygindk/preferences/save/articles).

[Swagger file](https://github.com/telegraph/platforms-swagger-specs/blob/master/user-preferences-swagger.json).