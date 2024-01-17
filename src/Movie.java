import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;
    public String title;
    public String director;
    public LocalDate releaseDate;
    public String genre;
    
    public int viewCount;
    public List<String> comments = new ArrayList<>();
    public Map<String, Integer> rating = new HashMap<>();
    
    public Movie(String title, String director, LocalDate releaseDate, String genre) {
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }
}
