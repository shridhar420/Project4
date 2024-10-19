import java.io.*;
import java.net.*;
import java.util.*;

class URLMapping {
    private String originalURL;
    private String shortURL;

    public URLMapping(String originalURL, String shortURL) {
        this.originalURL = originalURL;
        this.shortURL = shortURL;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public String getShortURL() {
        return shortURL;
    }
}

class LinkShortener {
    private Map<String, String> urlMap;
    private static final String BASE_URL = "http://short.ly/";

    public LinkShortener() {
        urlMap = new HashMap<>();
        loadMappings();
    }

    public String shortenURL(String originalURL) throws MalformedURLException {
        validateURL(originalURL);
        if (urlMap.containsKey(originalURL)) {
            return urlMap.get(originalURL);
        }
        
        String shortURL = generateShortURL();
        urlMap.put(originalURL, shortURL);
        saveMappings();
        return shortURL;
    }

    public String retrieveOriginalURL(String shortURL) {
        return urlMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(shortURL))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private String generateShortURL() {
        return BASE_URL + UUID.randomUUID().toString().substring(0, 8);
    }

    private void validateURL(String url) throws MalformedURLException {
        new URL(url);  // Validate the URL by trying to create a URL object
    }

    private void loadMappings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("url_mappings.dat"))) {
            urlMap = (Map<String, String>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading mappings: " + e.getMessage());
        }
    }

    private void saveMappings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("url_mappings.dat"))) {
            oos.writeObject(urlMap);
        } catch (IOException e) {
            System.out.println("Error saving mappings: " + e.getMessage());
        }
    }
}

public class QuickLinkShortener {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LinkShortener linkShortener = new LinkShortener();

        while (true) {
            System.out.println("1. Shorten URL");
            System.out.println("2. Retrieve Original URL");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter long URL: ");
                    String longURL = scanner.nextLine();
                    try {
                        String shortURL = linkShortener.shortenURL(longURL);
                        System.out.println("Shortened URL: " + shortURL);
                    } catch (MalformedURLException e) {
                        System.out.println("Invalid URL: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Enter shortened URL: ");
                    String shortURL = scanner.nextLine();
                    String originalURL = linkShortener.retrieveOriginalURL(shortURL);
                    if (originalURL != null) {
                        System.out.println("Original URL: " + originalURL);
                    } else {
                        System.out.println("Shortened URL not found.");
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
