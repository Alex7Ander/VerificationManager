package ToleranceParamPack.ParametrsPack;

public enum S_Parametr implements ToleranceParametrCharacteristic {
	S11, S12, S21, S22;

	@Override
	public String getTableNamePart() {
		String res = null;
        switch(this) {
            case S11:
                res = "����������� ��������� �� 1-�� �����";
                break;
            case S12:
                res = "����������� �������� S12";
                break;
            case S21:
            	res = "����������� �������� S21";
            	break;
            case S22:
            	res = "����������� ��������� �� 2-�� �����";
            	break;
        }
        return res;
	}

}
