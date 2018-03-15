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

## Hit the endpoints

Get user preferences
```sh
curl localhost:10000/mytelegraph/v0.3.0/user/my2ws23gnjvhkntngr2dcytmgeygindk/preferences
```
Get user preferences with 404 response
```sh
curl localhost:10000/mytelegraph/v0.3.0/user/notFound/preferences
```
Update user preferences
```sh
curl localhost:10000/mytelegraph/v0.3.0/user/notFound/preferences
```