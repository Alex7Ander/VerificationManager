package ToleranceParamPack.ParametrsPack;

public enum TimeType implements ToleranceParametrCharacteristic {
    PRIMARY, PERIODIC;

    public String getTableNamePart(){
        String res = null;
        switch(this) {
            case PRIMARY:
                res = "первичной";
                break;
            case PERIODIC:
                res = "периодической";
                break;
        }
        return res;
    }
}