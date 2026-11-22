package com.krakpaip;

import java.util.Scanner;

public class PodcastChatLoop {

    public void start(PodcastAgent podcastAgent) {
        System.out.println("Assistant: What news podcast would you want me to create? (type 'exit' to quit)");

        try(Scanner scanner = new Scanner(System.in)) {
            while(true) {
                System.out.println("User: ");
                String inputFromUser = scanner.nextLine().trim();

                if("exit".equalsIgnoreCase(inputFromUser)) {
                    System.out.println("Bye bye!");
                    break;
                }

                if(inputFromUser.isEmpty()) {
                    continue;
                }

                String response = podcastAgent.podcastAssistant.chat(inputFromUser);
                System.out.println("Assistant: " + response);
            }
        }
    }
}
