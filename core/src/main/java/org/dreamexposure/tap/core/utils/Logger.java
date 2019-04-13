package org.dreamexposure.tap.core.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.dreamexposure.tap.core.conf.GlobalVars;
import org.dreamexposure.tap.core.conf.SiteSettings;

import javax.annotation.Nullable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;

/**
 * @author NovaFox161
 * Date Created: 12/5/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
@SuppressWarnings("Duplicates")
public class Logger {
    private static Logger instance;
    private WebhookClient debugClient;
    private WebhookClient exceptionClient;
    private WebhookClient statusClient;

    private String exceptionsFile;
    private String apiFile;
    private String debugFile;
    
    private final String lineBreak = System.lineSeparator();
    
    private Logger() {
    } //Prevent initialization
    
    public static Logger getLogger() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void init(String folder) {
//Create webhook clients.
        if (SiteSettings.USE_WEBHOOKS.get().equalsIgnoreCase("true")) {
            debugClient = WebhookClient.withUrl(SiteSettings.DEBUG_WEBHOOK.get());
            exceptionClient = WebhookClient.withUrl(SiteSettings.ERROR_WEBHOOK.get());
            statusClient = WebhookClient.withUrl(SiteSettings.STATUS_WEBHOOK.get());
        }

        //Create files...
        String timestamp = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss").format(System.currentTimeMillis());
        
        exceptionsFile = folder + "/" + timestamp + "-exceptions.log";
        apiFile = folder + "/" + timestamp + "-api.log";
        debugFile = folder + "/" + timestamp + "-debug.log";
        
        try {
            PrintWriter exceptions = new PrintWriter(exceptionsFile, "UTF-8");
            exceptions.println("INIT --- " + timestamp + " ---");
            exceptions.close();
            
            PrintWriter api = new PrintWriter(apiFile, "UTF-8");
            api.println("INIT --- " + timestamp + " ---");
            api.close();
            
            PrintWriter debug = new PrintWriter(debugFile, "UTF-8");
            debug.println("INIT --- " + timestamp + " ---");
            debug.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exception(String message, Exception e, boolean postWebhook, Class clazz) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String error = "no error provided";
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            error = sw.toString(); // stack trace as a string
            pw.close();
            try {
                sw.close();
            } catch (IOException e1) {
                //Can ignore silently...
            }
        }
        
        try {
            FileWriter exceptions = new FileWriter(exceptionsFile, true);
            exceptions.write("ERROR --- " + timeStamp + " ---" + lineBreak);
            if (message != null) {
                exceptions.write("message: " + message + lineBreak);
            }
            exceptions.write("class: " + clazz.getName() + lineBreak);
            exceptions.write(error + lineBreak);
            exceptions.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

        //Post to webhook if wanted.
        if (SiteSettings.USE_WEBHOOKS.get().equalsIgnoreCase("true") && postWebhook) {
            WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle("Debug", null))
                    .addField(new WebhookEmbed
                            .EmbedField(false, "Class", clazz.getName()))
                    .setDescription(error)
                    .setColor(GlobalVars.errorEmbedColor.getRGB())
                    .setTimestamp(Instant.now());

            if (message != null) {
                builder.addField(new WebhookEmbed.EmbedField(false, "Message", message));
            }

            exceptionClient.send(builder.build());
        }
    }

    public void debug(String message, String info, boolean postWebhook, Class clazz) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime());
        
        try {
            FileWriter file = new FileWriter(debugFile, true);
            file.write("DEBUG --- " + timeStamp + " ---" + lineBreak);
            if (message != null) {
                file.write("message: " + message + lineBreak);
            }
            if (info != null) {
                file.write("info: " + info + lineBreak);
            }
            file.write("class: " + clazz.getName() + lineBreak);
            file.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

        //Post to webhook if wanted.
        if (SiteSettings.USE_WEBHOOKS.get().equalsIgnoreCase("true") && postWebhook) {
            WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle("Debug", null))
                    .setDescription(message)
                    .setColor(GlobalVars.infoEmbedColor.getRGB())
                    .setTimestamp(Instant.now());
            if (info != null) {
                builder.addField(new WebhookEmbed.EmbedField(false, "Info", info));
            }

            debugClient.send(builder.build());
        }
    }

    public void debug(String message, boolean postWebhook) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime());
        
        try {
            FileWriter file = new FileWriter(debugFile, true);
            file.write("DEBUG --- " + timeStamp + " ---" + lineBreak);
            if (message != null) {
                file.write("info: " + message + lineBreak);
            }
            file.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

        //Post to webhook if wanted.
        if (SiteSettings.USE_WEBHOOKS.get().equalsIgnoreCase("true") && postWebhook) {
            WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle("Debug", null))
                    .setDescription(message)
                    .setColor(GlobalVars.infoEmbedColor.getRGB())
                    .setTimestamp(Instant.now());

            debugClient.send(builder.build());
        }
    }
    
    public void api(String message) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime());
        
        try {
            FileWriter file = new FileWriter(apiFile, true);
            file.write("API --- " + timeStamp + " ---" + lineBreak);
            file.write("info: " + message + lineBreak);
            file.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    
    public void api(String message, String ip) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime());
        
        try {
            FileWriter file = new FileWriter(apiFile, true);
            file.write("API --- " + timeStamp + " ---" + lineBreak);
            file.write("info: " + message + lineBreak);
            file.write("IP: " + ip + lineBreak);
            file.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    
    public void api(String message, String ip, String host, String endpoint) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime());
        
        try {
            FileWriter file = new FileWriter(apiFile, true);
            file.write("API --- " + timeStamp + " ---" + lineBreak);
            file.write("info: " + message + lineBreak);
            file.write("IP: " + ip + lineBreak);
            file.write("Host: " + host + lineBreak);
            file.write("Endpoint: " + endpoint + lineBreak);
            file.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void status(String message, @Nullable String info) {
        //Post to webhook if wanted.
        if (SiteSettings.USE_WEBHOOKS.get().equalsIgnoreCase("true")) {
            WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle("Debug", null))
                    .setDescription(message)
                    .setColor(GlobalVars.infoEmbedColor.getRGB())
                    .setTimestamp(Instant.now());

            if (info != null) {
                builder.addField(new WebhookEmbed
                        .EmbedField(false, "Info", info));
            }

            statusClient.send(builder.build());
        }
    }
}
