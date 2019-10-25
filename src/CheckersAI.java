

import java.util.ArrayList;

public class CheckersAI {
	
	private int[][] board;

	public int evaluation(int[][] board){
		int eval = 0;
		for(int i=0; i<8; i++){
			for(int k=0; k<8; k++){
				if(board[i][k]==2) eval++;
				if(board[i][k]==1) eval--;
				if(board[i][k]==4) eval=eval+2;
				if(board[i][k]==3) eval=eval-2;
			}
		}
		 return eval;
	}
	
	public int[][] move(int[][] board){
		this.board = board;
		ArrayList<int[][]> moves = getAllActions(2);
		int resultValue = Integer.MIN_VALUE;
		int[][] result = null;
		for(int[][] m: moves){
			int[][] newBoard = cloneBoard(board);
			int value = minValue(execAction(m,2,newBoard), 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
			if (value > resultValue) {
                result = m;
                resultValue = value;
            }
		}
		return result;
	}
	
	public int maxValue(int[][] state, int depth, int alpha, int beta){
		if(depth>=8) return evaluation(state);
		ArrayList<int[][]> moves = getAllActions(2);
		int value = Integer.MIN_VALUE;
		for(int[][] m: moves){
			int[][] newBoard = cloneBoard(state);
            value = Math.max(value, minValue(execAction(m,2,newBoard), depth+1, alpha, beta));
            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
		return value;
	}
	
	public int minValue(int[][] state, int depth, int alpha, int beta){
		if(depth>=8) return evaluation(state);
		ArrayList<int[][]> moves = getAllActions(1);
		int value = Integer.MAX_VALUE;
		for (int[][] m: moves) {
			int[][] newBoard = cloneBoard(state);
            value = Math.min(value, maxValue(execAction(m,1,newBoard), depth+1, alpha, beta));
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
		return value;
	}
	
	public int[][] execAction(int[][] action, int who, int[][] newBoard){
		ArrayList<int[]> moves = validMove(action[0][0],action[0][1],action[1][0],action[1][1], who, newBoard);
		return this.executeMove(action[0][0], action[0][1], moves, who, newBoard);
	}
	
	
	public ArrayList<int[][]> getAllActions(int who){
		ArrayList<int[][]> resultingMoves = new ArrayList<int[][]>();
		if(who==1){
			for(int i=0; i<8; i++){
				for(int k=0; k<8; k++){
					if(board[i][k]==1||board[i][k]==3) resultingMoves.addAll(tryAll(i, k));
				}
			}
		}
		if(who==2){
			for(int i=0; i<8; i++){
				for(int k=0; k<8; k++){
					if(board[i][k]==2||board[i][k]==4) resultingMoves.addAll(tryAll(i, k));
				}
			}
		}
		return resultingMoves;
	}
	
	private ArrayList<int[][]> tryAll(int y, int x){
		ArrayList<int[][]> resultingMoves = new ArrayList<int[][]>();
		resultingMoves.addAll(canNoJumps(x, y));
		resultingMoves.addAll(canMultiJumps(x, y));
		return resultingMoves;
	}
	
	private ArrayList<int[][]> canNoJumps(int x, int y){
		ArrayList<int[][]> resultingMoves = new ArrayList<int[][]>();
		int who = board[y][x];
		if(who==1||who==3||who==4){
			if(y-1>=0&&x-1>=0&&board[y-1][x-1]==0){
				resultingMoves.add(new int[][]{new int[]{x,y},new int[]{x-1,y-1}});
			}
			if(y-1>=0&&x+1<8&&board[y-1][x+1]==0){
				resultingMoves.add(new int[][]{new int[]{x,y},new int[]{x+1,y-1}});
			}
		}
		if(who==2||who==3||who==4){
			if(y+1<8&&x-1>=0&&board[y+1][x-1]==0){
				resultingMoves.add(new int[][]{new int[]{x,y},new int[]{x-1,y+1}});
			}
			if(y+1<8&&x+1<8&&board[y+1][x+1]==0){
				resultingMoves.add(new int[][]{new int[]{x,y},new int[]{x+1,y+1}});
			}
		}
		return resultingMoves;
	}
	
	private ArrayList<int[][]> canMultiJumps(int x, int y){
		ArrayList<int[][]> resultingMoves = new ArrayList<int[][]>();
		resultingMoves.addAll(canJumps(x, y, board[y][x]));
		return resultingMoves;
	}
	
	private ArrayList<int[][]> canJumps(int curx, int cury, int who){
		ArrayList<int[][]> moves = new ArrayList<int[][]>();
		switch(who){
			case 1:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&board[cury-2][curx-2]==0&&(board[cury-1][curx-1]==2||board[cury-1][curx-1]==4)){
					for(int[][] i: canJumps(curx-2, cury-2, who)){
						moves.add(new int[][]{new int[]{curx,cury},i[1]});
					}
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx-2,cury-2}});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&board[cury-2][curx+2]==0&&(board[cury-1][curx+1]==2||board[cury-1][curx+1]==4)){
					for(int[][] i: canJumps(curx+2, cury-2, who)){
						moves.add(new int[][]{new int[]{curx,cury},i[1]});
					}
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx+2,cury-2}});
				}
				break;
			case 2:
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&board[cury+2][curx-2]==0&&(board[cury+1][curx-1]==1||board[cury+1][curx-1]==3)){
					for(int[][] i: canJumps(curx-2, cury+2, who)){
						moves.add(new int[][]{new int[]{curx,cury},i[1]});
					}
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx-2,cury+2}});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&board[cury+2][curx+2]==0&&(board[cury+1][curx+1]==1||board[cury+1][curx+1]==3)){
					for(int[][] i: canJumps(curx+2, cury+2, who)){
						moves.add(new int[][]{new int[]{curx,cury},i[1]});
					}
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx+2,cury+2}});
				}
				break;
			case 3:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&board[cury-2][curx-2]==0&&(board[cury-1][curx-1]==2||board[cury-1][curx-1]==4)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx-2,cury-2}});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&board[cury-2][curx+2]==0&&(board[cury-1][curx+1]==2||board[cury-1][curx+1]==4)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx+2,cury-2}});
				}
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&board[cury+2][curx-2]==0&&(board[cury+1][curx-1]==2||board[cury+1][curx-1]==4)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx-2,cury+2}});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&board[cury+2][curx+2]==0&&(board[cury+1][curx+1]==2||board[cury+1][curx+1]==4)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx+2,cury+2}});
				}
				break;
			case 4:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&board[cury-2][curx-2]==0&&(board[cury-1][curx-1]==1||board[cury-1][curx-1]==3)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx-2,cury-2}});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&board[cury-2][curx+2]==0&&(board[cury-1][curx+1]==1||board[cury-1][curx+1]==3)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx+2,cury-2}});
				}
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&board[cury+2][curx-2]==0&&(board[cury+1][curx-1]==1||board[cury+1][curx-1]==3)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx-2,cury+2}});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&board[cury+2][curx+2]==0&&(board[cury+1][curx+1]==1||board[cury+1][curx+1]==3)){
					moves.add(new int[][]{new int[]{curx, cury},new int[]{curx+2,cury+2}});
				}
				break;	
		}
		return moves;
	}
	
	private int[][] cloneBoard(int[][] oldBoard){
		int[][] newBoard = new int[8][8];
		for(int i=0; i<8; i++){
			for(int k=0; k<8; k++){
				newBoard[i][k] = oldBoard[i][k];
			}
		}
		return newBoard;
	}
	
	private ArrayList<int[]> validMove(int firstNumx, int firstNumy, int secondNumx, int secondNumy, int who, int[][] newBoard) {
		ArrayList<int[]> moves = new ArrayList<int[]>();
		if(firstNumx<0||firstNumx>=8||firstNumy<0||firstNumy>=8||secondNumx<0||firstNumx>=8||secondNumx<0||firstNumx>=8) return moves;
		if(board[secondNumy][secondNumx]!=0) return moves;
		moves = canNoJump(firstNumx, firstNumy, secondNumx, secondNumy, who);
		if(!moves.isEmpty()) return moves;
		moves = canMultiJump(firstNumx, firstNumy, secondNumx, secondNumy, who, new ArrayList<int[]>(), newBoard);
		return moves;
	}
	
	private int[][] executeMove(int firstNumx, int firstNumy, ArrayList<int[]> moves, int who, int[][] newBoard) {
		int curx = firstNumx;
		int cury = firstNumy;
		for(int i=0; i<moves.size(); i++){
			newBoard[cury][curx] = 0;
			int[] nextJump = moves.get(i);
			if(Math.abs(nextJump[1]-cury)==1){
				newBoard[nextJump[1]][nextJump[0]] = who;
				curx=nextJump[0];
				cury=nextJump[1];
			}
			else{
				newBoard[(nextJump[1]+cury)/2][(nextJump[0]+curx)/2] = 0;
				newBoard[nextJump[1]][nextJump[0]] = who;
				curx=nextJump[0];
				cury=nextJump[1];
			}
		}
		return this.checkKing(who, curx, cury, newBoard);
	}

	private int[][] checkKing(int who, int curx, int cury, int[][] newBoard) {
		if(who==1&&cury==0) newBoard[cury][curx]=3;
		if(who==2&&cury==7) newBoard[cury][curx]=4;
		return newBoard;
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
	
	private ArrayList<int[]> canMultiJump(int firstNumx, int firstNumy, int secondNumx, int secondNumy, int who, ArrayList<int[]> moves, int[][] newBoard){
		ArrayList<int[]> jumps = canJump(firstNumx, firstNumy, who, newBoard);
		for(int[] arr: jumps){
			if(arr[0]==secondNumx&&arr[1]==secondNumy){
				moves.add(arr);
				return moves;
			}
			else{
				moves.add(arr);
				ArrayList<int[]> temp = canMultiJump(arr[0],arr[1],secondNumx,secondNumy,who,moves,newBoard);
				if(temp.get(temp.size()-1)[0]==secondNumx&&temp.get(temp.size()-1)[1]==secondNumy){
					return moves;
				}
			}
		}
		return moves;
	}
	
	private ArrayList<int[]> canJump(int curx, int cury, int who, int[][] newBoard){
		ArrayList<int[]> moves = new ArrayList<int[]>();
		switch(who){
			case 1:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&newBoard[cury-2][curx-2]==0&&(newBoard[cury-1][curx-1]==2||newBoard[cury-1][curx-1]==4)){
					moves.add(new int[]{curx-2,cury-2});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&newBoard[cury-2][curx+2]==0&&(newBoard[cury-1][curx+1]==2||newBoard[cury-1][curx+1]==4)){
					moves.add(new int[]{curx+2,cury-2});
				}
				break;
			case 2:
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&newBoard[cury+2][curx-2]==0&&(newBoard[cury+1][curx-1]==1||newBoard[cury+1][curx-1]==3)){
					moves.add(new int[]{curx-2,cury+2});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&newBoard[cury+2][curx+2]==0&&(newBoard[cury+1][curx+1]==1||newBoard[cury+1][curx+1]==3)){
					moves.add(new int[]{curx+2,cury+2});
				}
				break;
			case 3:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&newBoard[cury-2][curx-2]==0&&(newBoard[cury-1][curx-1]==2||newBoard[cury-1][curx-1]==4)){
					moves.add(new int[]{curx-2,cury-2});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&newBoard[cury-2][curx+2]==0&&(newBoard[cury-1][curx+1]==2||newBoard[cury-1][curx+1]==4)){
					moves.add(new int[]{curx+2,cury-2});
				}
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&newBoard[cury+2][curx-2]==0&&(newBoard[cury+1][curx-1]==2||newBoard[cury+1][curx-1]==4)){
					moves.add(new int[]{curx-2,cury+2});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&newBoard[cury+2][curx+2]==0&&(newBoard[cury+1][curx+1]==2||newBoard[cury+1][curx+1]==4)){
					moves.add(new int[]{curx+2,cury+2});
				}
				break;
			case 4:
				if(curx-2>=0&&cury-2>=0&&curx-1>=0&&cury-1>=0&&newBoard[cury-2][curx-2]==0&&(newBoard[cury-1][curx-1]==1||newBoard[cury-1][curx-1]==3)){
					moves.add(new int[]{curx-2,cury-2});
				}
				if(curx+2<8&&cury-2>=0&&curx+1<8&&cury-1>=0&&newBoard[cury-2][curx+2]==0&&(newBoard[cury-1][curx+1]==1||newBoard[cury-1][curx+1]==3)){
					moves.add(new int[]{curx+2,cury-2});
				}
				if(curx-2>=0&&cury+2<8&&curx-1>=0&&cury+1<8&&newBoard[cury+2][curx-2]==0&&(newBoard[cury+1][curx-1]==1||newBoard[cury+1][curx-1]==3)){
					moves.add(new int[]{curx-2,cury+2});
				}
				if(curx+2<8&&cury+2<8&&curx+1<8&&cury+2<8&&newBoard[cury+2][curx+2]==0&&(newBoard[cury+1][curx+1]==1||newBoard[cury+1][curx+1]==3)){
					moves.add(new int[]{curx+2,cury+2});
				}
				break;	
		}
		return moves;
	}
}
