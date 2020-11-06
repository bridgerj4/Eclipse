package tones;

import java.util.Iterator;

public interface DictionaryInterface<K,V> 
{
	/**
	 * Add a new entry to the dictionary.  If the search key already exists in the dictionary,
	 * replace the value with the new one.  (Return the old one)
	 * @param key	
	 * @param value
	 * @return	Null if the new entry is added without conflict.  The value that was previously
	 * 			associated with the key if that value was replaced.
	 */
	public V add(K key, V value);
	
	/**
	 * Removes the entry in the dictionary corresponding to this key
	 * @param key
	 * @return	The value associated with this key (if one exists).
	 * 			Null if there is no value associated with this key.
	 */
	public V remove(K key);
	
	/**
	 * Retrieves the entry in the dictionary corresponding to this key
	 * @param key
	 * @return	The value associated with this key (if one exists).
	 * 			Null if there is no value associated with this key.
	 */
	public V getValue(K key);
	
	/** 
	 * Determines whether the dictionary contains an entry for this key.
	 * @param key
	 * @return
	 */
	public boolean contains(K key);
	
	/**
	 * Creates an iterator to traverse all key entries in the dictionary.
	 * @return
	 */
	public Iterator<K> getKeyIterator();
	
	/**
	 * Creates an iterator to traverse all value entries in the dictionary.
	 * @return
	 */
	public Iterator<V> getValueIterator();
	
	/**
	 * Checks to see whether the dictionary is empty.
	 * @return
	 */
	public boolean isEmpty();
	
	/**
	 * Gets the size of the dictionary.
	 * @return
	 */
	public int getSize();
	
	/**
	 * Removes all entries in the dictionary.
	 */
	public void clear();
	
}
