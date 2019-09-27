package DevicePack;

public interface Includable<T>{
	public T getMyOwner();
	public void onAdding(T Owner);
}