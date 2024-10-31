package com.ssilvadev.screenmatch.main;

import com.ssilvadev.screenmatch.model.DataSeries;
import com.ssilvadev.screenmatch.model.DataSeason;
import com.ssilvadev.screenmatch.model.Episodes;
import com.ssilvadev.screenmatch.model.Series;
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

    private final SeriesRepository repository;
    public Main(SeriesRepository repository) {
        this.repository = repository;
    }

    public void showMenu() {
        var option = -1;
        while(option != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                                    
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
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
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

    private void searchEpByNamePerSeries(){
        listSearchedSeries();

        System.out.println("Escolha uma série pelo nome");
        var name = scanner.nextLine();

        Optional<Series> searchedSeries = seriesList.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(name.toLowerCase()))
                .findFirst();

        if(searchedSeries.isPresent()){
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
        }else{
            System.out.println("Série não encontrada...");
        }


    }

    private void listSearchedSeries(){
        seriesList = repository.findAll();
        seriesList.stream()
                .sorted(Comparator.comparing(Series::getGenre))
                .forEach(System.out::println);
    }
}