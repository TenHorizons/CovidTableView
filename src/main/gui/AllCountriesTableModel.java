package main.gui;

import main.parser.Reader;
import main.parser.data.CasesByCountry;
import org.apache.commons.text.similarity.LevenshteinDistance;

import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllCountriesTableModel extends DefaultTableModel {
    private Reader data;
    private String[] columnNames = {"Country","Confirmed","Deaths","Recovered"};
    private Object[][] formattedData;

    public AllCountriesTableModel(){
        super();
        this.data  = Reader.getInstance();
        formattedData = getFormattedData();
        super.setDataVector(formattedData,columnNames);
    }

    private List<Map<String,Object>> getCaseTotals(){
        //get total cases for each country.
        //total cases is not always at 26 June 2022, so I will get max value in row.
        Function<List<CasesByCountry>,List<Integer>> getTotal = casesByCountryList -> casesByCountryList.stream().map(
                casesByCountry ->
                        Collections.max(casesByCountry.getSortedCasesByDate().values())
        ).collect(Collectors.toList());

        List<String> countryCol =
                data.getConfirmedCases().stream()
                        .map(CasesByCountry::getCountry).toList();
        List<Integer> confirmedCol = getTotal.apply(data.getConfirmedCases());
        List<Integer> deathsCol = getTotal.apply(data.getDeathsCases());
        List<Integer> recoveredCol = getTotal.apply(data.getRecoveredCases());

        List<Map<String,Object>> rows = new ArrayList<>();
        for(Integer confirmedColRow:confirmedCol){
            Map<String,Object> row = new HashMap<>();
            int rowIndex = confirmedCol.indexOf(confirmedColRow);
            row.put("countryCol",countryCol.get(rowIndex));
            row.put("confirmedCol",confirmedCol.get(rowIndex));
            row.put("deathsCol",deathsCol.get(rowIndex));
            row.put("recoveredCol",recoveredCol.get(rowIndex));
            rows.add(row);
        }
        return rows;
    }

    private Object[][] getFormattedData() {
        List<Map<String,Object>> rows = getCaseTotals();
        Object[][] fd = new Object[rows.size()][columnNames.length];
        for(Map<String, Object> row:rows){
            fd[rows.indexOf(row)] = new Object[]{
                    row.get("countryCol"),
                    Integer.parseInt(row.get("confirmedCol").toString()),
                    Integer.parseInt(row.get("deathsCol").toString()),
                    Integer.parseInt(row.get("recoveredCol").toString())
            };
        }
        return fd;
    }

    public void updateTableForSearch(String keyword) {
        Function<Object[],Integer> levenshteinDistanceCalculation = e ->
                ((int)(
                        ((double)LevenshteinDistance.getDefaultInstance().apply(keyword, ((String) e[0]))*100)
                        /
                        ((double)Math.max(keyword.length(),((String)e[0]).length()))
                ));
        List<Object[]> sortedRows = Arrays.stream(formattedData)
                .sorted(
                        //e[0] retrieves first column "countryCol" in Object[].
                        Comparator.comparingInt(levenshteinDistanceCalculation::apply)
                ).filter(e->
                        ((String)e[0]).toLowerCase().contains(keyword.toLowerCase())
                ).toList();
        //convert List to Object[][] for table.
        Object[][] tableAfterSearch = new Object[sortedRows.size()][columnNames.length];
        for(Object[] row:sortedRows){
            tableAfterSearch[sortedRows.indexOf(row)] = row;
        }
        //have JTableModel display filtered table with setDataVector
        super.setDataVector(tableAfterSearch,columnNames);
    }

    //to fix sorting issue caused by column classes being Object.
    Class[] types = new Class [] {
            java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
    };
    @Override
    public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
    }
}