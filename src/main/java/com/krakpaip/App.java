package com.krakpaip;

public class App 
{
    static void main()
    {
        System.out.println( "=== PODCASTer v0.1 ===" );
        AppConfiguration appConfiguration = AppConfiguration.load();
        new PodcastChatLoop().start(new PodcastAgent(appConfiguration));
    }
}
