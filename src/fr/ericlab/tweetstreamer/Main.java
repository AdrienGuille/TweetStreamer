package fr.ericlab.tweetstreamer;

import com.csvreader.CsvWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 *   @author Adrien GUILLE, ERIC Lab, University of Lyon 2
 *   @email adrien.guille@univ-lyon2.fr
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Prepare command line interface (CLI)
            Options options = new Options();
            options.addOption("corpus", true, "The name of the corpus determines the name of the directory where the tweets will be saved");
            options.addOption("track", true, "A comma-separated list of phrases which will be used to determine what tweets will be delivered on the stream");
            options.addOption("language", true, "A comma-separated list of languages which will be used to determine what tweets will be delivered on the stream");
            options.addOption("key", true, "Twitter oAuth: consumer key");
            options.addOption("secret", true, "Twitter oAuth: consumer secret");
            options.addOption("token", true, "Twitter oAuth: access token");
            options.addOption("tokensecret", true, "Twitter oAuth: access token");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            
            // Retrieve arguments values
            String corpusName, keywords = null, languages = null;
            if(cmd.hasOption("corpus")){
                corpusName = cmd.getOptionValue("corpus");
            }else{
                corpusName = "default";
            }
            File corpusDirectory = new File(corpusName);
            if(corpusDirectory.exists() && corpusDirectory.isDirectory()){
                throw new SimpleException("Invalid corpus name: a directory named "+corpusName+" already exists");
            }else{
                corpusDirectory.mkdir();
            }
            if(cmd.hasOption("track")){
                keywords = cmd.getOptionValue("track");
            }
            if(cmd.hasOption("language")){
                languages = cmd.getOptionValue("language");
            }
            String key;
            if(cmd.hasOption("key")){
                key = cmd.getOptionValue("key");
            }else{
                throw new SimpleException("Unable to connect to Twitter's streaming endpoint: no consumer key specified");
            }
            String secret;
            if(cmd.hasOption("secret")){
                secret = cmd.getOptionValue("secret");
            }else{
                throw new SimpleException("Unable to connect to Twitter's streaming endpoint: no consumer secret specified");
            }
            String token;
            if(cmd.hasOption("token")){
                token = cmd.getOptionValue("token");
            }else{
                throw new SimpleException("Unable to connect to Twitter's streaming endpoint: no access token specified");
            }
            String tokenSecret;
            if(cmd.hasOption("tokensecret")){
                tokenSecret = cmd.getOptionValue("tokensecret");
            }else{
                throw new SimpleException("Unable to connect to Twitter's streaming endpoint: no secret access token specified");
            }
            System.out.println("Corpus name: "+corpusName+"\nTrack: "+keywords+"\nLanguage: "+languages);
            
            // Connect to the public streaming endpoint
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true);
            cb.setOAuthConsumerKey(key);
            cb.setOAuthConsumerSecret(secret);
            cb.setOAuthAccessToken(token);
            cb.setOAuthAccessTokenSecret(tokenSecret);
            TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();            
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // Prepare CSV output
            File outputFile = new File(corpusName+"/tweets.csv");
            CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
            csvOutput.write("tweet_id");
            csvOutput.write("user_id");
            csvOutput.write("username");
            csvOutput.write("timestamp");
            csvOutput.write("text");
            csvOutput.write("rt_count");
            csvOutput.write("rt_tweet_id");
            csvOutput.write("rt_user_id");
            csvOutput.write("rt_username");
            csvOutput.write("rt_timestamp");
            csvOutput.write("tweet_rt_textid");
            csvOutput.endRecord();
            
            // Configure stream listener
            StatusListener listener = new StatusListener() {
                @Override
                public void onException(Exception arg0) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, arg0);
                }
                @Override
                public void onDeletionNotice(StatusDeletionNotice arg0) {

                }
                @Override
                public void onScrubGeo(long arg0, long arg1) {

                }
                @Override
                public void onStatus(Status status) {
                    try {
                        // This tweet
                        Date date = status.getCreatedAt();
                        User user = status.getUser();
                        String username = user.getScreenName();
                        String userId = ""+user.getId();
                        String tweetId = ""+status.getId();
                        String content = status.getText();
                        content = content.replace("\n", " ").replace("\r", " ").replace("\""," ");
                        
                        // Original tweet
                        String quotedTweetId = "0";
                        String quotedContent = "";
                        Date quotedDate = date;
                        String quotedUserId = "";
                        String quotedUsername = "";
                        String retweetCount = "0";
                        if(status.isRetweet()){
                            // Retweeted tweet
                            Status retweetedStatus = status.getRetweetedStatus();
                            quotedTweetId = ""+retweetedStatus.getId();
                            quotedContent = retweetedStatus.getText();
                            quotedContent = quotedContent.replace("\n", " ").replace("\r", " ").replace("\""," ");
                            quotedDate = retweetedStatus.getCreatedAt();
                            User quotedUser = retweetedStatus.getUser();
                            quotedUsername = quotedUser.getScreenName();
                            quotedUserId = ""+quotedUser.getId();
                            retweetCount = ""+retweetedStatus.getRetweetCount();
                        }
                        csvOutput.write(tweetId);
                        csvOutput.write(userId);
                        csvOutput.write(username);
                        csvOutput.write(dateFormat.format(date));
                        csvOutput.write(content);
                        csvOutput.write(retweetCount);
                        csvOutput.write(quotedTweetId);
                        csvOutput.write(quotedUserId);
                        csvOutput.write(quotedUsername);
                        csvOutput.write(dateFormat.format(quotedDate));
                        csvOutput.write(quotedContent);
                        csvOutput.endRecord();                        
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void onTrackLimitationNotice(int arg0) {
                    System.out.println("Track limitation notice: "+arg0);
                }

                @Override
                public void onStallWarning(StallWarning sw) {

                }
            };
            
            FilterQuery fq = new FilterQuery();
            if(languages != null){
                fq.language(languages.split(","));
            }
            if(keywords != null){
                fq.track(keywords.split(","));
            }
            twitterStream.addListener(listener);
            twitterStream.filter(fq);
        } catch (ParseException | SimpleException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
    }
    
}
