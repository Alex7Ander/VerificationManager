package Exceptions;

import DevicePack.Includable;

@SuppressWarnings("serial")
public class NoOwnerException extends SavingException {
	public NoOwnerException(@SuppressWarnings("rawtypes") Includable inclObject){
		super("Owner is not defined for:" + inclObject.toString());
	}
}