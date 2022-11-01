package main.parser.data;

public enum CaseType {
    CONFIRMED(
            "D:\\User\\user\\APLC\\Assignment\\Code\\" +
                    "Lim Ze Hong TP053115 APLC Assignment\\LimZeHongTP053115\\" +
                    "src\\csv\\time_series_covid19_confirmed_global.csv"
    ),
    DEATHS(
            "D:\\User\\user\\APLC\\Assignment\\Code\\" +
                    "Lim Ze Hong TP053115 APLC Assignment\\LimZeHongTP053115\\" +
                    "src\\csv\\time_series_covid19_deaths_global.csv"
    ),
    RECOVERED(
            "D:\\User\\user\\APLC\\Assignment\\Code\\" +
                    "Lim Ze Hong TP053115 APLC Assignment\\LimZeHongTP053115\\" +
                    "src\\csv\\time_series_covid19_recovered_global.csv"
    );

    private String path;

    CaseType(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
