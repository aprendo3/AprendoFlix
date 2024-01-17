import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {
    static final String GREEN = "\033[32m";
    static final String RESET = "\033[0m";
    static final String RED = "\033[31m";
    static final String BLUE = "\033[94m";
    static Scanner scanner = new Scanner(System.in);
    static boolean logged = false;
    static String currentUsername = null;
    static String currentPassword = null;
    static ArrayList<String> logins = null;
    static ArrayList<Movie> movies = null;
    static final String ADMIN_LOGIN = "admin";
    static final String MOVIES_FILENAME = "movies.data";

    public static void main(String[] args) {
        loadLogins();
        loadMovies(MOVIES_FILENAME);
        showWelcomeMenu();
        
        if (ADMIN_LOGIN.equals(currentUsername)) {
            showAdminMenu();
        } else if (logged) {
            showUserMenu();
        }
        storeMovies(MOVIES_FILENAME);
    }

    private static void showUserMenu() {
        boolean running = true;
        while (running) {
            System.out.println("=====================================");
            System.out.println("\033[94mWelcome to the Aprendo Flix\033[0m");
            System.out.println("=====================================");
            System.out.println();
            System.out.printf("[%s1%s] Movies\n", GREEN, RESET);
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
            case "1":
                cleanScreen();
                showMovies();
                break;
            case "q":
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
            }
        }
    }

    private static void showMovies() {
        System.out.printf("%sAprendo Fix%s > %sShow Movies%s\n", BLUE, RED, BLUE, RESET);
        boolean quit = false;
        while (!quit) {
            listMovies();
            System.out.println("Pick a Movie (number) to start watching");
            System.out.printf("[%sS%s] Search for a movie\n", GREEN, RESET);
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();
            Integer numberMovie = tryToParse(choice);
            if ("q".equals(choice))
                break;
            else if ("s".equals(choice))
                searchMovies(Main::watchMovie);
            else if (numberMovie != null && numberMovie > 0 && numberMovie <= movies.size()) {
                watchMovie(numberMovie - 1);
                cleanScreen();
            }
            else System.out.println("Invalid choice. Please try again.");
        }
        
    }

    private static void watchMovie(int indexMovie) {
        boolean quit = false;
        Movie movie = movies.get(indexMovie);
        movie.viewCount++;
        while(!quit ) {
            cleanScreen();
            System.out.printf("%sAprendo Flix %s>%s Watching:%s%s%s\n", BLUE, RED, BLUE, GREEN, movie.title, RESET);
            showMovieDetails(movie);
            System.out.println();
            System.out.printf("[%s1%s] Rating\n", GREEN, RESET);
            System.out.printf("[%s2%s] View comments\n", GREEN, RESET);
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();
            switch (choice) {
            case "1":
                addRating(movie);
                break;
            case "2":
                viewComments(movie);
                break;
            case "q":
                quit = true;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
            }
        }
        
    }

    private static void viewComments(Movie movie) {
        cleanScreen();
        System.out.printf("%sAprendo Flix %s>%s View comment: %s%s%s\n", BLUE, RED, BLUE, GREEN, movie.title, RESET);
        movie.comments.forEach(System.out::println);
        System.out.println();
        System.out.printf("Please enter new %scomment%s (blank to skip): ", BLUE, RESET);
        String comment = scanner.nextLine().toLowerCase();
        if (!comment.isEmpty())
            movie.comments.add(String.format("%s: %s", currentUsername, comment));
    }

    private static void addRating(Movie movie) {
        cleanScreen();
        System.out.printf("%sAprendo Flix %s>%s Rating: %s%s%s\n", BLUE, RED, BLUE, GREEN, movie.title, RESET);
        while(true) {
            System.out.println("Rating value from 1 to 5, movie rating");
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();
            Integer rating = tryToParse(choice);
            
            if ("q".equals(choice))
                break;
            else if (rating != null && rating > 0 && rating <= 5) { 
                movie.rating.put(currentUsername, rating);
                break;
            }
            else System.out.println("Invalid choice. Please try again.");
        }
        
    }

    private static void showAdminMenu() {
        boolean running = true;
        while (running) {
            System.out.println("=====================================");
            System.out.println("\033[94mWelcome to the Aprendo Movie App\033[0m");
            System.out.println("=====================================");
            System.out.println();
            System.out.printf("[%sA%s] Add Movie\n", GREEN, RESET);
            System.out.printf("[%sL%s] List all movies\n", GREEN, RESET);
            System.out.printf("[%sS%s] Search for a movie\n", GREEN, RESET);
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
            case "a":
                cleanScreen();
                addMovie();
                break;
            case "l":
                cleanScreen();
                moviesOptions();
                cleanScreen();
                break;
            case "s":
                searchMovies(Main::showAdminMenuMoviewDetails);
                break;
            case "q":
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
            }
        }
    }

    private static void searchMovies(Consumer<Integer> showDetails) {
        cleanScreen();
        System.out.printf("%sMovie App%s > %s Search Movie%s%n%n", BLUE, RED, BLUE,RESET);
        System.out.print("Enter search term (Title): ");
        String searchTerm = scanner.nextLine();
        System.out.printf("Results with %s%s%s:(Title)%n", GREEN, searchTerm, RESET);
        Map<Integer, Movie> foundMovies = new HashMap<Integer, Movie>();
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).title.toLowerCase().contains(searchTerm.toLowerCase())) {
                foundMovies.put(i, movies.get(i));
                System.out.printf("%d) %s (%s)%n", foundMovies.size(), movies.get(i).title, movies.get(i).releaseDate);
            }
        }
        if (foundMovies.size() == 0) {
            System.out.println("Not found.");
            return;
        }
        System.out.println("Pick a Movie (number) for more Details");
        System.out.printf("[%sQ%s] Quit\n", RED, RESET);
        System.out.println();
        System.out.printf("Please enter a movie %snumber%s: ", BLUE, RESET);
        String choice = scanner.nextLine().toLowerCase();
        Integer numberMovie = tryToParse(choice);
        if ("q".equals(choice)) {
            cleanScreen();
            return;
        }
        
        if (numberMovie != null && numberMovie > 0 && numberMovie <= foundMovies.size()) {
            showDetails.accept((int)foundMovies.keySet().toArray()[numberMovie - 1]);
            cleanScreen();
        }
        System.out.println();
    }

    private static void moviesOptions() {
        System.out.printf("%sMovie App%s > %sAll Movies%s\n", BLUE, RED, BLUE, RESET);
        boolean quit = false;
        while (!quit) {
            listMovies();
            System.out.println("Pick a Movie (number) for more Details");
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();
            Integer numberMovie = tryToParse(choice);
            if ("q".equals(choice))
                break;
            else if (numberMovie != null && numberMovie > 0 && numberMovie <= movies.size()) {
                showAdminMenuMoviewDetails(numberMovie - 1);
                cleanScreen();
            }
            else System.out.println("Invalid choice. Please try again.");
        }
        
    }

    private static void listMovies() {
        for (int i = 1; i <= movies.size() && i <= 10 ; i++) {
            Movie movie = movies.get(i - 1);
            System.out.printf("%d) %s, %s\n", i, movie.title, movie.releaseDate);
        }
    }

    private static void showAdminMenuMoviewDetails(int indexMovie) {
        boolean quit = false;
        while (!quit) {
            cleanScreen();
            Movie movie = movies.get(indexMovie);
            System.out.printf("%sMovie App%s > %s%s (Details)%s\n", BLUE, RED, BLUE, movie.title,RESET);
            showMovieDetails(movie);
            System.out.println();
            System.out.printf("[%sE%s] Edit a movie\n", GREEN, RESET);
            System.out.printf("[%sD%s] Delete a movie\n", GREEN, RESET);
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();
            switch (choice) {
            case "e":
                editMovie(indexMovie);
                break;
            case "d":
                deleteMovie(indexMovie);
                quit = true;
                break;
            case "q":
                quit = true;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
            }
        }
        
    }

    private static void deleteMovie(int indexMovie) {
        movies.remove(indexMovie);
        
    }

    private static void editMovie(int indexMovie) {
        cleanScreen();
        Movie movie = movies.get(indexMovie);
        System.out.printf("%sMovie App%s > %s%s (Editing)%s\n", BLUE, RED, BLUE, movie.title,RESET);
        showMovieDetails(movie);
        System.out.println("Enter the new title (or press enter to keep existing title):");
        String newTitle = scanner.nextLine();
        if (!newTitle.isEmpty()) {
            movie.title = newTitle;
        }
        
        System.out.println("Enter the new director (or press enter to keep existing director):");
        String newDirector = scanner.nextLine();
        if (!newDirector.isEmpty()) {
            movie.director = newDirector;
        }
        
        System.out.println("Enter the new release date (or press enter to keep release date):");
        String newReleaseDate = scanner.nextLine();
        if (!newReleaseDate.isEmpty()) {
            try {
                movie.releaseDate = LocalDate.parse(newReleaseDate);
            } catch (Exception e) {
                System.out.printf("`%s` is not valid date %n", newReleaseDate);
            }
        }
        
        System.out.println("Enter the new genre (or press enter to keep genre):");
        String newGenre = scanner.nextLine();
        if (!newGenre.isEmpty()) {
            movie.genre = newGenre;
        }
    }

    private static void showMovieDetails(Movie movie) {
        System.out.printf("Title: %s\n", movie.title);
        System.out.printf("Director: %s\n", movie.director);
        System.out.printf("Release Date: %s\n", movie.releaseDate);
        System.out.printf("Genre: %s\n", movie.genre);
        String movieStats = getMovieStats(movie);
        System.out.printf("Stats: %s\n", movieStats);
        
    }

    private static String getMovieStats(Movie movie) {
        var views = String.format("%d views", movie.viewCount);
        var comment = String.format("%d comments", movie.comments.size());
        double ratingValue = movie.rating.values().stream().mapToInt(r -> r).average().orElse(0);
        var rating = String.format("%.1f/5 (%d)", ratingValue , movie.rating.size());
        return String.format("%s[%s, %s, %s]%s", GREEN, views, rating, comment, RESET);
    }

    private static Integer tryToParse(String choice) {
        try {
            return Integer.parseInt(choice);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void cleanScreen() {
        System.out.println(new String(new char[50]).replace("\0", "\n"));
    }

    private static void addMovie() {
        System.out.printf("%sMovie App%s > %sAdd Movie%s\n", BLUE, RED, BLUE, RESET);
        String inputData = "";
        while(true) {
            System.out.print("\nEnter title: ");
            inputData = scanner.nextLine();
            if (!inputData.isBlank()) break;
        }
        String title = inputData;
        
        while(true) {
            System.out.print("Enter director: ");
            inputData = scanner.nextLine();
            if (!inputData.isBlank()) break;
        }
        
        String director = inputData;
        
        LocalDate releaseDate = LocalDate.MIN;

        while(true) {
            System.out.print("Enter release date (yyyy-mm-dd): ");
            inputData = scanner.nextLine();
            try {
                releaseDate = LocalDate.parse(inputData);
                break;
            } catch (DateTimeParseException e) {
                System.out.printf("`%s` is not valid date, (%s) %n", inputData, e.getMessage());
            }
        }

        while(true) {
            System.out.print("Enter genre: ");
            inputData = scanner.nextLine();
            if (!inputData.isBlank()) break;
        }
        String genre = inputData;
        
        Movie movie = new Movie(title, director, releaseDate, genre);
        movies.add(movie);
        cleanScreen();
        System.out.println("Movie added successfully.");
    }
    
    private static void storeMovies(String moviesFilename) {
        try {
            var fs = new FileOutputStream(moviesFilename);
            var os = new ObjectOutputStream(fs);
            os.writeObject(movies);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadMovies(String moviesFilename) {
        try {
            Path filePath = Paths.get(moviesFilename);
            var file = filePath.toFile();
            if (!file.exists())
                file.createNewFile();
            var fis = new FileInputStream(file);
            var ois = new ObjectInputStream(fis);
            movies = (ArrayList<Movie>)ois.readObject();
            ois.close();
        } catch (EOFException e) {
            movies = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static void loadLogins() {
        try {
            Path filePath = Paths.get("logins.data");
            var file = filePath.toFile();
            if (!file.exists())
                file.createNewFile();
            var fis = new FileInputStream(file);
            var ois = new ObjectInputStream(fis);
            logins = (ArrayList<String>)ois.readObject();
            ois.close();
        } catch (EOFException e) {
            logins = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void storeLogins() {
        try {
            var fs = new FileOutputStream("logins.data");
            var os = new ObjectOutputStream(fs);
            os.writeObject(logins);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showWelcomeMenu() {
        boolean running = true;
        while (running) {
            System.out.println("=====================================");
            System.out.println("\033[94mWelcome to the Aprendo Movie App\033[0m");
            System.out.println("=====================================");
            System.out.println();
            System.out.printf("[%s1%s] Login\n", GREEN, RESET);
            System.out.printf("[%s2%s] Register\n", GREEN, RESET);
            System.out.printf("[%sQ%s] Quit\n", RED, RESET);
            System.out.println();
            System.out.printf("Please select an %soption%s: ", BLUE, RESET);
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
            case "1":
                running = !login();
                break;
            case "2":
                register();
                break;
            case "q":
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
            }
        }
    }

    private static void register() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        
        if(username == null || username.trim().isBlank() 
            || password == null || password.trim().isBlank()) {
            System.out.println("Registration Failed! username or password not be empty");
            
            return;
        }

        if (addNewLogin(username, password))
            System.out.println("Registration successful! Please login.");
        else
            System.out.println("Registration Failed! username already exists.");
    }
    
    private static boolean addNewLogin(String username, String password) {
        for (int i = 0; i < logins.size(); i+=2) {
            if (logins.get(i).equalsIgnoreCase(username)) return false;
        }
        logins.add(username);
        logins.add(password);

        storeLogins();
        return true;
    }

    private static boolean login() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        if (tryLogin(username, password)) {
            System.out.println("Login successful!");
            currentUsername = username;
            logged = true;
        } else {
            System.out.println("Incorrect username or password. Please try again.");
            logged = false;
        }
        return logged;
    }
    
    private static boolean tryLogin(String username, String password) {
        for (int i = 0; i < logins.size(); i+=1)
            if (logins.get(i).equals(username) && logins.get(i + 1).equals(password))
                return true;
        return false;
    }
}
