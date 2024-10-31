package com.ssilvadev.screenmatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodes")
public class Episodes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer season;
    private String title;
    private Integer epNumber;
    private Double rating;
    private LocalDate dateOfRelease;

    @ManyToOne()
    private Series series;

    public Episodes(Integer season, DataEpisode dataEpisode) {
        this.season = season;
        this.title = dataEpisode.title();
        this.epNumber = dataEpisode.number();

        try {
            this.rating = Double.valueOf(dataEpisode.rating());
        } catch (NumberFormatException ex) {
            this.rating = 0.0;
        }

        try {
            this.dateOfRelease = LocalDate.parse(dataEpisode.dateOfRelease());
        } catch (DateTimeParseException ex) {
            this.dateOfRelease = null;
        }
    }

    public Episodes(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEpNumber() {
        return epNumber;
    }

    public void setEpNumber(Integer epNumber) {
        this.epNumber = epNumber;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDate getDateOfRelease() {
        return dateOfRelease;
    }

    public void setDateOfRelease(LocalDate dateOfRelease) {
        this.dateOfRelease = dateOfRelease;
    }

    @Override
    public String toString() {
        return "temporada=" + season +
                ", titulo='" + title + '\'' +
                ", numeroEpisodio=" + epNumber +
                ", avaliacao=" + rating +
                ", dataLancamento=" + dateOfRelease;
    }
}
