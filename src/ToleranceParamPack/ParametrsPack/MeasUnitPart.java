package ToleranceParamPack.ParametrsPack;

public enum MeasUnitPart implements ToleranceParametrCharacteristic {
    MODULE, PHASE;

	@Override
    public String getTableNamePart(){
        String res = null;
        switch(this) {
            case MODULE:
                res = "������";
                break;
            case PHASE:
                res = "����";
                break;
        }
        return res;
    }

    public String getMarker(){
        return null;
    }
    
}