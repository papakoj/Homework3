package com.gamewerks.blocky.engine;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;
import java.util.Random;

public class BlockyGame {
	private static final int LOCK_DELAY_LIMIT = 30;

	private Board board;
	private Piece activePiece;
	private Direction movement;

	private int lockCounter;

	private int shufflecount;

	public BlockyGame() {
		board = new Board();
		movement = Direction.NONE;
		lockCounter = 0;
		trySpawnBlock();
	}

	private void fyshuffle(PieceKind[] arr) {
		Random rand = new Random();
		for (int i = arr.length - 1; i > 0; i--) {
			int j = rand.nextInt(i);
			PieceKind swap = arr[i];
			arr[i] = arr[j];
			arr[j] = swap;
		}
	}

	private void trySpawnBlock() {
		if (activePiece == null) {

			//activePiece = new Piece(PieceKind.I, new Position(Constants.BOARD_HEIGHT - 1, Constants.BOARD_WIDTH / 2 - 2));
			PieceKind[] wtf = {PieceKind.I, PieceKind.J, PieceKind.L, PieceKind.O, PieceKind.S, PieceKind.T, PieceKind.Z};
			activePiece = new Piece(wtf[shufflecount], new Position(3, Constants.BOARD_WIDTH / 2 - 2));
			shufflecount++;
			//shufflecount = shufflecount > 6 ? 0 : shufflecount;
			if (shufflecount == 0 || (shufflecount > 6)) {
				fyshuffle(wtf);
				shufflecount = 0;
			}
			if (board.collides(activePiece)) {
				System.exit(0);
			}
		}
	}

	private void processMovement() {
		Position nextPos;
		switch(movement) {
		case NONE:
			nextPos = activePiece.getPosition();
			break;
		case LEFT:
			nextPos = activePiece.getPosition().add(0, -1);
			break;
		case RIGHT:
			nextPos = activePiece.getPosition().add(0, 1);
			break;
		default:
			throw new IllegalStateException("Unrecognized direction: " + movement.name());
		}
		if (!board.collides(activePiece.getLayout(), nextPos)) {
			activePiece.moveTo(nextPos);
		}
	}

	private void processGravity() {
		//Position nextPos = activePiece.getPosition().add(-1, 0);
		Position nextPos = activePiece.getPosition().add(1, 0);
		if (!board.collides(activePiece.getLayout(), nextPos)) {
			lockCounter = 0;
			activePiece.moveTo(nextPos);
		} else {
			if (lockCounter < LOCK_DELAY_LIMIT) {
				lockCounter += 1;
			} else {
				board.addToWell(activePiece);
				lockCounter = 0;
				activePiece = null;
			}
		}
	}

	private void processClearedLines() {
		board.deleteRows(board.getCompletedRows());
	}

	public void step() {
		trySpawnBlock();
		processMovement();
		processGravity();
		processClearedLines();
	}

	public boolean[][] getWell() {
		return board.getWell();
	}

	public Piece getActivePiece() { return activePiece; }
	public void setDirection(Direction movement) { this.movement = movement; }
	public void rotatePiece(boolean dir) { activePiece.rotate(dir); }
}
