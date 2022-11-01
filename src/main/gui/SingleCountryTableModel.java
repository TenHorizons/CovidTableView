package main.gui;

import main.parser.Reader;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SingleCountryTableModel extends DefaultTableModel {
    private Map<LocalDate,Integer> sortedCasesByDate;
    private final String[] columnNames = {"Date","Confirmed","Histogram"};
    private Object[][] formattedDataByMonth;
    private Object[][] formattedDataByWeek;
    private Integer casesPerStar;

    public SingleCountryTableModel(String country) {
        super();
        this.sortedCasesByDate = Reader.getInstance().getConfirmedCases().stream().filter(e->
                e.getCountry().equals(country)
        ).findFirst().get().getSortedCasesByDate();
        //Table displays confirmed cases by month by default.
        formattedDataByMonth = convertMapToTable.apply(sortedCasesByDate.entrySet()
                .stream()
                .collect(Collectors.groupingBy(getClassifier.apply("Month"),
                        Collectors.summingInt(Map.Entry::getValue))));
        super.setDataVector(formattedDataByMonth,columnNames);
    }
    private final Function<Map<?,Integer>,Object[][]> convertMapToTable = map -> {
        UnaryOperator<Integer> casesPerStar = maxStars -> Collections.max(map.values())/maxStars;
        //hard-code max stars to 30.
        this.casesPerStar = casesPerStar.apply(40);
        Function<Integer,String> getStars = cases ->
                Collections.nCopies((cases / this.casesPerStar), "*")
                        .stream().reduce("", String::concat);

        //Finally prepare table data.
        List<Object[]> rows = new ArrayList<>();
        map.forEach((k,v) ->
                rows.add(new Object[]{k,v,getStars.apply(v)})
        );
        rows.sort(Comparator.comparing(object -> ((Comparable)object[0])));
        Object[][] tableData = new Object[rows.size()][columnNames.length];
        for(Object[] row:rows) tableData[rows.indexOf(row)] = row;
        return tableData;
    };
    private final Function<String,Function<Map.Entry<LocalDate,Integer>,?>> getClassifier = selectedTimeframe ->
            selectedTimeframe.equals("Week")?
                    (m -> m.getKey().with(WeekFields.of(Locale.getDefault()).dayOfWeek(),1))
                    :(m -> YearMonth.from(m.getKey()));//Table displays confirmed cases by month by default.

    private final Function<String,Object[][]> doGroupingBy = selectedTimeframe ->
            convertMapToTable.apply(sortedCasesByDate.entrySet()
                    .stream()
                    .collect(Collectors.groupingBy(getClassifier.apply(selectedTimeframe),
                            Collectors.summingInt(Map.Entry::getValue))));

    public void updateTableForComboBox(String selectedTimeframe) {
        assert selectedTimeframe != null;
        if(formattedDataByWeek==null) formattedDataByWeek = doGroupingBy.apply(selectedTimeframe);

        super.setDataVector(
                selectedTimeframe.equals("Month") ?formattedDataByMonth:formattedDataByWeek,
                columnNames
        );
    }

    public Integer getCasesPerStar() {
        return casesPerStar;
    }

    //to fix sorting issue caused by column classes being Object.
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex==1) return java.lang.Integer.class;
        return super.getColumnClass(columnIndex);
    }
}
