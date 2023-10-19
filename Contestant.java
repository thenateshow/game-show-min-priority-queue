public class Contestant{

	private int id;
	private int score;
	private Contestant left;
	private Contestant right;

	public Contestant(int i, int s){
		id = i;
		score = s;
		left = null;
		right = null;
	}

	public void setID(int i){
		id = i;
	}
	public void setScore(int s){
		score = s;
	}
	public int getID(){
		return id;
	}
	public int getScore(){
		return score;
	}
	public void setLeft(Contestant c){
		left = c;
	}
	public void setRight(Contestant c){
		right = c;
	}
	public Contestant getLeft(){
		return left;
	}
	public Contestant getRight(){
		return right;
	}
}