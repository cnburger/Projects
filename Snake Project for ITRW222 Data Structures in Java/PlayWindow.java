import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.Random;
import java.lang.Object.*;
import java.io.*;
import java.nio.file.*;
import java.lang.*;
/**
*@author Christiaan Neil Burger
*@version Final
*/

public class PlayWindow extends JFrame  implements KeyListener, ActionListener
{
		//Dimentions of the snake
	    int ROWS = 20;
		int COLS = 20;
		int TIME_DELAY = 300;
	
		int key; 								//international variable to know in other methods if pressed
		
		//Score earning variables
		int scoreToErn = TIME_DELAY*10; 		//because the intitial snake length is 5
		int mainScore =0;
		boolean addScore = false;
		
		//Snake speed variables
		int snakespeed = TIME_DELAY;
		int snakespeedIncDec = 10 ; 			//factor by which the snake gets incremented and decremented
		int maxSpeed = 20;	
		int minSpeed = TIME_DELAY;
		
		//Moving up or down variables
		boolean goingUp = true;
		boolean goingDown = false;
		boolean goingLeft = false;
		boolean goingRight = false;
		
		//Variables for the labels and play screen		
		private JLabel [][] screen = new JLabel[ROWS][COLS];
		private JLabel scoreLabel, scoreUpForGrabsLabel;
		private JFrame frame;
		private JPanel basePanel, playAreaPanel, scorePanel, buttonsPanel,tipsPanel;	
		
		//button Pause events
		private JButton btnPause;
		boolean buttonPressed = false;
		
		//button too Hot
		private JButton btnReduceSpeed;
		
		//Score counter
		public int scoreCounter = 10;
		
		//TEXT FILE OPERATIONS & TOP SCORE
		private Formatter output;
		private Scanner input;
		public 	TopPlayers[] list;
		private TopPlayers tempPlayer;
		public 	String userName;
		private JPanel top10Panel;
		private JLabel[] userLabel; 
		private JButton btnClearHighScores;
		
		//Labels for the Shortcuts
		private JLabel[] shortcutLabels;
		
		//The snake variables
		private Snake mySnake;
	    private SnakeElement mouse;
		private SnakeElement snakeHead;
		private SnakeElement oldSnakeHead;
		
		//The snake timer
		private javax.swing.Timer timer;
		private javax.swing.Timer scoreTimer;
		
		
	PlayWindow()
	{
		super("Welcome to Neil Burger's snake"); 				//form name of the snake	
		
		//Create and set up the window
		frame = this;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(ROWS*35,COLS*35);
		
		// Create Panel for Score and other info		
		scorePanel = new JPanel();
		scoreLabel = new JLabel("Current Score: 0"); 			//default score
		
		populateArrayList();
		userLabel = new JLabel[10]; 							//pupulate the user labels
		top10Panel = new JPanel(); 								//implent the top 10 panel
		top10Panel.setLayout(new GridLayout(11,1,1,1)); 		//Setting the layout sothat the top scores is underneath each other
		top10Panel.setBackground(Color.gray); 					//Setting the backround colour
		
		for ( int i = 0; i<10;i++)
			{
				userLabel[i] = new JLabel(list[i].toString()); 	//initiating the user label and setting text to the user name and score
				top10Panel.add(userLabel[i]);					//adding labels to the top 10 panel		
			}

		//Button to clear the highscores
		btnClearHighScores = new JButton("Clear Scores");		
		btnClearHighScores.addActionListener( new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{		
						clearHighScores(); //calling the clear function
					}
				}
			);
		top10Panel.add(btnClearHighScores);
				
		//BUTTONS INFO
		buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.gray);
		
		//BUTTON PAUSE
		btnPause = new JButton("Pause ('P')"); 					//setting the name of the pause button		
			btnPause.addActionListener( new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{		
						if(buttonPressed) //if button was pressed is true (after the first pres) do the following
						{
							buttonPressed = false;
							pauseGo(buttonPressed);									
						}
						else //if not true do this:
						{	buttonPressed = true;					
							pauseGo(buttonPressed);
						}					
					}
				}
			);
			
		//BTN Reducespeed code
		btnReduceSpeed = new JButton("Reduce Speed ('R')"); 			//Name of the reduce speed button
			btnReduceSpeed.addActionListener( new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{			
					 int isPossible = mainScore - 1000; 				//Points needs to be above the threshold
					 if(isPossible >0)
					 {
						if(slowDown()) //if the slowdown was possible									
						{
							mainScore = (int) Math.floor(isPossible*0.95); 	//reduce the main score -1000 and another 5%
							mySnake.shrink();//shrink the snake		
						}
						else
						{
						System.err.println("Speed is to slow");
						}
					 }
					 playAreaPanel.requestFocusInWindow();				//request focus sothat the user kan keep on moving		 				 
					}
				}
			);
		//Adding buttons to the buttons panel		
		buttonsPanel.add(btnPause);
		buttonsPanel.add(btnReduceSpeed);
		
		
		//Label to denote the scores
		scoreUpForGrabsLabel = new JLabel("Points to earn:");
		scoreLabel.setForeground(Color.white); 							//setting text to white
		scoreUpForGrabsLabel.setForeground(Color.white);
		
		//Adding the labes to the score panel
		scorePanel.add(scoreLabel);
		scorePanel.add(scoreUpForGrabsLabel);
		
		//Setting the backround so that the user can see the snake in a box and for asthetics
		scorePanel.setBackground(Color.gray);
		
		
		//create panel with playing grid
		playAreaPanel = new JPanel();
	    playAreaPanel.setSize(ROWS*50,COLS*50);
		GridLayout playLayout = new GridLayout(ROWS,COLS,1,1);
		playAreaPanel.setLayout(playLayout);
		playAreaPanel.addKeyListener(this);
		
		//create 2D Array of Lables adn populating the screen
		for (int row = 0; row < ROWS; row ++)
			for (int col = 0; col< COLS; col ++)
			{
				screen[row][col] = new JLabel(); 					//creating a new label
				screen[row][col].setOpaque(true); 					//setting the color
				screen[row][col].setBackground(Color.green);
				playAreaPanel.add(screen[row][col]);				//adding each label to the screen
				screen[row][col].setVisible(false);					//setting each visible label to false
			}
			
		//Creating my snake elements
		oldSnakeHead = new SnakeElement();
		snakeHead = new SnakeElement();		
		mySnake = new Snake();
		oldSnakeHead = mySnake.createInitialSnake(ROWS/2,COLS/2,5); //for initial snake creation
		
		//Info panel ------------------------------------------------------------------
		tipsPanel = new JPanel();
		tipsPanel.setLayout(new GridLayout(10,1,1,1));
		shortcutLabels = new JLabel[10];
		shortcutLabels[0] = new JLabel("To reduce");				//Code for user to read on the left as well as a 
		shortcutLabels[1] = new JLabel("press");					//way to make a box around the snake
		shortcutLabels[2] = new JLabel("'R'");
		shortcutLabels[3] = new JLabel("To Pause");
		shortcutLabels[4] = new JLabel("press ");
		shortcutLabels[5] = new JLabel("'P'");
		shortcutLabels[6] = new JLabel("To move");
		shortcutLabels[7] = new JLabel("press");
		shortcutLabels[8] = new JLabel("A|S|D|W");
		shortcutLabels[9] = new JLabel("& arrows");
		
		//populatig the tips panel
		for(int i =0; i<10;i++)
			tipsPanel.add(shortcutLabels[i]);
		//Info Panel END--------------------------------------------------------------
		
		
		//Add panels to BasePanel
		basePanel = new JPanel(); 										//decalring the base panel
		tipsPanel.setBackground(Color.gray);							//setting a bacround colour to dark gray
		BorderLayout bLayout = new BorderLayout(); 						//setting a layout type
		basePanel.setLayout(bLayout);
		basePanel.add(scorePanel, BorderLayout.NORTH); 					//adding the score panel north
		basePanel.add(playAreaPanel, BorderLayout.CENTER); 				//adding the play area in the center
		basePanel.add(buttonsPanel, BorderLayout.SOUTH);				//adding the buttons south	
		basePanel.add(top10Panel, BorderLayout.EAST);					//adding the top 10 on the right
		basePanel.add(tipsPanel, BorderLayout.WEST);					//adding the tips on the left
		frame.add(basePanel);											//adding the base panel to the frame
		playAreaPanel.setFocusable(true);
		playAreaPanel.requestFocusInWindow();							//request focus sothat the buttons wil work when pressed
		frame.setVisible(true);
		
		//Declaring the timers
		timer = new javax.swing.Timer(TIME_DELAY, new TimerListener());
		scoreTimer = new javax.swing.Timer(TIME_DELAY, new ScoreTimerListener());
		
		
		//Asking the user if he/she is ready to begin
		int optionUsed = JOptionPane.showConfirmDialog(frame, "Are you ready???");	//Dialog box to know what was pressed	
		if(optionUsed == 0)
		{
           timer.start(); 															//start the program if the user is ready
		   scoreTimer.start();
		}
		else
			System.exit(0); 														//exit if user clicked on cancel or no
		
		//adding the food / Mouse
		scoreTimer.setDelay(1000); 													//setting second timer time
		addMouse();
		
	}
   public void addMouse()
	{
		Random randomNumbers = new Random();						//random number generator		
		int mouseRow = 0;											//setting the mouse row&column to deufault 0
		int mouseCol = 0;		
		boolean mousePlanted = false;
		
		while (!mousePlanted)
		{
			mouseRow = randomNumbers.nextInt(ROWS); 								//nextInt(n) produce random number from
			mouseCol = randomNumbers.nextInt(COLS);									//0-(n-1) we want a number from 1-(ROWS)
			mouse = new SnakeElement(mouseRow,mouseCol);
			
			if (!mySnake.isMember(mouse)) // mouse not in snake
			{
				 screen[mouse.getRow()][mouse.getCol()].setOpaque(true); 			//Making colour change possible
				 screen[mouse.getRow()][mouse.getCol()].setBackground(Color.black); //setting mouse background black
				 screen[mouse.getRow()][mouse.getCol()].setForeground(Color.white); //setting mouse to white
				 screen[mouse.getRow()][mouse.getCol()].setText("<:3)"); 			//mouse image				 
				 screen[mouse.getRow()][mouse.getCol()].setVisible(true);			//setting mouse to visible
				 mousePlanted = true;												//mouse planted
			}			
		}		
	}
	public void paint(Graphics g) {
        // Call the superclass paint method.
        super.paint(g);
    }	

	@Override
	public void actionPerformed(ActionEvent e) {
       	    playAreaPanel.requestFocusInWindow();									//requests the focus in the window
    }
	@Override
	public void keyPressed(KeyEvent e) 
	{				
		key = e.getKeyCode(); //getting all the info on all the keys pressed
		if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) 	//if arrow left 	is 	pressed 	OR 	A (gaming keys)
		{
			goingUp = goingDown =  goingRight = false;
			goingLeft = true;
		}
		if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)		//if arrow up 		is 	pressed 	OR 	W (gaming keys)
		{
			goingLeft = goingDown =  goingRight = false;
			goingUp = true;
			
		}
		if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)	//if arrow right 	is 	pressed 	OR 	D (gaming keys)
		{
			goingLeft = goingDown =  goingUp = false;
			goingRight = true;
			
		}
		if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)	//if arrow down 	is 	pressed 	OR 	S (gaming keys)
		{
			goingLeft = goingUp =  goingRight = false;
			goingDown = true;
		}
		if(key == KeyEvent.VK_R)										//if R is pressed REDUCE snake according to the rules
		{
			int isPossible = mainScore - 1000;
					 if(isPossible >0)
					 {
						if(slowDown()) //if the slowdown was possible									
						{
							mainScore = (int) Math.floor(isPossible*0.95); 	//reduce the main score -1000 and another 5%
							mySnake.shrink();//shrink the snake		
						}
						else
						{
							System.err.println("Speed is to slow");
						}												
					 }
		}
		if(key == KeyEvent.VK_P)										//if P is pressed PAUSE THE SNAKE
		{
			if(buttonPressed) //if button was pressed is true (after the first pres) do the following
						{
							buttonPressed = false;
							pauseGo(buttonPressed);									
						}
						else
						{	buttonPressed = true;					
							pauseGo(buttonPressed);
						}					
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {} 								//do nothing
	@Override
	public void keyTyped(KeyEvent e) {} 								//do nothing
    
	private class TimerListener implements ActionListener { 			//TIMER CLASS

        public void actionPerformed(ActionEvent e) {
			
			snakeHead = new SnakeElement();
			snakeHead.setRow(oldSnakeHead.getRow());	
			snakeHead.setCol(oldSnakeHead.getCol());
			
			//if user is going en a certian direction keep on going in that direction
            if (goingUp) 
				snakeHead.decrementRow();

			if (goingDown)
				snakeHead.incrementRow();

			if (goingLeft)
				snakeHead.decrementCol();

			if (goingRight)
				snakeHead.incrementCol();
			
			if  (snakeHead.compareTo(mouse)==0)  									//if the snake eats the mouse
			{ 
				addScore = true; 													// so that the if statment will execute below
				System.out.println("addScore TRUE");
				mySnake.grow(snakeHead);
				screen[mouse.getRow()][mouse.getCol()].setOpaque(true);				//setting snake colorable
				screen[mouse.getRow()][mouse.getCol()].setBackground(Color.green);	//making the snake green
				screen[mouse.getRow()][mouse.getCol()].setText("");					//setting the snake text to nothing			
				screen[mouse.getRow()][mouse.getCol()].setVisible(true);			//setting the snake to visile	
				scoreCounter = 10;
				addMouse();															//add a new mouse
			}
			else
			{
				mySnake.moveForward(snakeHead); 									//moving my snake forward if snake havent eaten the mouse
				addScore = false; 													// to make sure the if statment below doenst executes
			}
			
		oldSnakeHead.setRow(snakeHead.getRow());									//setting the ould snake head to a new snake head values i.e. moving forward
		oldSnakeHead.setCol(snakeHead.getCol());			
		score(addScore);															//add score if addScore is true
		scoreLabel.setText("Current Score: " + mainScore); 							//set the new mainscore		
		}
	
	}
	
	//Second Timer class for awarding a score depeding on the time that it takes
	private class ScoreTimerListener implements ActionListener
	{
		
		public void actionPerformed(ActionEvent e)
		{			
			
			if (scoreCounter == 10) 		//Score counter = 10 = 10seconds to find the new mouse
				scoreToErn = dynamicScore();			//Score to be awarded when finding the apple
			
			if( scoreCounter > 0)
			{
				scoreCounter--;
				System.out.println(scoreCounter);
				scoreToErn = scoreToErn - dynamicScore()/10;
			}
			else
			{
				scoreCounter = 10;
				scoreToErn = dynamicScore();	
				screen[mouse.getRow()][mouse.getCol()].setOpaque(true);				//setting snake colorable
				screen[mouse.getRow()][mouse.getCol()].setBackground(Color.green);	//making the snake green
				screen[mouse.getRow()][mouse.getCol()].setText("");					//setting the snake text to nothing			
				screen[mouse.getRow()][mouse.getCol()].setVisible(false);	
				addMouse();				
			}
		
		scoreUpForGrabsLabel.setText("Points to earn: " + scoreToErn);				//setting the score that the user can ern
		}
	}
	public int dynamicScore() //making the score that counts down to be earned dynamic depending on the size of the snake & time to eat
	{
		return (int) Math.floor(mySnake.getSize()*1100);
	}

//===================================================SCORE CALCULATOR======================================
	public void score(boolean addTheScore)
	{ 	
		if(addTheScore) 						//if true to add the score do
		{
			mainScore = mainScore + scoreToErn; //Main Score adding the new score
			speedUp();							//speed up the snake
		}
	}
	
	public void gameOver()
	{
		timer.stop(); 																		//stop the timer
		scoreTimer.stop(); 																	//stop the score timer
		JFrame frame = new JFrame("Input"); 												//Naming the form score
		userName = JOptionPane.showInputDialog(frame,"Enter a one word username:");			//opening the dialogbox
		insertUser(userName,mainScore); 													//insert the new user in the textfile
		int optionUsed = JOptionPane.showConfirmDialog(frame, "GAME OVER! \nDo you want to retry?"); //getting the Yes No cancel
		
		if(optionUsed == 0)//YES
		{
			try{
			    Runtime.getRuntime().exec("cmd /c start \"\" run.bat");
				dispose(); //restarts the program
			}
			catch (IOException e)
			{
				System.out.println("FileNotFound Error");
				System.exit(0);
			}			
		}
		else if (optionUsed == 1) //NO
		{
			System.exit(0);
		}
		else if(optionUsed == 2) //CANCEL
		{
			JOptionPane.showMessageDialog(frame,"You will be returned to the image of your snake dying....");
		}		
	}
	
//----------------------------SNAKE SPEED METHODS----------------------------------------------------
	public void speedUp()
	{
		if((snakespeed > maxSpeed) ) //maximum speed
		{
			snakespeed = (int) Math.floor(TIME_DELAY*(Math.pow(0.95,Math.floor(mainScore/10000))));
			//Gets the floor value, TimeDelay*(the no of times the speeds needs to be reduced depending on the score of the user floor of Score/1000)
			//the speeds only gets increased every 10000 points the user scores
			timer.setDelay(snakespeed); //set new speed
		}
			
	}
	//default slowdown
	public boolean slowDown()
	{
		if(snakespeed < minSpeed) 								//smaller speed = faster snake = because of snake interval
		{
			snakespeed = (int) Math.floor(snakespeed*1.15); 	//make the snake 15% slower with the current speed
			timer.setDelay(snakespeed); 						//set new speed
			return true;  										//return true if succesful
		}
		else
			return false;										//return false if snake speed is slower that minimum
	}

//---------------------------------------------------- END OF SNAKE SPEED METHODS -------------------------------------------
	
	public void pauseGo(boolean pause)
	{
		if(pause) // if pause is pressed do the following
		{
			timer.stop(); 							//Stop the timer
			scoreTimer.stop();						//Stop the score timer
			playAreaPanel.requestFocusInWindow(); 	//so that the snake will move normally with the arrows	
		}
		else
		{
			timer.start(); 							// start the timer
			scoreTimer.start();						//start the score timer
			playAreaPanel.requestFocusInWindow(); //so that the snake will move normally with the arrows	
		}

	}
	
//---------------------------------------------------TEXT FILE OPERATIONS---------------------------------------------------------	
	public void insertUser(String name, int score)
	{
		list = new TopPlayers[10]; 								//initiate to make sure it is
		//populat the array with current scores	
		openReadFromFile();	
		readRecords();											
		closeReadFromFile();
		
		
		tempPlayer = new TopPlayers(name,score); //making a temp player to be written to the textfile
		
		int i =0;
		boolean stop = true;
				
		while(i< list.length && stop != false)
		{	try
			{ 
				if(tempPlayer.getScore() > list[i].getScore()) 	//list is always going to be sorted 
				{
					
					for( int k = list.length-1; k >i ; k--)   	//move the rest down inser user
					{
						list[k] = list[k-1];
					}
					list[i] = tempPlayer;
					stop=false; 								//to stop the loop
				}
			}
			catch (NullPointerException e)
			{
				//catch nothing
			}
			i++;
			
		}
		
	//adding that new score
	openWriteToFile();
	addRecords();
	closeWriteToFile();
	}
	
	public void populateArrayList()
	{
		openReadFromFile(); 	//open
		readRecords();			//populate
		closeReadFromFile();	//close
	}
	
	public void printList()
	{
		for(int i = 0; i<10;i++)
		{
			System.out.println("Position: " + (i+1) + " "+ list[i].toString()); //get values and output
		}
		System.out.println("");
	}
	
	public void openWriteToFile()
	{
		try
		{
			output = new Formatter("TopUsers.txt");  				//setting an output file 
		}
		catch (SecurityException e)
		{
			System.err.println("Write permission denied: "+e);  	//error
		}		
		catch(FileNotFoundException e)
		{
			System.err.println("File not found: "+e);				//error
		}
	}
	
	public void openReadFromFile()
	{
		try
		{
			input = new Scanner(Paths.get("TopUsers.txt"));		//set input stream
		}
		catch (IOException e)
		{
			System.err.println("Read Error: " +e);				//error
		}
	}
	public void addRecords()
	{
		int i = 0; 																//counter
		
		while(i < 10)
		{
			try
			{
				output.format("%s %d ",list[i].getName(), list[i].getScore());  //try to write to file
			}
			catch(FormatterClosedException e)
			{
				System.err.println("Formatter closed:" + e);					//catch closed file
			}
			catch(NoSuchElementException e)
			{
				System.err.println("Not found: " + e);							//catch no such element
			}
			
			i++;
		}
	}
	
	public void clearHighScores()
	{
		openWriteToFile();  											//open file
		TopPlayers temp = new TopPlayers();								//create default temp
		
		int i = 0;
		
		while(i<10) 													//while 0,1,2...9
		{
			try
			{
				output.format("%s %d ",temp.getName(), temp.getScore());//write default temp (Empty,0)
			}
			catch(FormatterClosedException e)
			{
				System.err.println("Formatter closed:" + e);			//if closed catch
			}
			catch(NoSuchElementException e)
			{
				System.err.println("Not found: " + e);					//catch no such element
			}
			
			i++;
		}
		closeWriteToFile();
	}
	
	public void readRecords()
	{
		list = new TopPlayers[10];  									//set the list
		int i = 0;														//counter
		try
		{
			while(input.hasNext())
			{
				list[i] = new TopPlayers(input.next(),input.nextInt());	//set values form textfile to the list
				i++;
			}
		}
		catch (NoSuchElementException e)
		{
			System.err.println("File Error: " + e);						//catch no such element
		}
		catch(IllegalStateException e)
		{
			System.err.println("Read Stat Error: " +e);					//catch read stat error
		}
		catch(Exception e)
		{
			openWriteToFile();											//when nullpoiunter write some data to file
			clearHighScores();
			closeWriteToFile();
		}
		
	}
	public void closeWriteToFile()
	{						//close write to the file
		if(output!=null)
			output.close();
	}
	
	public void closeReadFromFile()
	{						//close reat to the file
		if (input!= null)
			input.close();
	}

//-----------------------------------------------END OF TEXT FILE OPERATIONS------------------------------------------------------	
	
	public class Snake 
	{ 
	    int maxLength = (ROWS * COLS)-1;   //max length -1
		public QueueAsSLL <SnakeElement> theSnake = new QueueAsSLL <SnakeElement>();
		int snakeLength=0; //initial length
		
		public Snake()
		{
			snakeLength = 0;
		}
		public SnakeElement createInitialSnake(int row, int col, int size)
		{
			SnakeElement temp=new SnakeElement();
			SnakeElement tt=new SnakeElement();
			// create small snake to start with 5 elements
			int i;
			for (i = (size-1); i>=0; i--) 				//Swapped order
			{
				temp = null;
				temp = new SnakeElement(row,col+i);
				theSnake.enqueue(temp); 				//make the queue longer
				screen[row][col+i].setVisible(true);	//setting labels visible
			}
			snakeLength = size;							//setting the snake size
			tt.deepCopy(temp);							//deep copy temp to tt
			return tt;									//return tt
		}

		public void moveForward(SnakeElement newHead)
		{
			SnakeElement t,t1;
			if (snakeLength < maxLength && snakeLength >0)   							//snake longer than 0 and smaller than maximum length
			{
				t = (SnakeElement) theSnake.dequeue(); 									//t is the previous head value
				screen[t.getRow()][t.getCol()].setVisible(false);						//set head falise (head == snake tail)
				t1 = new SnakeElement(newHead.getRow(),newHead.getCol());				//set new head
				if (theSnake.isMember(t1))
				{
					gameOver();															//if snake eat himself end game
				}
				else
				{
					if (theSnake.enqueue(t1))
						screen[newHead.getRow()][newHead.getCol()].setVisible(true);	// increase the snake
				}
			}			
		}
		
		public void shrink()
		{
			int noOfTimes = (int) Math.floor(snakeLength*0.25); 					//gets 25% of the current length ti get the size to shrink
			SnakeElement tempValue = new SnakeElement(); 							//temp value to get the delteted values row and col pos
			
			if(noOfTimes < snakeLength)
			for( int i = 0; i < noOfTimes; i++)
			{
				tempValue = theSnake.dequeue(); //delete the head
				screen[tempValue.getRow()][tempValue.getCol()].setVisible(false); 	//make the corresponding labels invisible
				snakeLength--;														//decrease the lenght of the snake according to the no of times
			}
			
		}
		
		public void grow(SnakeElement newHead)
		{
			
			SnakeElement t,t1; 
			if (snakeLength < maxLength && snakeLength >0)  						//length smaller than max bigger than 0
			{
				t1=new SnakeElement(newHead.getRow(),newHead.getCol());				//new "growth element"
				if (theSnake.enqueue(t1))											//if enqued worked
				{
				    screen[newHead.getRow()][newHead.getCol()].setVisible(true); 	//set new head true
					snakeLength++;													//make the increment the snake length
				}

			}			
		}
		public boolean isMember(SnakeElement item) 
		{
			return theSnake.isMember(item); //return if is member true
		}
		public int getSize()
		{
			return snakeLength; //return length
		}
	}
		
	public class SnakeElement implements Comparable<SnakeElement>
	{
		//row and col positions
		int rowPos;
		int colPos;
		
		public SnakeElement() //default constructor
		{
			this(0,0);
		}
		
		public SnakeElement(int r, int c) //constructor
		{
			if (r<ROWS)
				rowPos=r;
			else
				rowPos=0;
			if (c<COLS)
				colPos=c;
			else
				colPos=0;
		}
		
		void deepCopy(SnakeElement param)
		{
           rowPos=param.rowPos;
		   colPos=param.colPos;
		}
		
		void incrementRow() //incrementing the rows (Going down)
		{
			if (rowPos < (ROWS-1))
			{
				rowPos++;
			}
			else
			{
				rowPos = 0;
			}
		}
		void decrementRow() //decrementing the row - going up
		{
			if (rowPos > 0)
			{
				rowPos--;
			}
			else
			{
				rowPos = ROWS-1;
			}
		}
		void incrementCol() //incrementing the colums - going right
		{
			if (colPos < (COLS-1))
			{
				colPos++;
			}
			else
			{
				colPos = 0;
			}
		}
		void decrementCol() //decrementing the columns - going left
		{
			if (colPos > 0)
			{
				colPos--;
			}
			else
			{
				colPos = COLS-1;
			}
		}
		//Mutators follow
		int getRow () 
		{
			return rowPos;
		}
		void setRow (int r)
		{
			rowPos = r;
		}
		int getCol ()
		{
			return colPos;
		}
		void setCol (int c)
		{
			colPos =c;
		}	
	
	 @Override //toString method
		public String toString()
		{
			String s = "("+rowPos+ "," +colPos+")";
			return s;
		}
  	@Override //compareTo method
		public int compareTo(SnakeElement param)
		{
			if (param.colPos==colPos && param.rowPos==rowPos)
				return 0;
			else return -1;
		}
	}
	
 public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			@Override
			public void run() {
				new PlayWindow().setVisible(true);
			}
		});
	} 

}