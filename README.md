# TweetStreamer

TweetStreamer is a command line tool for collecting tweets via Twitter's public streaming API.

## Example

Running the command below will collect tweets written in English that mention at least one of the republican party presidential candidates for the 2016 United States presidential election. It will save these tweets in a file named "tweets.csv", in a directory named "republican_debate".

java -jar TweetStreamer.jar -corpus republican_debate -track "bush,carson,christie,cruz,fiorina,gilmore,graham,huckabee,kasich,pataki,paul,rubio,santorum,trump" -language "en" -key w3uvmKqezBWtyugqlkb5y7w -secret mN9M6cfSRPRAdUxxYOSWRYaHBeU5yFTGPGgc8fFdY -token 1988904670-XAnOV6XquVDuWzXwwhAvKiZ9T1DI9ziM3r7Cz3s -tokensecret wRwJhSq1m7zZeQYeTgivVSZ6H7acsv0KNiznF3StoS5P9