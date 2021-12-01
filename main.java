import javax.swing.*;
import java.awt.*;
class main{
	public static void main(String[] args) {
		Hex game1=new Hex();
		game1.playGame();
		Hex game2=new Hex();
		game2=(Hex)game1.clone();
		System.out.println("Cloned Game2 Size: "+game2.getSize());
	}
}