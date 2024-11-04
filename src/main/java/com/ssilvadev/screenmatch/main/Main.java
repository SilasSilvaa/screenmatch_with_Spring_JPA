package com.ssilvadev.screenmatch.main;

import com.ssilvadev.screenmatch.model.*;
import com.ssilvadev.screenmatch.repository.SeriesRepository;
import com.ssilvadev.screenmatch.service.ConsumptionApi;
import com.ssilvadev.screenmatch.service.ConvertData;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumptionApi api = new ConsumptionApi();
    private final ConvertData convertData = new ConvertData();
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private final List<DataSeries> dataSeries = new ArrayList<>();
    private List<Series> seriesList = new ArrayList<>();
    private Optional<Series> optionalSeries;

    private final SeriesRepository repository;

    public Main(SeriesRepository repository) {
        this.repository = repository;
    }

    public void showMenu() {
        var option = -1;
        while (option != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar séries por nome
                    5 - Buscar séries por autor
                    6 - Listar top 5 Series
                    7 - Buscar séries por categoria
                    8 - Filtrar series
                    9 - Buscar episodiio por trecho
                    10 - Top 5 episodios por serie
                    11 - Buscar episodios por data
                    0 - Sair
                    """;

            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    searchSeriesOnWeb();
                    break;
                case 2:
                    searchEpByNamePerSeries();
                    break;
                case 3:
                    listSearchedSeries();
                    break;
                case 4:
                    searchSeriesByName();
                    break;
                case 5:
                    searchSeriesByActor();
                    break;
                case 6:
                    searchTopFiveSeries();
                    break;
                case 7:
                    searchByCatalog();
                    break;
                case 8:
                    filterSeriesPerSeasonAndRating();
                    break;
                case 9:
                    searchEpByStretch();
                    break;
                case 10:
                    topEpisodesPerSeries();
                    break;
                case 11:
                    searchEpisodeAfterDate();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void searchEpisodeAfterDate() {
        searchSeriesByName();

        if (optionalSeries.isPresent()) {
            Series series = optionalSeries.get();
            System.out.println("Digite o ano limite de lançamento");
            int yearOfRelease = scanner.nextInt();

            List<Episodes> episodes = repository.searchEpisodeAfterDate(series, yearOfRelease);
            episodes.forEach(System.out::println);
        }
    }
    private void topEpisodesPerSeries() {
        searchSeriesByName();

        if(optionalSeries.isPresent()){
            Series series = optionalSeries.get();
            List<Episodes> episodes = repository.topEpisodesPerSeries(series);

            System.out.println("Episodios encontrados:");
            episodes.forEach(e -> System.out.println(e.getSeries()  + " Ep - " +
                    e.getEpNumber() + " Season -  " +
                    e.getSeason() + "Title - " + e.getTitle() +
                    " Rating - " + e.getRating()));
        }
    }

    private void searchEpByStretch() {
        System.out.println("Qual o nome do episodio para busca?");
        String stretch = scanner.nextLine();
        List<Episodes> episodes = repository.episodesByStretch(stretch);

        System.out.println("Episodios encontrados:");
        episodes.forEach(e -> System.out.println(e.getSeries()  + " Ep - " +
                e.getEpNumber() + " Season -  " + e.getSeason() + "Title - " + e.getTitle()));
    }

    private void filterSeriesPerSeasonAndRating() {
        System.out.println("Filtrar series até quantas temporadas?");
        int totalSeason = scanner.nextInt();

        System.out.println("Com avaliação a partir de que valor?");
        double rating = scanner.nextDouble();

        List<Series> series = repository.seriesPerSeasonAndRating(totalSeason, rating);
        System.out.println("series filtradas");
        series.forEach(System.out::println);
    }

    private void searchByCatalog() {
        System.out.println("Digite o nome da categoria");
        String name = scanner.nextLine();

        Catalog catalog = Catalog.fromString(name);
        List<Series> seriesByGenre = repository.findByGenre(catalog);

        seriesByGenre.forEach(System.out::println);
    }

    private void searchTopFiveSeries() {
        List<Series> series = repository.findTop5ByOrderByRatingDesc();
        System.out.println("Top 5");
        System.out.println(series);
    }

    private void searchSeriesByActor() {
        System.out.println("Digite o nome do ator");
        String name = scanner.nextLine();

        List<Series> series = repository.findByActorsContainingIgnoreCase(name);
        System.out.println("Series encontradas: " + series);


    }

    private void searchSeriesByName() {
        System.out.println("Digite o nome da série");
        String name = scanner.nextLine();

        optionalSeries = repository.findByTitleContainingIgnoreCase(name);

        if (optionalSeries.isPresent()) {
            System.out.println("Serie encontrada: " + optionalSeries.get());
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void searchSeriesOnWeb() {
        DataSeries dados = getDataSeries();
        Series series = new Series(dados);
        //dadosSeries.add(dados);
        repository.save(series);
        System.out.println(dados);
    }

    private DataSeries getDataSeries() {
        System.out.println("Digite o nome da série para busca");
        var name = scanner.nextLine();
        var json = api.getData(ADDRESS + name.replace(" ", "+") + API_KEY);

        return convertData.getData(json, DataSeries.class);
    }

    private void searchEpByNamePerSeries() {
        listSearchedSeries();

        System.out.println("Escolha uma série pelo nome");
        var name = scanner.nextLine();

        Optional<Series> searchedSeries = repository.findByTitleContainingIgnoreCase(name);

        if (searchedSeries.isPresent()) {
            List<DataSeason> seasons = new ArrayList<>();
            Series series = searchedSeries.get();

            for (int i = 1; i <= series.getTotalSeason(); i++) {
                var json = api.getData(ADDRESS + series.getTitle().replace(" ", "+") + "&season=" + i + API_KEY);
                DataSeason dataSeason = convertData.getData(json, DataSeason.class);
                seasons.add(dataSeason);
            }
            seasons.forEach(System.out::println);

            List<Episodes> episodes = seasons.stream()
                    .flatMap(d -> d.episodes().stream()
                            .map(e -> new Episodes(d.number(), e)))
                    .collect(Collectors.toList());

            series.setEpisodes(episodes);

            repository.save(series);
        } else {
            System.out.println("Série não encontrada...");
        }
    }

    private void listSearchedSeries() {
        seriesList = repository.findAll();
        seriesList.stream()
                .sorted(Comparator.comparing(Series::getGenre))
                .forEach(System.out::println);
    }
}