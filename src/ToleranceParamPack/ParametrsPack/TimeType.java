package ToleranceParamPack.ParametrsPack;

public enum TimeType implements ToleranceParametrCharacteristic {
    PRIMARY, PERIODIC;

    public String getTableNamePart(){
        String res = null;
        switch(this) {
            case PRIMARY:
                res = "���������";
                break;
            case PERIODIC:
                res = "�������������";
                break;
        }
        return res;
    }
}