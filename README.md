# TweetStreamer

TweetStreamer is a command line tool for collecting tweets via Twitter's public streaming API.
Get the binary release here: https://github.com/AdrienGuille/TweetStreamer/releases

## Arguments

- corpus: The name of the corpus determines the name of the directory where the tweets will be saved
- track (optional): A comma-separated list of phrases which will be used to determine what tweets will be delivered on the stream
- language (optional): A comma-separated list of languages which will be used to determine what tweets will be delivered on the stream
- key: Twitter oAuth = consumer key
- secret: Twitter oAuth = consumer secret
- token: Twitter oAuth = access token
- tokensecret: Twitter oAuth = access token

## Output

The output is a CSV file, which columns are:
```
tweet_id,user_id,username,timestamp,text,rt_count,rt_tweet_id,rt_user_id,rt_username,rt_timestamp,tweet_rt_textid
```
The first 4 four columns are filled for every tweet. In case a retweet is received, the last 4 columns are filled with details about the original tweet.

## Example

Running the command below will collect tweets written in English that mention at least one of the republican party presidential candidates for the 2016 United States presidential election. It will save these tweets in a file named "tweets.csv", in a directory named "republican_debate".

```
java -jar TweetStreamer.jar -corpus republican_debate -track "bush,carson,christie,cruz,fiorina,gilmore,graham,huckabee,kasich,pataki,paul,rubio,santorum,trump" -language "en" -key w3uvmKqezBWtyugqlkb5y7w -secret mN9M6cfSRPRAdUxxYOSWRYaHBeU5yFTGPGgc8fFdY -token 1988904670-XAnOV6XquVDuWzXwwhAvKiZ9T1DI9ziM3r7Cz3s -tokensecret wRwJhSq1m7zZeQYeTgivVSZ6H7acsv0KNiznF3StoS5P9
```
