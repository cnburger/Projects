/**
*@author Christiaan Neil Burger
*@version Final
*/
public class QueueAsSLL<T extends Comparable<? super T>>
{   
	private SLL<T> theQueue;  // list header
	
	
    public QueueAsSLL()
    {  theQueue = new SLL<T>();       
    }
	
    public boolean enqueue(T newElement) //insert at tail
    {  
		   return theQueue.append(newElement);
    }
	public T peek()
	{
		return theQueue.getHead();
	}
	
	public T dequeue() //remove from head
    {  
		T temp = null;
		temp = (T)theQueue.deleteHead();
		return temp;
    }
	public String toString()
	{
		return theQueue.toString(); 
	}

	 public boolean isMember(T item)
   {
	   return theQueue.isMember(item);
   }
   
}//end class

