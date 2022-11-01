package main.parser;

import com.opencsv.bean.CsvToBeanBuilder;
import main.parser.data.CaseType;
import main.parser.data.CasesByCountry;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Reader{
    //Reader should be singleton to save on processing time.
    private static Reader INSTANCE;
    private static List<CasesByCountry> confirmedCases;
    private static List<CasesByCountry> deathsCases;
    private static List<CasesByCountry> recoveredCases;

    private Reader(){
        confirmedCases = readFile.apply(CaseType.CONFIRMED.getPath());
        deathsCases = readFile.apply(CaseType.DEATHS.getPath());
        recoveredCases = readFile.apply(CaseType.RECOVERED.getPath());
    }
    public static Reader getInstance(){
        if(INSTANCE!=null) return INSTANCE;
        INSTANCE = new Reader();
        return INSTANCE;
    }


    /**
     * @.map: sort cases by ascending date.
     * @.groupingBy: split into lists of countries to identify duplicates.
     * @.reduce: reduce CasesByCountry by merging sortedCasesByDates TreeMaps.*/
    public static UnaryOperator<List<CasesByCountry>> processInput = casesByCountryList -> {
        List<CasesByCountry> toR = new ArrayList<>();
        casesByCountryList.stream().map(
                casesByCountry ->
                        casesByCountry.addToSortedCasesByDate(
                                casesByCountry.getCasesByDate()
                        )
        ).collect(
                Collectors.groupingBy(CasesByCountry::getCountry)
        ).forEach(
                (country, casesByCountry) ->
                        toR.add(casesByCountry.stream().reduce(
                                null,
                                (country1, country2) ->
                                        country1!=null
                                                ? country1.setSortedCasesByDate.apply(country1, country2)
                                                :country2
                        ))
        );
        toR.sort(Comparator.comparing(CasesByCountry::getCountry));
        return toR;
    };

    public static Function<String, List<CasesByCountry>> readFile = (path) -> {
        try {
            List<CasesByCountry>l = new CsvToBeanBuilder(new FileReader(path))
                    .withType(CasesByCountry.class)
                    .build()
                    .parse();
            l = processInput.apply(l);
//            sort dates by ascending.
//            l.forEach(caseByCountry -> caseByCountry.addToSortedCasesByDate(caseByCountry.getCasesByDate()));
            return l;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    };



    public List<CasesByCountry> getConfirmedCases() {
        return confirmedCases;
    }

    public List<CasesByCountry> getDeathsCases() {
        return deathsCases;
    }

    public List<CasesByCountry> getRecoveredCases() {
        return recoveredCases;
    }
}
