package main.parser.data;

import com.opencsv.bean.CsvBindAndJoinByName;
import com.opencsv.bean.CsvBindByName;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.function.BinaryOperator;

public class CasesByCountry {
    @CsvBindByName(column="Country/Region",required=true)
    private String country;
    @CsvBindAndJoinByName(column="[0-9]{1,2}/[0-9]{1,2}/[0-9]{1,4}", elementType = String.class, mapType = ArrayListValuedHashMap.class)
    private MultiValuedMap<String,String> casesByDate;
    private TreeMap<LocalDate, Integer> sortedCasesByDate = new TreeMap<>();

    public CasesByCountry(){}


    public String getCountry() {
        return country;
    }
    public MultiValuedMap<String, String> getCasesByDate() {
        return casesByDate;
    }

    public TreeMap<LocalDate, Integer> getSortedCasesByDate() {
        return sortedCasesByDate;
    }
    public CasesByCountry addToSortedCasesByDate(MultiValuedMap<String,String> map) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d/yy");
        for(String key:map.keys()){
            sortedCasesByDate.put(LocalDate.parse(key,dateFormat),Integer.valueOf(map.get(key).toString().replaceAll("[\\[\\]]","")));
        }
        return this;
    }
    public BinaryOperator<CasesByCountry> setSortedCasesByDate = (country1,country2) ->{
        country1.getSortedCasesByDate()
                .forEach(
                        (date, numOfCases) ->
                                country1.getSortedCasesByDate()
                                        .put(
                                                date,
                                                numOfCases + country2.getSortedCasesByDate().get(date)
                                        )
                );
        return country1;
    };

    @Override
    public String toString() {
        return "{country= "+this.getCountry()+
                "\ncases= " + this.getSortedCasesByDate().size() +
                "[" + this.getSortedCasesByDate() +
                "]}";
    }

}
