package com.ssilvadev.screenmatch.main;

import com.ssilvadev.screenmatch.model.DataSeries;
import com.ssilvadev.screenmatch.model.DataSeason;
import com.ssilvadev.screenmatch.model.Series;
import com.ssilvadev.screenmatch.repository.SeriesRepository;
import com.ssilvadev.screenmatch.service.ConsumptionApi;
import com.ssilvadev.screenmatch.service.ConvertData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumptionApi api = new ConsumptionApi();
    private final ConvertData convertData = new ConvertData();
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private final List<DataSeries> dataSeries = new ArrayList<>();

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
        DataSeries dataSeries = getDataSeries();
        List<DataSeason> seasons = new ArrayList<>();

        for (int i = 1; i <= dataSeries.totalSeason(); i++) {
            var json = api.getData(ADDRESS + dataSeries.title().replace(" ", "+") + "&season=" + i + API_KEY);
            DataSeason dataSeason = convertData.getData(json, DataSeason.class);
            seasons.add(dataSeason);
        }
        seasons.forEach(System.out::println);
    }

    private void listSearchedSeries(){
        List<Series> series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Series::getGenre))
                .forEach(System.out::println);
    }
}