/**
*@author Christiaan Neil Burger
*@version Final
*/
public class SLL<T extends Comparable<? super T>>
{   
	private Element<T> head;  // list header
	private Element<T> tail;
	
    public SLL()
    {  head = null;  
	   tail = null;        
    }//25788426 C.N Burger
	public T getHead()
   {
	   
	   if (head == null)//item not found
		   return null;
	   else 
		   return head.data;
   }
   public T deleteHead()
   {
	   T deleted;
	   if (head == null)//item not found
		   return null;
	   else 
	   {
		   deleted = head.data;
		   head = head.next;
		   return deleted;
	   }
   }
   public T getTail()
   {
	   
	   if (head == null)//item not found
		   return null;
	   else 
		   return tail.data;
   }

    public boolean prepend(T newElement) //insert at head
    {  
		Element<T> temp = new Element<T>(newElement);
        if(temp == null) // out of memory
           return false;
        else
        {  
	       if (head==null)
		   {
			head = temp;
			tail = temp;
		   }
           else
           {
			 temp.next = head;
			 head = temp;
		   }
		}
        return true;
    }
	 public boolean append(T newElement) //insert at tail
    {  
		Element<T> temp = new Element<T>(newElement);
        if(temp == null) // out of memory
           return false;
        else
        {  
	       if (head==null)
		   {
			head = temp;
			tail = temp;
		   }
           else
           {
			 tail.next = temp;
			 tail = temp;
		   }
		}
     return true;
    }
    
   public boolean delete(T item)
   {
	   Element<T> ptr = head;
	   Element<T> prevPtr = null;
	   while (ptr!= null&& ptr.data.compareTo(item)!= 0)
	   {
		   prevPtr=ptr;
		   ptr=ptr.next;
	   }
	   if (ptr == null)//item not found
		   return false;
	   if (ptr==head) // item is first element
		   head= ptr.next;
	   else // general case
		   prevPtr.next=ptr.next;
	   if (ptr==tail)// last element
		   tail=prevPtr;
	   return true;
   }
   public boolean isMember(T item)
   {
	   Element<T> ptr = head;
	   if (head == null)
		   return false;
	   if (item == null)
		   return false;
	   while (ptr!= null)
	   {
		   if (ptr.data.compareTo(item)== 0)
			   return true;
			   
		   ptr=ptr.next;
	   }
	   return false;   
   }
  

	public String toString()
	{
		String s="";
		Element<T> ptr = head;
		while (ptr != null) //continue to traverse the list
		{   
			s=s + ptr.data + " ";
			ptr = ptr.next;
		}
		return s;
	}
   
  
	
  public class Element<T extends Comparable<? super T>>
  {   
      private T data;
      private Element<T> next;
      public Element(T param)
     {
		 data = param;
     }
	  	
   }// end of inner class Node
}//end SinglyLinkedList outer class

