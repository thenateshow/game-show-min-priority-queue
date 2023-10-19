import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class program2{

	public static void main(String[] args){

		File input = new File(args[0]);
		File output = new File(args[0]);

		Game g = new Game(input, output);
		g.parseFile();


	}

}