package tones;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class ArrayDictionary<K extends Comparable<? super K>,V extends Comparable<? super V>> implements DictionaryInterface<K, V> 
{

	private Entry<K,V>[] myData;
	private int currentSize;
	private boolean initialized = false;
	private final static int DEFAULT_CAPACITY = 25;
	private final static int MAXIMUM_CAPACITY = 100000;
	
	public ArrayDictionary()
	{
		this(DEFAULT_CAPACITY); 
	}
	
	public ArrayDictionary(int capacity)
	{
		checkCapacity(capacity);
		@SuppressWarnings("unchecked")
		Entry<K,V>[] temp = (Entry<K,V>[]) new Entry[capacity];
		myData = temp;
		currentSize = 0;
		initialized = true;
	}
	
	private void checkCapacity(int capacity)
	{
		if(capacity > MAXIMUM_CAPACITY)
			throw new IllegalStateException("Attempt to create a dictionary whose " + 
											"capacity exceeds allowed maximum of " + MAXIMUM_CAPACITY);										
	}
	
	private void ensureCapacity()
	{
		if( currentSize == myData.length - 1 )
		{
			int newLength = 2*myData.length;
			checkCapacity(newLength);
			myData = Arrays.copyOf(myData, newLength);
		}
	}
	
	private void checkInitialization()
	{
		if (!initialized)
			throw new SecurityException("ArrayDictionary object is not initialized properly.");
	}
	
	@Override
	public V add(K key, V value) 
	{
		checkInitialization();
		if (( key == null || value == null))
			throw new IllegalArgumentException();
		else
		{
			V result = null;
			int keyIndex = findIndex(key);
			
			if( keyIndex < currentSize )
			{
				// Key was found!  return and replace the old value.
				result = myData[keyIndex].getValue();
				myData[keyIndex].setValue(value);
			}
			else
			{
				//Key was not found!  Add at end of array.
				myData[currentSize] = new Entry<>(key, value);
				currentSize++;
				ensureCapacity();
			}

			return result;
		}
	}	//end of add method

	// this is a generic sequential "find" method for an unsorted dictionary.
	// for a sorted dictionary, we'll do something smarter.
	private int findIndex(K key)
	{
		int index = 0;
		while (index < currentSize && !key.equals(myData[index].getKey()))
			index++;
		return index;
	}
	
	@Override
	public V remove(K key) 
	{
		checkInitialization();
		V result = null;
		int keyIndex = findIndex(key);
		if( keyIndex < currentSize )
		{
			result = myData[keyIndex].getValue();
			myData[keyIndex] = myData[currentSize - 1];
			//move the entry at the end of the array to this spot.
			//note: if this array is sorted, removing an entry this way is dumb.
			myData[currentSize - 1] = null;
			currentSize--;
		}
		return result;
	}

	@Override
	public V getValue(K key) 
	{
		checkInitialization();
		V result = null;
		int keyIndex = findIndex(key);
		if( keyIndex < currentSize )
		{
			result = myData[keyIndex].getValue();
		}
		
		return result;
		
	}

	@Override
	public boolean contains(K key) 
	{
		checkInitialization();
		int keyIndex = findIndex(key);
		if( keyIndex < currentSize )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public Iterator<K> getKeyIterator() 
	{
		return new KeyIteratorForArrayDictionary();
	}

	@Override
	public Iterator<V> getValueIterator() 
	{
		return new ValueIteratorForArrayDictionary();
	}

	@Override
	public boolean isEmpty()
	{
		return currentSize == 0;
	}

	@Override
	public int getSize() 
	{
		return currentSize;
	}

	@Override
	public void clear() 
	{
		checkInitialization();
		for(int i = 0; i < currentSize; i++)
		{
			myData[i] = null;
		}
		currentSize = 0;
	}
	
	
	
	
	private class KeyIteratorForArrayDictionary implements Iterator<K>
	{
		private int currentIndex;
		
		// default constructor
		private KeyIteratorForArrayDictionary()
		{
			checkInitialization();
			currentIndex = 0;
		}
		
		@Override
		public boolean hasNext() 
		{
			return currentIndex < getSize();
		}

		@Override
		public K next() 
		{
			if(hasNext())
			{
				Entry<K, V> myEntry = myData[currentIndex];
				currentIndex++;
				return (K) myEntry.getKey();
			}
			else
				throw new NoSuchElementException("Illegal call to next(); iterator is at end of dictionary.");
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException("remove() is not supported by this iterator");
		}
		
	}
	
	private class ValueIteratorForArrayDictionary implements Iterator<V>
	{
		private int currentIndex;
		
		// default constructor
		private ValueIteratorForArrayDictionary()
		{
			checkInitialization();
			currentIndex = 0;
		}
		
		@Override
		public boolean hasNext() 
		{
			return currentIndex < getSize();
		}

		@Override
		public V next() 
		{
			if(hasNext())
			{
				Entry<K, V> myEntry = myData[currentIndex];
				currentIndex++;
				return (V) myEntry.getValue();
			}
			else
				throw new NoSuchElementException("Illegal call to next(); iterator is at end of dictionary.");
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException("remove() is not supported by this iterator");
		}
		
	}
}
