package com.blockchain.test;

public class DataModel {

    String adName;
    String ssp;

    String ts;
    String dataType;

    int radom;

    public DataModel(String adName, String ssp, String ts, String dataType, int radom) {
        super();

        this.adName = adName;
        this.ssp = ssp;
        this.ts = ts;
        this.dataType = dataType;
        this.radom = radom;
    }

    @Override
    public String toString() {
        return adName + ", " + ssp + ", " + ts + ", " + dataType + ", " + radom;
    }

}
