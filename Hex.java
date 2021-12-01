import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
/**Hex Class*/
public class Hex implements Hex_i,ActionListener,Cloneable{
	private JFrame frame;
	private JButton t_button,reset,load,save,undo,newG;
    private JButton[][] hexCells_but;
	private JRadioButton rb2,rb1;
	private JLabel game_label,score1_label,score2_label,empty_label;
	
	private JTextField tf;
	private JButton s_B;
	private JFrame frame1;
	
	private Cell[][] hexCells;
	private Cell[] moves;
	private	char last_char;
	private	int size,type,last_int;

	@Override
	public void setSize(int _size_){ size=_size_;}//Sets size
	@Override
	public int getSize(){ return size; }//Returns size
	@Override
	public void setType(int _type_){ type=_type_;}//Sets type
	@Override
	public int getType(){ return type; }//Returns type
	/**Default Constructor*/
	Hex(){ }
	/**Takes The Board Size From Player*/
	private void size(){
		String _size_;
		try{
			_size_=JOptionPane.showInputDialog(frame,"\n\tEnter width of board:(5->16)","5");
			if (_size_ == null ) System.exit(0);
			size = Integer.parseInt(_size_);
		}catch(Exception e){
			if (e instanceof NumberFormatException ) {
				JOptionPane.showMessageDialog(frame,"Invalid Input","ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
			size();
		}
		if (size> 16 || size<5 ) {
			JOptionPane.showMessageDialog(frame,"Invalid Size","ERROR",
			JOptionPane.ERROR_MESSAGE);
			size();
		}else
			setSize(size);
	}
	/**Takes The Game Type From Player*/
	private void type(){
		frame =new JFrame("Type");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500,200);
		frame.setSize(300,300);
		frame.setResizable(false);
		frame.setLayout(null);  
		rb1=new JRadioButton("1-Play With Friend");
		rb1.setBounds(75,50,200,30);
		rb1.addActionListener(this);    
		rb2=new JRadioButton("2-Play With Computer");
		rb2.setBounds(75,100,200,30);
		rb2.addActionListener(this);
		ButtonGroup type_opt=new ButtonGroup();
		type_opt.add(rb1);
		type_opt.add(rb2);
		t_button=new JButton("OK");
		t_button.setBounds(100,150,80,30);
		t_button.addActionListener(this);
		frame.add(rb1);
		frame.add(rb2);
		frame.add(t_button);
		frame.setVisible(true);
	}
	/**Starts The Game*/
	@Override
	public void playGame(){
		size();
		type();
		create_table();	
	}
	/**Sets And Adds The Labels*/
	private void setLabels(){
		Border border =BorderFactory.createLineBorder(Color.BLACK,1);
		Border border2 =BorderFactory.createLineBorder(Color.RED,1);
		game_label= new JLabel();
		game_label.setVisible(true);
		game_label.setBounds(0,0,(size/2+size)*44,(size*44));
		game_label.setBorder(border);
		game_label.setBackground(Color.BLACK);
		game_label.setOpaque(true);
		frame.add(game_label);
	
		score1_label= new JLabel("Player 1: "+userScore(Cell_type.PLAYER1.type));
		score1_label.setBounds((size/2+size)*44,150,150,30);
		score1_label.setBorder(border2);
		score1_label.setBackground(Color.RED);
		score1_label.setForeground(Color.WHITE);
		score1_label.setOpaque(true);
		frame.add(score1_label);
		if (getType()==2) {
			score2_label= new JLabel("Computer: "+userScore(Cell_type.COMPUTER.type));
		}else
			score2_label= new JLabel("Player 2: "+userScore(Cell_type.PLAYER2.type));
		score2_label.setBounds((size/2+size)*44,180,150,30);
		score2_label.setBorder(border2);
		score2_label.setBackground(Color.BLUE);
		score2_label.setForeground(Color.WHITE);
		score2_label.setOpaque(true);
		frame.add(score2_label);

		empty_label= new JLabel();
		empty_label.setVisible(true);
		empty_label.setBounds((size/2+size)*44,180,150,(size*44)-180);
		empty_label.setBackground(Color.DARK_GRAY);
		empty_label.setOpaque(true);
		frame.add(empty_label);
	}
	/**Sets And Adds The Buttons*/
	private void setButtons(){
		frame=new JFrame("Hex Game");
		frame.setLocation(2500/size,900/size);
		frame.setSize((size/2+size)*44+150, (size*44)+40);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(null);
		
		hexCells_but=new JButton[size][size];
		int k=0;
		for (int i=0;i<size ;i++ ) {
			for (int j=0;j<size ;j++ ) {
				k=22*(j);
				hexCells_but[i][j] =new JButton();
				hexCells_but[i][j].setBounds((i*44)+k,j*44,44,44);
				hexCells_but[i][j].setBackground(Color.GRAY);
				hexCells_but[i][j].addActionListener(this);
				frame.add(hexCells_but[i][j]);
			}
		}
		newG=new JButton("NEW");
		newG.addActionListener(this);
		newG.setBackground(Color.CYAN );
		newG.setBounds((size/2+size)*44,0,150,30);
		frame.add(newG);
		
		load=new JButton("LOAD");
		load.addActionListener(this);
		load.setBackground(Color.ORANGE );
		load.setBounds((size/2+size)*44,30,150,30);
		frame.add(load);
	
		save=new JButton("SAVE");
		save.addActionListener(this);
		save.setBackground(Color.WHITE );
		save.setBounds((size/2+size)*44,60,150,30);
		frame.add(save);

		reset=new JButton("RESET");
		reset.addActionListener(this);
		reset.setBackground(Color.GREEN );
		reset.setBounds((size/2+size)*44,90,150,30);
		frame.add(reset);
		
		undo=new JButton("UNDO");
		undo.addActionListener(this);
		undo.setBackground(Color.YELLOW );
		undo.setBounds((size/2+size)*44,120,150,30);
		frame.add(undo);
		
		frame.setVisible(true);
		setLabels();
	}
	/**Button Actions*/
	@Override
    public void actionPerformed(ActionEvent e){
		if (e.getSource() == t_button) {
			if(type==1 || type==2){
				frame.dispose();
				JOptionPane.showMessageDialog(new JFrame(),"Player1: RED   Left->Right\nPlayer2: BLUE  Up->Down\n\nFirst One To Connects Edges Wins\n              GOOD LUCK!!","RULES",
				JOptionPane.INFORMATION_MESSAGE);
				setButtons();
			}else{
				JOptionPane.showMessageDialog(new JFrame(),"Please Select A Type","ERROR",
				JOptionPane.ERROR_MESSAGE);
			}
		}else if(e.getSource() == rb1){
			setType(1);
		}else if(e.getSource() == rb2){
			setType(2);
		}else if(e.getSource() == reset){
			frame.dispose();
			create_table();
			setButtons();
		}else if(e.getSource() == undo){
			undo();
			setLabels();
		}else if(e.getSource() == load){
			String filename =JOptionPane.showInputDialog(frame,"\tFile Name: ","save1.txt");
			loadGame(filename);
		}else if(e.getSource() == save){
			String filename =JOptionPane.showInputDialog(frame,"\tFile Name: ","save1.txt");
			saveGame(filename);
		}else if(e.getSource() == newG){
			int a=JOptionPane.showConfirmDialog(new JFrame(),"Are you sure?","NEW GAME??",JOptionPane.YES_NO_OPTION);  
			if(a==JOptionPane.YES_OPTION){
				frame.dispose();
				setType(0);
				playGame();
			}else 
				return;  
		}else{
			int i=0,j=0;
			for (i=0;i<size;i++)
				for (j = 0; j < size; j++) 
					if(e.getSource()==hexCells_but[i][j]){
						play(extractCell(j,(char)(i+'a')));
						if(isFinished()==1){
							int a=JOptionPane.showConfirmDialog(new JFrame(),"One More Game??","PLAY AGAIN",JOptionPane.YES_NO_OPTION);  
							if(a==JOptionPane.YES_OPTION){
								frame.dispose();
								setType(0);
								playGame();
							}else  
								System.exit(0);
						}else if (type==2) 
							play();
					}
		}
	}
	/**Action Of UNDO Button*/
	private void undo(){
		if (moves!=null && marked_cells()!=0){
			for (int i = 0; i < getType(); ++i){
				int y=moves[marked_cells()-1].getY();
				char x = moves[marked_cells()-1].getX();
				putTable(Cell_type.EMPTY.type,y,x-'a');
				hexCells_but[x-'a'][y].setBackground(Color.GRAY);
				hexCells_but[x-'a'][y].setEnabled(true);
			}
			score1_label.setText("Player 1: "+userScore(Cell_type.PLAYER1.type));
			if (getType()==2) {
				score2_label.setText("Computer: "+userScore(Cell_type.COMPUTER.type));
			}else
				score2_label.setText("Player 2: "+userScore(Cell_type.PLAYER2.type));
		}else
			JOptionPane.showMessageDialog(new JFrame(),"NO MOVES","ERROR",
				JOptionPane.ERROR_MESSAGE);
	}
	/**Saves The Game To File*/
	@Override
	public void saveGame(String filename){
		try{
			BufferedWriter bw =new BufferedWriter(new FileWriter(filename));
			bw.write((int)getSize());bw.write('\n');
			bw.write(getType());bw.write('\n');
			for (int i = 0; i < getSize(); ++i){
				for (int j = 0; j < getSize(); ++j)
					bw.write(getTable(i,j));
				bw.write('\n');
			}
			bw.write(marked_cells());bw.write('\n');
			for (int i = 0; i < marked_cells(); ++i){
				bw.write(moves[i].getY());bw.write(moves[i].getX());bw.write('\n');
			}
			bw.close();
			JOptionPane.showMessageDialog(new JFrame(),"Your progress has been saved.","SAVED",
				JOptionPane.PLAIN_MESSAGE);
		}catch(Exception e){
			JOptionPane.showMessageDialog(new JFrame(),"Something Happened !!!","ERROR",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	/**Changes The Buttons Color After Game Load*/
	private void synchronizeButtons(){
		for (int i = 0; i < getSize(); ++i){
			for (int j = 0; j < getSize(); ++j)
				if (getTable(i,j)==Cell_type.PLAYER1.type) {
					hexCells_but[j][i].setBackground(Color.RED);
					hexCells_but[j][i].setEnabled(false);
				}else if(getTable(i,j)==Cell_type.PLAYER2.type || getTable(i,j)==Cell_type.COMPUTER.type){
					hexCells_but[j][i].setBackground(Color.BLUE);
					hexCells_but[j][i].setEnabled(false);
				}else if(getTable(i,j)==Cell_type.EMPTY.type){
					hexCells_but[j][i].setBackground(Color.GRAY);
					hexCells_but[j][i].setEnabled(true);
				}
			}
	}
	/**Loads Game From File*/
	@Override
	public void loadGame(String filename){
		try{
			BufferedReader br =new BufferedReader(new FileReader(filename));
			setSize(br.read());br.read();
			setType(br.read());br.read();
			create_table();
			for (int i = 0; i < getSize(); ++i){
				for (int j = 0; j < getSize(); ++j)
					putTable((char)br.read(),i,j);
				br.read();
			}
			int mark=br.read();br.read();
			moves = new Cell[mark];
			for (int i = 0; i <mark; ++i){
				moves[i] = new Cell();
				moves[i].setY((char)(br.read()));
				moves[i].setX((char)(br.read()));
				br.read();
			}
			br.close();
		}catch(Exception e){
			if (e instanceof FileNotFoundException){
				JOptionPane.showMessageDialog(new JFrame(),"The File Does Not Exist!!!","ERROR",
					JOptionPane.ERROR_MESSAGE);
			}else if (e instanceof NullPointerException){
				return;
			}else
				JOptionPane.showMessageDialog(new JFrame(),"Something Happened!!!","ERROR",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		frame.dispose();
		setButtons();
		synchronizeButtons();
	}
	/**Checks The Game If It Is Finished Or Not*/
	@Override
	public int isFinished(){
		for (int i = 0; i < getSize(); ++i)
			for (int j = 0; j < getSize(); ++j){
				if (getTable(i,j)==Cell_type.PLAYER1.type-32 || getTable(i,j)==Cell_type.PLAYER2.type-32|| getTable(i,j)==Cell_type.COMPUTER.type-32)
					return 1;
			}
		return 0;
	}
	/**Returns The Wanted Cell */
	private Cell extractCell(int y,char x){ return hexCells[y][x-'a']; }
	/**Puts The Type In Cell*/
	private void putTable(char player,int y,int x){	
		hexCells[y][x].setX((char)(x+'a'));	
		hexCells[y][x].setY(y);
		hexCells[y][x].setState(player);
	}
	/**Resets The Table*/
	private void create_table(){
		moves = new Cell[1];
		hexCells= new Cell[size][size];
		for (int i = 0; i < size; ++i){
			for (int j = 0; j < size; ++j){
				hexCells[i][j] = new Cell();
				hexCells[i][j].setX((char)(j+'a'));
				hexCells[i][j].setY(i);
			}
		}
		if (hexCells == null){
			JOptionPane.showMessageDialog(new JFrame(),
			"Something happened while memory allocation","ERROR!!!",JOptionPane.ERROR_MESSAGE);
		}else
			for (int i = 0; i < size; ++i){
				for (int j = 0; j < size; ++j){
					putTable(Cell_type.EMPTY.type,i,j);
				}
			}
	}
	/**Determines The Turn*/
	private int check_turn(){
		int counter= 0;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (getTable(i,j) != Cell_type.EMPTY.type){
					counter+=1;
				}
		if (counter%2==0)
			return 1;
		else
			return 2;
	}
	/**Reterns The Wanted Cell's State*/
	private char getTable(int y,int x){
		if (0<=x && 0<=y && x<size&& y<size){
			return hexCells[y][x].getState();
		}else
			return Cell_type.EMPTY.type;
	}
	/**Searchs For The Beginning Of The Path */
	private int find_initial(char player,int x,int y){
		int check=0;
		if (player==Cell_type.PLAYER1.type){
			for (int i = x; i <=size-1; ++i)
				if (getTable(i,y)==Cell_type.PLAYER1.type){
					check=check_win(Cell_type.PLAYER1.type,i-1,y+1);
					if (check==1){
						putTable((char)(Cell_type.PLAYER1.type-32),i,y);
						hexCells_but[y][i].setBackground(Color.PINK);
					}
				}
		}else if (player==Cell_type.PLAYER2.type)
			for (int i = y; i <=size-1; ++i)
				if (getTable(x,i)==Cell_type.PLAYER2.type){
					check=check_win(Cell_type.PLAYER2.type,x+1,i-1);
					if (check==1){
						putTable((char)(Cell_type.PLAYER2.type-32),x,i);
						hexCells_but[i][x].setBackground(Color.PINK);
					}	
				}
		return check;
	}
	/**Checks For The Winning Path*/
	private int check_win(char player,int x,int y){
		int check=0;
		if (player==Cell_type.PLAYER1.type){
			for (int i = x;  i<x+2; ++i)//checks right and right-up
				if (getTable(i,y) ==player){			
					hexCells[i][y].setState((char)(hexCells[i][y].getState()-32));
					if (y==size-1){
						hexCells_but[y][i].setBackground(Color.PINK);
						return 1;//x win
					}
					check=check_win(player,i-1,y+1);
					if (check==0)
						hexCells[i][y].setState((char)(hexCells[i][y].getState()+32));
					else
						hexCells_but[y][i].setBackground(Color.PINK);
					return check;
				}
			for (int i = y-2; i <y; i++)//checks left-down and down
				if (getTable(x+2,i)==Cell_type.PLAYER1.type){
					hexCells[x+2][i].setState((char)(hexCells[x+2][i].getState()-32));
					check=check_win(player,x+1,i+1);
					if (check==0)
						hexCells[x+2][i].setState((char)(hexCells[x+2][i].getState()+32));
					else
						hexCells_but[i][x+2].setBackground(Color.PINK);
					return check;
				}
			for (int i = y-2 ,j=x+1 ; i < y && j>x-1 ; ++i, --j)//checks left and up
				if (getTable(j,i)==player){
					hexCells[j][i].setState((char)(hexCells[j][i].getState()-32));
					check=check_win(player,j-1,i+1);
					if (check==0)
						hexCells[j][i].setState((char)(hexCells[j][i].getState()+32));
					else
						hexCells_but[i][j].setBackground(Color.PINK);
					return check;
				}
		}else if (player==Cell_type.PLAYER2.type){
			for (int i = y; i<y+2 ; ++i)
				if (getTable(x,i)==player){
					hexCells[x][i].setState((char)(hexCells[x][i].getState()-32));
					if (x==size-1){
						hexCells_but[i][x].setBackground(Color.PINK);
						return 1;//O win
					}
					check=check_win(player,x+1,i-1);
					if (check==0){
						hexCells[x][i].setState((char)(hexCells[x][i].getState()+32));
					}else{
						hexCells_but[i][x].setBackground(Color.PINK);
						return 1;
					}
				}
			for (int i = x-2;  i <x; ++i)
				if (getTable(i,y+2)==player){
					hexCells[i][y+2].setState((char)(hexCells[i][y+2].getState()-32));
					check=check_win(player,i+1,y+1);
					if (check==0){
						hexCells[i][y+2].setState((char)(hexCells[i][y+2].getState()+32));
					}else{
						hexCells_but[y+2][i].setBackground(Color.PINK);
						return 1;
					}
				}
			for (int i = x-2 , j=y+1 ; i < x && j>y-1 ; ++i,--j)
				if (getTable(i,j)==player){
					hexCells[i][j].setState((char)(hexCells[i][j].getState()-32));
					check=check_win(player,i+1,j-1);
					if (check==0){
						hexCells[i][j].setState((char)(hexCells[i][j].getState()+32));
					}else{
						hexCells_but[j][i].setBackground(Color.PINK);
						return 1;
					}
				}
				return check;
		}
		return 0;
	}
	/**Plays Player's Move*/
	@Override
	public void play(Cell cell){
		switch(check_turn())
			{
				case 1:	putTable(Cell_type.PLAYER1.type,cell.getY(),cell.getX()-'a');
						score1_label.setText("Player 1: "+userScore(Cell_type.PLAYER1.type));
						hexCells_but[(int)(cell.getX()-'a')][cell.getY()].setBackground(Color.RED);
						hexCells_but[(int)(cell.getX()-'a')][cell.getY()].setEnabled(false);		
						last_int=cell.getY()+1;
						last_char=cell.getX(); 
						break;
				case 2: putTable(Cell_type.PLAYER2.type,cell.getY(),cell.getX()-'a');
						score2_label.setText("Player 2: "+userScore(Cell_type.PLAYER2.type));
						hexCells_but[(int)(cell.getX()-'a')][cell.getY()].setBackground(Color.BLUE);
						hexCells_but[(int)(cell.getX()-'a')][cell.getY()].setEnabled(false);
						break;
			}
		putLast(cell);
		if (find_initial(Cell_type.PLAYER1.type,0,0)==1 || find_initial(Cell_type.PLAYER2.type,0,0)==1 ){
			if (check_turn()==1){
				make_upper(Cell_type.PLAYER2.type);
			}else
				make_upper(Cell_type.PLAYER1.type);	
			JOptionPane.showMessageDialog(new JFrame(),
				"Player-"+(check_turn()%2 +1)+" Wins","WINNER!!!",JOptionPane.PLAIN_MESSAGE);
		}
	}
	/**Changes The Color of Winner Buttons*/
	private void make_upper(char winner){
		for (int i = 0; i < size; ++i)
			for (int j = 0; j < size; ++j)
				if (getTable(i,j)==winner){
					if (getTable(i,j+1)==winner-32 || getTable(i+1,j)==winner-32 || getTable(i,j-1)==winner-32 ||getTable(i-1,j+1)==winner-32 || getTable(i-1,j)==winner-32 ||getTable(i+1,j-1)==winner-32 ){
						putTable((char)(winner-32),i,j);
						hexCells_but[j][i].setBackground(Color.PINK);
						make_upper(winner);
					}
				}
	}
	/**Plays Computer's Move*/
	@Override
	public Cell play(){
		Cell played;
		played= computers_turn(last_int,last_char);
		hexCells_but[(int)(played.getX()-'a')][played.getY()].setBackground(Color.BLUE);
		hexCells_but[(int)(played.getX()-'a')][played.getY()].setEnabled(false);
		score2_label.setText("Computer: "+userScore(Cell_type.COMPUTER.type));
		putLast(played);
		if (find_initial(Cell_type.COMPUTER.type,0,0)==1 ){
			make_upper(Cell_type.COMPUTER.type);
			JOptionPane.showMessageDialog(new JFrame(),"Computer Wins","WINNER!!!",JOptionPane.PLAIN_MESSAGE);
			int a=JOptionPane.showConfirmDialog(new JFrame(),"One More Game??","PLAY AGAIN",JOptionPane.YES_NO_OPTION);  
				if(a==JOptionPane.YES_OPTION){
					frame.dispose();
					playGame();
					return played;
				}else  
					System.exit(0);
		}
		return played;
	}
	/**Helper For Undo*/
	private void putLast(Cell cell){
		try{
			Cell[] new_moves;
			new_moves = new Cell[marked_cells()];
			for (int i = 0; i < marked_cells()-1; ++i){
				new_moves[i]=new Cell();
				new_moves[i]=moves[i];
			}
			moves = new Cell[marked_cells()];
			for (int i = 0; i < marked_cells()-1; ++i){
				moves[i]=new Cell();
				moves[i]=new_moves[i];
			}
			moves[marked_cells()-1]=new Cell();
			moves[marked_cells()-1]= cell;
		}catch(Exception e){
			JOptionPane.showMessageDialog(new JFrame(),e,"ERROR!!!",JOptionPane.ERROR_MESSAGE);
		}
	}
	/**Returns The Number Of Marked Cell In Game*/
	@Override
	public int marked_cells(){
		int number=0;
		for (int i = 0; i < getSize(); ++i)
			for (int j = 0; j < getSize(); ++j)
				if (getTable(i,j)!= Cell_type.EMPTY.type)
					number++;
		return number;
	}
	/**Helper For Computer's Move*/
	private Cell computers_turn(int y,char x){
		char new_x= (char)(((int)x+(int)(getSize()+'a'))/2);//for smart moves ->average of inputed and size
		char new_x2= (char)((((int)x+(int)'a')/2)+1);//for smart moves -> average of inputed and zero('a' in alphabet) 
		if ((double)getSize()/2>(int)(x-'a')){//left half of the table 
			if (getTable(y-1,(int)(x-'a')-1)==Cell_type.PLAYER1.type)//if the left side is 'x'
			{
				if (check_input(y+1,new_x)==1)	{	
					hexCells[y][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y,new_x);
				}else if (check_input(y-1,new_x)==1){
					hexCells[y-2][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y-2,new_x);
				}else if (check_input(y+1,(char)(new_x-1))==1){
					hexCells[y][(int)(new_x-'a')-1].setState(Cell_type.COMPUTER.type);
					return extractCell(y,(char)(new_x-1));
				}else
					return put_empty(y,x);
			}else{//if the left side is not 'x'
				if (check_input(y,new_x)==1){
					hexCells[y-1][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y-1,new_x);
				}else if (check_input(y+1,new_x)==1){
					hexCells[y][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y,new_x);
				}else
					 return put_empty(y,x);
			}
		}else{//right half of the table
			if (getTable(y-1,(int)(x-'a')+1)==Cell_type.PLAYER1.type)//if the right side is 'x'
			{
				if (check_input(y+1,new_x2)==1){
					hexCells[y][(int)(new_x2-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y,new_x2);
				}else if (check_input(y+1,new_x2)==1)
				{
					hexCells[y-2][(int)(new_x2-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y-2,new_x2);
				}else if (check_input(y+1,(char)(new_x2-1))==1)
				{
					hexCells[y][(int)(new_x2-'a')-1].setState(Cell_type.COMPUTER.type);
					return extractCell(y,(char)(new_x2-1));
				}else
					return put_empty(y,x);
			}else{//if the right side is not 'x'
				if (check_input(y,new_x2)==1)
				{
					hexCells[y-1][(int)(new_x2-'a')].setState(Cell_type.COMPUTER.type);
					return extractCell(y-1,new_x2);
				}else if (check_input(y,(char)(new_x2-1))==1)
				{
					hexCells[y-1][(int)(new_x2-'a')-1].setState(Cell_type.COMPUTER.type);
					return extractCell(y-1,(char)(new_x2-1));
				}else
					 return put_empty(y,x);
			}
		}
	}
	/**Helper For Computer's Move*/
	private Cell put_empty(int y,char x){
		if (check_input(y-1,(char)(x+1)) == 1 ){//if the right-up side is Cell_type.EMPTY.type
			hexCells[y-2][(int)(x-'a')+1].setState(Cell_type.COMPUTER.type);
			 return extractCell(y-2,(char)(x+1));
		}else if (check_input(y,(char)(x+1)) == 1 ){//if the right side is Cell_type.EMPTY.type
			hexCells[y-1][(int)(x-'a')+1].setState(Cell_type.COMPUTER.type);
			return extractCell(y-1,(char)(x+1));
		}else if (check_input(y,(char)(x-1)) == 1 ){//if the left side is Cell_type.EMPTY.type
			hexCells[y-1][(int)(x-'a')-1].setState(Cell_type.COMPUTER.type);
			return extractCell(y-1,(char)(x-1));
		}else if (check_input(y+1,(char)(x-1)) == 1 ){//if the left-down side is Cell_type.EMPTY.type
			hexCells[y][(int)(x-'a')-1].setState(Cell_type.COMPUTER.type);
			return extractCell(y,(char)(x-1));
		}else if (check_input(y+1,x) == 1 ){//if the down side is Cell_type.EMPTY.type
			hexCells[y][(int)(x-'a')].setState(Cell_type.COMPUTER.type);
			return extractCell(y,x);
		}else if (check_input(y-1,x) == 1 ){//if the up side is Cell_type.EMPTY.type
			hexCells[y-2][(int)(x-'a')].setState(Cell_type.COMPUTER.type);
			return extractCell(y-2,x);
		}else{
			int i,j=0;
			for (i = 0; i < getSize()-1; i++){
				for (j = 0; j < getSize()-1; j++)
					if (getTable(i,j)==Cell_type.EMPTY.type)
						break;
				if (getTable(i,j)==Cell_type.EMPTY.type)
					break;
			}
			hexCells[i][j].setState(Cell_type.COMPUTER.type);
			return extractCell(i,(char)(j+'a'));
		}
	}
	/**Helper For Computer's Move*/
	private int check_input(int y,char x){
		if (1<=y && y<=size && 'a'<=x && x<'a'+size)
			if (hexCells[y-1][(int)(x-'a')].getState() == Cell_type.EMPTY.type){
				return 1;
			}else
				return 0;
		return 0;
	}
	/**Calculates And Returns The Score*/
	@Override
	public int userScore(char player){
		double score=0.0,score_perConnect=100.0/(getSize()*getSize());
		for (int i = 0; i < size; ++i)
			for (int j = 0; j < size; ++j)
				if (getTable(i,j)==player || getTable(i,j)==player-32)
					if (player==Cell_type.PLAYER1.type){
						score+=score_perConnect*neighboorCells_x(i,j,player)*(j+1)*(i+1);
					}else
						score+=score_perConnect*neighboorCells_o(i,j,player)*(j+1)*(i+1);				
		return  (int)score;
	}
	/**Helper For Score*/
	private int neighboorCells_x(int i,int j,char player){
		int n_Cells=0;
		if (getTable(i,j+1)==player || getTable(i,j+1)==player-32)
			n_Cells++;
		if (getTable(i,j-1)==player ||getTable(i,j-1)==player-32 )
			n_Cells++;
		if (getTable(i-1,j+1)==player||getTable(i-1,j+1)==player-32)
			n_Cells++;
		if (getTable(i+1,j-1)==player || getTable(i+1,j-1)==player-32)
			n_Cells++;
		return n_Cells +1;
	}
	/**Helper For Score*/
	private int neighboorCells_o(int i,int j,char player){
		int n_Cells=0;
		if (getTable(i+1,j)==player||getTable(i+1,j)==player-32)
			n_Cells++;
		if (getTable(i-1,j+1)==player||getTable(i-1,j+1)==player-32)
			n_Cells++;
		if (getTable(i-1,j)==player||getTable(i-1,j)==player-32)
			n_Cells++;
		if (getTable(i+1,j-1)==player||getTable(i+1,j-1)==player-32)
			n_Cells++;
		return n_Cells +1;
	}
	/**Override clone() Method*/
	@Override
    public Object clone(){
        try {
            Hex clone = (Hex)super.clone(); 
            return clone;
        }
        catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }
}//END