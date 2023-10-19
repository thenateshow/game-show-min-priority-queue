import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;

public class Game{

	Contestant[] con;
	int[] locs;
	File input;
	File output;
	List<String> outputStrings = new ArrayList<String>();

	int nextPos = 0;
	int maxSize = 0;
	int currentSize = 0;

	public Game(File i, File o){
		input = i;
		output = o;
	}

	public void parseFile(){
		try{
			Scanner s = new Scanner(input);
			String line = s.nextLine();
			String[] content;
			setUpArrays(Integer.parseInt(line));
			String action = "";
			while(s.hasNextLine()){
				line = s.nextLine();
				outputStrings.add(line);
				content = line.split("\\s+");
				if(content[0].equals("findContestant")){
					action = findContestant(Integer.parseInt(content[1].substring(1, content[1].length()-1)));
				}
				else if(content[0].equals("insertContestant")){
					action = insertContestant(Integer.parseInt(content[1].substring(1, content[1].length()-1)), Integer.parseInt(content[2].substring(1, content[2].length()-1)));
				}
				else if(content[0].equals("eliminateWeakest")){
					action = eliminateWeakest();
				}
				else if(content[0].equals("earnPoints")){
					action = earnPoints(Integer.parseInt(content[1].substring(1, content[1].length()-1)), Integer.parseInt(content[2].substring(1, content[2].length()-1)));
				}
				else if(content[0].equals("losePoints")){
					action = losePoints(Integer.parseInt(content[1].substring(1, content[1].length()-1)), Integer.parseInt(content[2].substring(1, content[2].length()-1)));
				}
				else if(content[0].equals("showContestants")){
					action = showContestants();
				}
				else if(content[0].equals("showHandles")){
					action = showHandles();
				}
				else if(content[0].equals("showLocation")){
					action = showLocation(Integer.parseInt(content[1].substring(1, content[1].length()-1)));
				}
				else if(content[0].equals("crownWinner")){
					action = crownWinner();
				}
				else{
					System.out.println("Not a valid input");
				}
				outputStrings.add(action);
			}
			outputToFile(outputStrings);
		}
		catch(FileNotFoundException e){
			System.out.println("No file");
		}
	}

	public static void outputToFile(List<String> outputs){
		try{
			FileWriter writer = new FileWriter("output.txt");
			for(String s : outputs){
				writer.write(s + "\n");
			}
			writer.close();
		}
		catch(IOException e){
			System.out.println("error writing to file");
			e.printStackTrace();
		}
	}

	public void setUpArrays(int numC){
		con = new Contestant[numC + 1];
		locs = new int[numC + 1];
		for(int i = 0; i < locs.length; i++){
			locs[i] = -1;
		}
		maxSize = numC + 1;
	}

	public void swap(Contestant a, Contestant b){
		Contestant temp = a;
		con[locs[a.getID()]] = con[locs[b.getID()]];
		con[locs[b.getID()]] = temp;
		int temp2 = locs[a.getID()];
		locs[a.getID()] = locs[b.getID()];
		locs[b.getID()] = temp2;
	}

	public Contestant getParent(Contestant c){
		return con[locs[c.getID()] / 2];
	}

	public Contestant getLeft(Contestant c){
		return con[locs[c.getID()] * 2];
	}

	public Contestant getRight(Contestant c){
		return con[locs[c.getID()] * 2 + 1];
	}

	public Boolean leaf(Contestant c){
		return (locs[c.getID()] > (currentSize / 2) && locs[c.getID()] <= currentSize);
	}

	public void heapify(Contestant c){
		if(!leaf(c) && (c.getScore() > getLeft(c).getScore() || c.getScore() > getRight(c).getScore())){
			if(locs[c.getID()] * 2 + 1 > currentSize || getLeft(c).getScore() < getRight(c).getScore()){
				swap(c, getLeft(c));
				heapify(c);
			}
			else{
				swap(c, getRight(c));
				heapify(c);
			}
		}
	}

	public String findContestant(int i){
		String output = "";
		Contestant c;
		if(locs[i] == -1){
			output = "Contestant <" + i + "> is not in the extended heap.";
		}
		else{
			output = "Contestant <" + i + "> is in the extended heap with score <" + con[locs[i]].getScore() + ">.";
		}

		return output;
	}

	public String insertContestant(int i, int s){
		String output = "";
		Contestant c = new Contestant(i, s);
		if(currentSize >= maxSize){
			output = "Contestant <" + i + "> could not be inserted because the extended heap is full.";
		}
		else if(locs[i] != -1){
			output = "Contestant <" + i + "> is already in the extended heap: cannot insert.";
		}
		else{
			currentSize++;
			con[currentSize] = c;
			locs[i] = currentSize;
			//swap(con[1], con[currentSize]);
			//heapify(con[1]);
			int curr = currentSize;
			while(curr/2 > 0 && con[curr].getScore() < getParent(con[curr]).getScore()){
				swap(con[curr], getParent(con[curr]));
				curr = locs[getParent(con[curr]).getID()];
			}
			output = "Contestant <" + i + "> inserted with initial score <" + s + ">";
		}
		return output;
	}

	public String eliminateWeakest(){
		String output = "";
		if(currentSize == 0 || currentSize == 1){
			output = "No contestant can be eliminated since the extended heap is empty.";
		}
		else{
			Contestant weakest = con[1];
			swap(con[1], con[currentSize]);
			currentSize--;
			heapify(con[1]);
			output = "Contestant <" + weakest.getID() + "> with current lowest score <" + weakest.getScore() + "> eliminated.";
			locs[weakest.getID()] = -1;
		}
		return output;
	}

	public String earnPoints(int i, int p){
		String output = "";
		if(locs[i] == -1){
			output = "Contestant <" + i + "> is not in the extended heap.";
		}
		else{
			con[locs[i]].setScore(con[locs[i]].getScore() + p);
			heapify(con[locs[i]]);
			output = "Contestant <" + i + ">'s score increased by <" + p + "> points to <" + con[locs[i]].getScore() + ">.";
		}
		return output;
	}

	public String losePoints(int i, int p){
		String output = "";
		if(locs[i] == -1){
			output = "Contestant <" + i + "> is not in the extended heap.";
		}
		else{
			con[locs[i]].setScore(con[locs[i]].getScore() - p);
			int curr = currentSize;
			while(curr/2 > 0 && con[curr].getScore() < getParent(con[curr]).getScore()){
				swap(con[curr], getParent(con[curr]));
				curr = locs[getParent(con[curr]).getID()];
			}
			output = "Contestant <" + i + ">'s score decreased by <" + p + "> points to <" + con[locs[i]].getScore() + ">.";
		}

		return output;
	}

	public String showContestants(){
		String output = "";
		for(int i = 1; i <= currentSize; i++){
			output +="Contestant <" + con[i].getID() + "> in extended heap location <" + i + "> with score <" + con[i].getScore() + ">.";
			if(i != currentSize){
				output += "\n";
			}
		}

		return output;
	}

	public String showHandles(){
		String output = "";
		for(int i = 1; i <= maxSize - 1; i++){
			if(locs[i] == -1){
				output += "There is no Contestant <" + i + "> in the extended heap: handle[<" + i + ">] = -1.";
			}
			else{
				output += "Contestant <" + i + "> stored in extended heap location <" + locs[i] + ">.";
			}
			if(i != maxSize - 1){
				output += "\n";
			}
		}

		return output;
	}

	public String showLocation(int i){
		String output = "";
		Contestant c;

		return output;
	}

	public String crownWinner(){
		while(currentSize > 1){
			eliminateWeakest();
		}

		return "Contestant <" + con[1].getID() + "> wins with score <" + con[1].getScore() + ">!";
	}

}