# Wesoga
WeSoGa (Web Socket Game) is a multiplayer game playable from a navigator

## How to play

* Launch the server
* Open your web browser at the address [localhost:6160](http://localhost:6160)

## How to contribute

* If you have found a bug you can report it by creating an [issue](https://github.com/ParallGames/Wesoga/issues)
* If you have an idea you can also create an [issue](https://github.com/ParallGames/Wesoga/issues) to suggest it
* You can also create a [pull request](https://github.com/ParallGames/Wesoga/pulls) to suggest code changes

## How to use a certificate

### Default self signed certificate
Add these options to java : -Djavax.net.ssl.keyStore=selfsigned.jks -Djavax.net.ssl.keyStorePassword=selfsigned -Djavax.net.ssl.trustStore=selfsigned.jks -Djavax.net.ssl.trustStorePassword=selfsigned

### Your own certificate
Add these options to java : -Djavax.net.ssl.keyStore=**JKS_PATH** -Djavax.net.ssl.keyStorePassword=**PASSWORD** -Djavax.net.ssl.trustStore=**JKS_PATH** -Djavax.net.ssl.trustStorePassword=**PASSWORD**