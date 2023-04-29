import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movie implements Serializable {
    public String title;
    public String director;
    public LocalDate releaseDate;
    public String genre;
    
    public int viewCount;
    public List<String> comments = new ArrayList<>();
    public List<Integer> rating = new ArrayList<>();
    
    public Movie(String title, String director, LocalDate releaseDate, String genre) {
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }
}
