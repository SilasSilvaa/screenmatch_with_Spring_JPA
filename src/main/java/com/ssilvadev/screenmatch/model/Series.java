package com.ssilvadev.screenmatch.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;
    private Integer totalSeason;
    private Double rating;

    @Enumerated(EnumType.STRING)
    private Catalog genre;

    private String actors;
    private String poster;
    private String plot;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodes> episodes = new ArrayList<>();


    public Series() {}

    public Series(DataSeries dataSeries){
        this.title = dataSeries.title();
        this.totalSeason = dataSeries.totalSeason();
        this.rating = OptionalDouble.of(Double.valueOf(dataSeries.rating())).orElse(0);
        this.genre = Catalog.fromString(dataSeries.genre().split(",")[0].trim());
        this.actors = dataSeries.actors();
        this.poster = dataSeries.poster();
        this.plot = dataSeries.plot();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episodes> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episodes> episodes) {
        episodes.forEach(e -> e.setSeries(this));
        this.episodes = episodes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalSeason() {
        return totalSeason;
    }

    public void setTotalSeason(Integer totalSeason) {
        this.totalSeason = totalSeason;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Catalog getGenre() {
        return genre;
    }

    public void setGenre(Catalog genre) {
        this.genre = genre;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    @Override
    public String toString() {
        return
                "genero=" + genre +
                        ", titulo='" + title + '\'' +
                        ", totalTemporadas=" + totalSeason +
                        ", avaliacao=" + rating +

                        ", atores='" + actors + '\'' +
                        ", poster='" + poster + '\'' +
                        ", sinopse='" + plot + '\'' +
                        ", episodes='" + episodes + '\'';
    }
}
