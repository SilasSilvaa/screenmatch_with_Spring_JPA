package com.ssilvadev.screenmatch.repository;

import com.ssilvadev.screenmatch.model.Catalog;
import com.ssilvadev.screenmatch.model.Episodes;
import com.ssilvadev.screenmatch.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String name);
    List<Series> findByActorsContainingIgnoreCase(String name);
    List<Series> findTop5ByOrderByRatingDesc();
    List<Series> findByGenre(Catalog catalog);

    @Query("SELECT s FROM Series s WHERE s.totalSeason <= :totalSeason AND s.rating >= :rating")
    List<Series> seriesPerSeasonAndRating(int totalSeason, double rating);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE e.title ILIKE %%:stretch")
    List<Episodes> episodesByStretch(String stretch);

    @Query("SELECT e FROM Series s WHERE s.episodes e WHERE s = :series ORDER BY e.rating DESC LIMIT 5")
    List<Episodes> topEpisodesPerSeries(Series series);

    @Query("SELECT e FROM Series s WHERE s.episodes e WHERE s = :series AND YEAR(e.dateOfRelease) > yearOfRelease")
    List<Episodes> searchEpisodeAfterDate(Series series, int yearOfRelease);
}
