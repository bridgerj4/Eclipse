package tones;

public class Entry<K extends Comparable<? super K>,V extends Comparable<? super V>> implements Comparable<Entry<K,V>>
{
	private V value;
	private K key;
	
	public Entry( K key, V value)
	{
		this.key = key;
		this.value = value;
	}
	
	public V getValue()
	{
		return value;
	}
	
	public void setValue(V newVal)
	{
		value = newVal;
	}
	
	public K getKey()
	{
		return key;
	}

	@Override
	public int compareTo(Entry<K, V> o) 
	{
		return this.getValue().compareTo(o.getValue());
		//return this.getKey().compareTo(o.getKey());
	}
	
	
	
}
