package ToleranceParamPack.ParametrsPack;

public enum S_Parametr implements ToleranceParametrCharacteristic {
	S11, S12, S21, S22;

	@Override
	public String getTableNamePart() {
		String res = null;
        switch(this) {
            case S11:
                res = "Коэффициент отражения от 1-го порта";
                break;
            case S12:
                res = "Коэффициент передачи S12";
                break;
            case S21:
            	res = "Коэффициент передачи S21";
            	break;
            case S22:
            	res = "Коэффициент отражения от 2-го порта";
            	break;
        }
        return res;
	}

}
