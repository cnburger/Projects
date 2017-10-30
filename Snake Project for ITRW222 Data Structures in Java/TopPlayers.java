import java.io.*;
/**
*@author Christiaan Neil Burger
*@version Final
*/
public class TopPlayers
{
	private String name;			//user name
	private int score;				//user score
	
	TopPlayers()					//default constructor
	{
		this("Empty",0);
	}
	
	TopPlayers( String n, int s)	//constructor n= name, s=score
	{
		setName(n);
		setScore(s);		
	}
	
	
	public String getName()
	{
		return this.name;
	}	
	
	public int getScore()
	{
		return this.score;
	}

	public void setName(String n)
	{
		this.name = n;
	}	
	
	public String toString()
	{
		return getScore()  + "\t  "+ getName() + " ";		//to string of the score and name
	}
	
	public void setScore(int s)
	{
		this.score = s;
	}
	
}