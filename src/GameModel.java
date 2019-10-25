

import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;


public class GameModel extends Observable{
	
	private int[][] board;
	private Scanner scan;
	CheckersAI ai;
	
	public GameModel(){
		board = new int[8][8];
		for(int k=0; k<3; k++){
			int start = 0;
			if(k%2==0) start=1;
			for(int i = start; i<8; i=i+2){
				board[k][i]=2;
			}
		}
		for(int k=5; k<8; k++){
			int start = 0;
			if(k%2==0) start=1;
			for(int i = start; i<8; i=i+2){
				board[k][i]=1;
			}
		}
		ai = new CheckersAI();
		scan = new Scanner(System.in);
	}
	
	public boolean getTurn(int who){
		int firstNumx, firstNumy, secondNumx, secondNumy;
		String curPosition1 = scan.next();
		String curPosition2 = scan.next();
		String nextPosition1 = scan.next();
		String nextPosition2 = scan.next();
		try {
			firstNumx = Integer.parseInt(curPosition1);
			firstNumy = Integer.parseInt(curPosition2);
			secondNumx = Integer.parseInt(nextPosition1);
			secondNumy = Integer.parseInt(nextPosition2);
		}
		catch(NumberFormatException e) {
			return false;
		}
		if(who!=board[firstNumy][firstNumx]){
			if(who==1&&board[firstNumy][firstNumx]==3){
				who=3;
			}
			else if(who==2&&board[firstNumy][firstNumx]==4){
				who=4;
			}
			else return false;
		}
		ArrayList<int[]> moves = this.validMove(firstNumx, firstNumy, secondNumx, secondNumy, who);
		boolean isValid = !moves.isEmpty();
		if(isValid) this.executeMove(firstNumx, firstNumy, moves, who);
		return isValid;
	}
	
	private void executeMove(int firstNumx, int firstNumy, ArrayList<int[]> moves, int who) {
		int curx = firstNumx;
		int cury = firstNumy;
		for(int i=0; i<moves.size(); i++){
			board[cury][curx] = 0;
			int[] nextJump = moves.get(i);
			if(Math.abs(nextJump[1]-cury)==1){
				board[nextJump[1]][nextJump[0]] = who;
				curx=nextJump[0];
				cury=nextJump[1];
			}
			else{
				board[(nextJump[1]+cury)/2][(nextJump[0]+curx)/2] = 0;
				board[nextJump[1]][nextJump[0]] = who;
				curx=nextJump[0];
				cury=nextJump[1];
			}
		}
		this.checkKing(who, curx, cury);
	}

	private void checkKing(int who, int curx, int cury) {
		if(who==1&&cury==0) board[cury][curx]=3;
		if(who==2&&cury==7) board[cury][curx]=4;
	}

	private ArrayList<int[]> validMove(int firstNumx, int firstNumy, int secondNumx, int secondNumy, int who) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		if(firstNumx<0||firstNumx>=8||firstNumy<0||firstNumy>=8||secondNumx<0||firstNumx>=8||secondNumx<0||firstNumx>=8) return moves;
		if(board[secondNumy][secondNumx]!=0) return moves;
		moves = canNoJump(firstNumx, firstNumy, secondNumx, secondNumy, who);
		if(!moves.isEmpty()) return moves;
		moves = canMultiJump(firstNumx, firstNumy, secondNumx, secondNumy, who, new ArrayList<int[]>());
		return moves;
	}
	
	private ArrayList<int[]> canNoJump(int firstNumx, int firstNumy, int secondNumx, int secondNumy, int who){
		ArrayList<int[]> moves = new ArrayList<int[]>();
		switch(who){
			case 1:
				if(firstNumy-1!=secondNumy) return moves;
				if(secondNumx-1==firstNumx||secondNumx+1==firstNumx){
					moves.add(new int[]{secondNumx, secondNumy});
					return moves;
				}
				break;
			case 2:
				if(firstNumy+1!=secondNumy) return moves;
				if(secondNumx-1==firstNumx||secondNumx+1==firstNumx){
					moves.add(new int[]{secondNumx, secondNumy});
					return moves;
				}
				break;
			default:
				if((secondNumx-1==firstNumx||secondNumx+1==firstNumx)&&(secondNumy-1==firstNumy||secondNumy+1==firstNumy)){
					moves.add(new int[]{secondNumx, secondNumy});
					return moves;
				}
				break;
		}
		return moves;
	}
	
	private ArrayList<int[]> canMultiJump(int firstNumx, int firstNumy, int secondNumx, int secondNumy, int who, ArrayList<int[]> moves){
		ArrayList<int[]> jumps = canJump(firstNumx, firstNumy, who);
		for(int[] arr: jumps){
			if(arr[0]==secondNumx&&arr[1]==secondNumy){
				moves.add(arr);
				return moves;
			}
			else{
				moves.add(arr);
				ArrayList<int[]> temp = canMultiJump(arr[0],arr[1],secondNumx,secondNumy,who,moves);
				if(temp.get(temp.size()-1)[0]==secondNumx&&temp.get(temp.size()-1)[1]==secondNumy){
					return moves;
				}
			}
		}
		return moves;
	}
	
	private ArrayList<int[]> canJump(int curx, int cury, int who){
		ArrayList<int[]> moves = new ArrayList<int[]>();
		switch(who){
			case 1:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&board[cury-2][curx-2]==0&&(board[cury-1][curx-1]==2||board[cury-1][curx-1]==4)){
					moves.add(new int[]{curx-2,cury-2});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&board[cury-2][curx+2]==0&&(board[cury-1][curx+1]==2||board[cury-1][curx+1]==4)){
					moves.add(new int[]{curx+2,cury-2});
				}
				break;
			case 2:
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&board[cury+2][curx-2]==0&&(board[cury+1][curx-1]==1||board[cury+1][curx-1]==3)){
					moves.add(new int[]{curx-2,cury+2});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&board[cury+2][curx+2]==0&&(board[cury+1][curx+1]==1||board[cury+1][curx+1]==3)){
					moves.add(new int[]{curx+2,cury+2});
				}
				break;
			case 3:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&board[cury-2][curx-2]==0&&(board[cury-1][curx-1]==2||board[cury-1][curx-1]==4)){
					moves.add(new int[]{curx-2,cury-2});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&board[cury-2][curx+2]==0&&(board[cury-1][curx+1]==2||board[cury-1][curx+1]==4)){
					moves.add(new int[]{curx+2,cury-2});
				}
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&board[cury+2][curx-2]==0&&(board[cury+1][curx-1]==2||board[cury+1][curx-1]==4)){
					moves.add(new int[]{curx-2,cury+2});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&board[cury+2][curx+2]==0&&(board[cury+1][curx+1]==2||board[cury+1][curx+1]==4)){
					moves.add(new int[]{curx+2,cury+2});
				}
				break;
			case 4:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&board[cury-2][curx-2]==0&&(board[cury-1][curx-1]==1||board[cury-1][curx-1]==3)){
					moves.add(new int[]{curx-2,cury-2});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&board[cury-2][curx+2]==0&&(board[cury-1][curx+1]==1||board[cury-1][curx+1]==3)){
					moves.add(new int[]{curx+2,cury-2});
				}
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&board[cury+2][curx-2]==0&&(board[cury+1][curx-1]==1||board[cury+1][curx-1]==3)){
					moves.add(new int[]{curx-2,cury+2});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&board[cury+2][curx+2]==0&&(board[cury+1][curx+1]==1||board[cury+1][curx+1]==3)){
					moves.add(new int[]{curx+2,cury+2});
				}
				break;	
		}
		return moves;
	}

	public void doAIMove(){
		int[][] move = ai.move(board);
		ArrayList<int[]> moves = this.validMove(move[0][0], move[0][1], move[1][0], move[1][1], 2);
		this.executeMove(move[0][0], move[0][1], moves, 2);
	}
	
	public void printBoard(){
		System.out.println("   0 1 2 3 4 5 6 7");
		System.out.println("  -----------------");
		for(int i=0; i<8; i++){
			System.out.print(i+" |");
			for(int k=0; k<8; k++){
				System.out.print(board[i][k]);
				System.out.print("|");
			}
			System.out.println();
		}
		System.out.println("  -----------------");
	}
	
	public static void main(String[] args){
		GameModel checkers = new GameModel();
		checkers.printBoard();
		System.out.println("Use command line to input move in the format <current"
				+ " position x> <current position y> <next postion x> <next position y>");
		while(true){
			while(!checkers.getTurn(1)){
				System.out.println("Invalid move, try again.");
			}
			checkers.printBoard();
			if(checkers.checkp1win()) break;
			checkers.doAIMove();
			System.out.println("AI's turn");
			checkers.printBoard();
			System.out.println("AI moved");
			if(checkers.checkp2win()) break;
		}
		checkers.scan.close();
	}

	private boolean checkp2win() {
		for(int k=0; k<8; k++){
			for(int i=0; i<8; i++){
				if(board[k][i]==1||board[k][i]==3) return false;
			}
		}
		System.out.println("ai Wins!");
		return true;
	}

	private boolean checkp1win() {
		for(int k=0; k<8; k++){
			for(int i=0; i<8; i++){
				if(board[k][i]==2||board[k][i]==4) return false;
			}
		}
		System.out.println("you Win!");
		return true;
	}
}
