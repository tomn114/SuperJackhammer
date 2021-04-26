package com.tomn114.entities;

import java.awt.Graphics;
import java.util.ArrayList;

import com.tomn114.gamestate.PlayState;
import com.tomn114.main.Game;
import com.tomn114.util.PresetChunks;

public class TileManager {
	
	public Tile[][] tiles;
	private Tile[][] nextChunk;
	private static int MAX_HEIGHT_RENDERED = 22;
	private static int CHUNK_HEIGHT = 11;
	private static int COLS;

	private float startY;
	
	/*  "Air" tile: not drawn, no collision
	 *  Dirt tile: dirt 
	 *  Obsidian tile
	 *  Silver ore
	 *  Gold ore
	 *  Diamond ore
	 *  Lava
	 */
	
	/*
	 *  0 == DIRT
	 *  1 == SILVER
	 *  2 == GOLD
	 *  3 == DIAMOND
	 *  4 == OBSIDIAN
	 *  5 == AIR
	 *  6 == LAVA
	 *  
	 *  TODO: BUGFIX: Nullpointers / Arrayoutofbounds with fast speeds
	 *  TODO: More obsidian paatches
	 *  	  -Variations in lava
	 *        - Air above lava
	 *        -Smaller lava pit
	 *        -Better chunk generation
	 */
	
	public TileManager() {
		new PresetChunks();
		COLS = Game.WIDTH / Tile.TILE_SIZE;
		tiles = new Tile[MAX_HEIGHT_RENDERED][Game.WIDTH / COLS];
		startY = Game.HEIGHT / 2;
		
		Tile[][] chunk1 = generateChunk(startY);
		Tile[][] chunk2 = generateChunk(startY + Tile.TILE_SIZE * CHUNK_HEIGHT);
		nextChunk = new Tile[CHUNK_HEIGHT][COLS];
		
		for(int i = 0; i < tiles.length; i++) {
			if(i < CHUNK_HEIGHT) {
				tiles[i] = copyTileArray(chunk1[i]);
			}
			else {
				tiles[i] = copyTileArray(chunk2[i - CHUNK_HEIGHT]);
			}
		}
	}
	
	/* X1: left side of jackhammer end
	 * X2: right side of jackhammer end
	 * */
	
	// MAn fuck this language , i gotta use an arraylist
	public ArrayList<Integer> onTile(float x1, float x2, float y) {
		//System.out.println("Modulus test: " + ((y - tiles[0][0].y) % Tile.TILE_SIZE));
		if(y < startY)
			return null;
	
		ArrayList<Integer> onTileArrayList = new ArrayList<Integer>();
		int startRow = getRow(y);	
		
		int startCol = getCol(x1);
		int endCol = getCol(x2);
		//System.out.println("x1: " + x1);
		//System.out.println("x1: " + (x1 / Tile.TILE_SIZE) + " x1 rounded: " + startCol + ", x2: " + (x2 / Tile.TILE_SIZE) + " x2 rounded: " + endCol);
		
		for(int i = startCol; i <= endCol; i++) {
			//System.out.println("startRow: " + startRow + ", i: " + i);
			if(tiles[startRow][i].hasCollision())
				onTileArrayList.add(i);
		}
		//System.out.println("On tile is false!");
		if(onTileArrayList.size() > 0)
			return onTileArrayList;
		return null;
	}
	
	/*
	 * 
	 * TODO: BUGFIX Going right while falling can cause error (band-aid fix so far)
	 *       BUGFIX #2, going into a block while falling (pretty hard to reproduce)
	 * X1: x + vx
	 * X2: x2 + vx
	 * Returns the side tile the player is touching
	 * */
	public int onSideTiles(float x1, float x2, float y) {
		if(y <= startY || getCol(x2) > tiles[0].length - 1) {
			//System.out.println("??");
			return -1;
		}
		int startRow;
		//System.out.println(x1 + ", " + y);
		
		//JANKIEST SHIT EVER MAN FUCK.
		ArrayList<Integer> tilesOn = onTile(x1 + Player.BOTTOM_OFFSET, x1 + Player.BOTTOM_OFFSET + Player.BOTTOM_WIDTH, y);
		if(tilesOn != null) {
			/*
			for(int i = 0; i < tilesOn.size(); i++)
				System.out.println("Tiles on " + i + ": " + tilesOn.get(i));
			System.out.println("x1: " + x1 + ", x2: " + x2 + ", y: " + y + ", x1 offset: " + (x1 + Player.BOTTOM_OFFSET) + ", x1 offset width: " + (x1 + Player.BOTTOM_OFFSET + Player.BOTTOM_WIDTH));
			System.out.println("row - 1 bitch");*/
			
			//Bandaid fix again (for bugfix #2)
			if(getRow(y) != 0)
				startRow = getRow(y) - 1;  // -1 because you want the row that the player is on, not on top of.
			else
				startRow = getRow(y);
		}
		else
			startRow = getRow(y);
		
		//System.out.println("start row: " + startRow);
		
		
		//int startCol = (int) (x1 / Tile.TILE_SIZE);
		//int endCol = (int) (x2 / Tile.TILE_SIZE);
		
		//System.out.println("startRow: " + startRow + ", startCol: " + startCol + ", endCol: " + endCol);
		
		//System.out.println(tiles[startRow][getCol(x1)].hasCollision());
		
		if(tiles[startRow][getCol(x1)].hasCollision())
			return getCol(x1);
		else if(tiles[startRow][getCol(x2)].hasCollision()) {
			return getCol(x2);
		}
		
		return -1;
		
	}
	
	public int getRow(float y) {
		if((int)((y - tiles[0][0].y) / Tile.TILE_SIZE) == 10)
			System.out.println("Row calculation: y: " + y + " - tiles[0][0].y: " + tiles[0][0].y + " / TILE_SIZE = " +  ((y - tiles[0][0].y) / Tile.TILE_SIZE));
		
		return (int)((y - tiles[0][0].y) / Tile.TILE_SIZE);
		
		//return (int) ((y - startY) / Tile.TILE_SIZE);
	}
	
	public int getCol(float x) {
		return (int) (x / Tile.TILE_SIZE);
	}
	
	
	//Gets the Top Y coordinate of the given row Ex: Row 1 = startY + 40 = 240
	//Gets top y coordinate of first tile of array
	public float getRowTopYCoordinate(int row) {
		//System.out.println("row: " + row + ", da coordinate: " + (startY + row * Tile.TILE_SIZE));
		return tiles[row][0].y;
	}
	
	public float getBottomChunkYCoordinate() {
		return tiles[tiles.length - 1][0].y + Tile.TILE_SIZE;
	}
	
	public float getColLeftXCoordinate(int col) {
		return col * Tile.TILE_SIZE;
	}
	
	public float getColRightXCoordinate(int col) {
		return col * Tile.TILE_SIZE + Tile.TILE_SIZE;
	}
	
	//Gets the cols of tiles to demolish (Old method)
	/*
	public void demolish(int row, ArrayList<Integer> colsToDemolish, float power) {
		for(int i = 0; i < colsToDemolish.size(); i++) {
			
			//System.out.println(tiles[row][colsToDemolish.get(i)].getDurability());
			if(tiles[row][colsToDemolish.get(i)].getDurability() > 0)
				tiles[row][colsToDemolish.get(i)].breakTile(power);
			else
				tiles[row][colsToDemolish.get(i)] = new Air(getColLeftXCoordinate(colsToDemolish.get(i)), getRowTopYCoordinate(row));
		}
	}*/
	
	//returns moneymade
	public int demolish(ArrayList<int[]> tilesToDemolish, float power) {
		int moneyMade = 0;
		for(int i = 0; i < tilesToDemolish.size(); i++) {
			int[] tileXY = tilesToDemolish.get(i);
			int tileY = tileXY[0];
			int tileX = tileXY[1];
			
			//System.out.println("TileX: " + tileX + ", TileY: " + tileY);
			
			//System.out.println(tiles[row][colsToDemolish.get(i)].getDurability());
			
			//TILE IS X, Y BTW!!!!!!!!!!!
			if(tiles[tileY][tileX].getDurability() > 0) {
				//System.out.println("Breaking tile: TileY,X: " + tileY + ", " + tileX);
				tiles[tileY][tileX].breakTile(power);
			}
			if(tiles[tileY][tileX].getDurability() <= 0) {
				moneyMade += tiles[tileY][tileX].getValue();
				//System.out.println(tiles[tileY][tileX].getValue());
				//System.out.println(moneyMade);
				//System.out.println("Making air block: TileY: " + tileY + " (" + getRowTopYCoordinate(tileY) + ") " + "TileX: " + tileX + " (" +  getColLeftXCoordinate(tileX) + ")");
				tiles[tileY][tileX] = new Air(getColLeftXCoordinate(tileX), getRowTopYCoordinate(tileY));
			}
		}
		return moneyMade;
	}
	
	
	public float getStartY() { return startY; }
	
	
	
	public void update() {
		int rowsToShift = rowsOffScreen();
		
		if(isNextChunkEmpty()) {
			//System.out.println("chunk empty, filling");
			fillNextChunk();
		}
		
		if(rowsToShift > 0) {
			//System.out.println("Shifting " + rowsToShift + " rows");
			shiftRows(rowsToShift);
		}
	}
	
	public int rowsOffScreen() {
		//Todo: fix this algorithm.
		
		int row = 0;
		
		if(tiles[row][0].y + Tile.TILE_SIZE > PlayState.cameraY1)
			return 0;
		
		boolean notOffScreen = false;
		while(!notOffScreen) {
			/*
			if(row == 0) {
				System.out.println((tiles[row][0].y + Tile.TILE_SIZE) + ", cameraY1: " + PlayState.cameraY1);
			}*/
			
			if(tiles[row][0].y + Tile.TILE_SIZE < PlayState.cameraY1) {
				//System.out.println("row offscreen: " + row);
			}
			else break; // break Cuz FUCCK YOU
			row++;
		}
		return row;
	}
	
	public void shiftRows(int numRows) {
		//System.out.println("Huh???");
		for(int i = 0; i < numRows; i++) {
			
			for(int row = 0; row < tiles.length; row++) {
				if(row == tiles.length - 1) {
					//System.out.println("New row being created with y: " + (getRowTopYCoordinate(row - 1) + Tile.TILE_SIZE));
					
					tiles[row] = copyTileArray(nextChunk[0]);
					/*
					for(int col = 0; col < tiles[i].length; col++) {
						
						tiles[row][col] = new Dirt(getColLeftXCoordinate(col), getRowTopYCoordinate(row - 1) + Tile.TILE_SIZE);
						
						
						
						//System.out.println("row: " + row + ", ycoordinate: " + (getRowTopYCoordinate(row - 1) + Tile.TILE_SIZE));
					}*/
					shiftNextChunkRows();
				}
				else {
					//System.out.println("Row transfer: row " + (row+1) + " -> row " + row + "  " + tiles[row + 1][0].y + " being put into " + tiles[row][0].y);
					tiles[row] = copyTileArray(tiles[row + 1]);
					
				}
			}
		}		
	}
	
	public Tile[] copyTileArray(Tile[] arr) {
		Tile[] copy = new Tile[arr.length];
		
		for(int i = 0; i < arr.length; i++) {
			copy[i] = arr[i];
		}
		
		return copy;
	}
	
	public Tile[][] generateChunk(float chunkStartY) {
		
		float x;
		float y = chunkStartY;
		
		Tile[][] chunk = new Tile[CHUNK_HEIGHT][COLS];
		//int randomChunk = (int)(Math.random() * 4);
		//System.out.println(randomChunk);
		int randomChunk = (int)(Math.random() * 1000);
		
		int[][] chunkIndices = convertRandomToChunk(randomChunk);
		for(int i = 0; i < CHUNK_HEIGHT; i++) {
			x = 0;
			for(int j = 0; j < COLS; j++) {
				
				if(randomChunk > 300) {
					int random = (int)(Math.random() * 1000);
				
					chunk[i][j] = createTileWithKey(convertRandomToKey(random), x, y);
				}
				else {
					for(int row = 0; row < CHUNK_HEIGHT; row++) {
						for(int col = 0; col < COLS; col++) {
							
							chunk[i][j] = createTileWithKey(chunkIndices[i][j], x, y);
						}
					}
				}
			
				
				x += Tile.TILE_SIZE;
			}
			y += Tile.TILE_SIZE;
		}
		
		return chunk;
	}
	
	public boolean isNextChunkEmpty() {
		//System.out.println("checking");
		return nextChunk[0][0] == null;
	}
	
	public void fillNextChunk() {
		//System.out.println(getBottomChunkYCoordinate());
		Tile[][] generatedChunk = generateChunk(getBottomChunkYCoordinate());
		
		for(int i = 0; i < generatedChunk.length; i++) {
			nextChunk[i] = copyTileArray(generatedChunk[i]);
		}
	}
	
	public void shiftNextChunkRows() {
		//System.out.println("Shift");
		for(int row = 0; row < nextChunk.length; row++) {
			if(row == nextChunk.length - 1) {
				nextChunk[row] = new Tile[COLS];
			}
			else {
				nextChunk[row] = copyTileArray(nextChunk[row + 1]);
			}
			/*
			for(int col = 0; col < COLS; col++) {
				if(nextChunk[row][col] == null)
					System.out.print("null ");
				else
					System.out.print(nextChunk[row][col] + " ");
			}
			System.out.println();*/
			
		}
		if(isNextChunkEmpty())
			fillNextChunk();
	}
	
	public Tile createTileWithKey(int key, float x, float y) {
		if(key == 0)
			return new Dirt(x, y);
		else if(key == 1)
			return new Silver(x, y);
		else if(key == 2)
			return new Gold(x, y);
		else if(key == 3)
			return new Diamond(x, y);
		else if(key == 4)
			return new Obsidian(x, y);
		else if(key == 5)
			return new Air(x, y);
		else if(key == 6)
			return new Lava(x, y);
		return new Dirt(x, y); //returns dirt as default
	}
	
	public int convertRandomToKey(int randomNumber) {
		int silverThreshold = Silver.RARITY;
		int goldThreshold = silverThreshold + Gold.RARITY;
		int diamondThreshold = goldThreshold + Diamond.RARITY;
		int obsidianThreshold = diamondThreshold + Obsidian.RARITY;
		int airThreshold = obsidianThreshold + Air.RARITY;
		int lavaThreshold = airThreshold + Lava.RARITY;
		
		if(randomNumber < silverThreshold)
			return 1;
		else if(randomNumber < goldThreshold)
			return 2;
		else if(randomNumber < diamondThreshold)
			return 3;
		else if(randomNumber < obsidianThreshold)
			return 4;
		else if(randomNumber < airThreshold)
			return 5;
		else if(randomNumber < lavaThreshold)
			return 6;	
		return 0;
	}
	
	public int[][] convertRandomToChunk(int randomNumber) {
		int dirtThreshold = 30;
		int obCenterThreshold = dirtThreshold + 30;
		int obLeftThreshold = obCenterThreshold + 30;
		int obRightThreshold = obLeftThreshold + 30;
		int silverThreshold = obRightThreshold + 40;
		int goldThreshold = silverThreshold + 20;
		int diamondThreshold = goldThreshold + 10;
		int oreThreshold = diamondThreshold + 10;
		int aLeftThreshold = oreThreshold + 20;
		int aRightThreshold = aLeftThreshold + 20;
		int lavaPitCThreshold = aRightThreshold + 20;
		int lavaPitLThreshold = lavaPitCThreshold + 20;
		int lavaPitRThreshold = lavaPitLThreshold + 20;
		
		if(randomNumber < dirtThreshold)
			return PresetChunks.DIRT_PATCH;
		else if(randomNumber < obLeftThreshold)
			return PresetChunks.OBSIDIAN_PATCH_LEFT;
		else if(randomNumber < obRightThreshold)
			return PresetChunks.OBSIDIAN_PATCH_RIGHT;
		else if(randomNumber < silverThreshold)
			return PresetChunks.SILVER_PATCH;
		else if(randomNumber < goldThreshold)
			return PresetChunks.GOLD_PATCH;
		else if(randomNumber < diamondThreshold)
			return PresetChunks.DIAMOND_PATCH;
		else if(randomNumber < oreThreshold)
			return PresetChunks.ORE_PATCH;
		else if(randomNumber < aLeftThreshold)
			return PresetChunks.RANDOM_AIR_LEFT;
		else if(randomNumber < aRightThreshold)
			return PresetChunks.RANDOM_AIR_RIGHT;
		else if(randomNumber < lavaPitCThreshold)
			return PresetChunks.LAVA_PIT_CENTER;
		else if(randomNumber < lavaPitLThreshold)
			return PresetChunks.LAVA_PIT_LEFT;
		else if(randomNumber < lavaPitRThreshold)
			return PresetChunks.LAVA_PIT_RIGHT;
		return PresetChunks.DIRT_PATCH;
		
	}
	
	public boolean isTileLethal(int row, int col) {
		//System.out.println("row: " + row + ", col: " + col);
		return tiles[row][col].isLethal();
	}
	public boolean isTileSlowDown(int row, int col) {
		return tiles[row][col].hasSlowDown();
	}
	
	
	public void render(Graphics g) {
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[i].length; j++) {
				try {
					tiles[i][j].render(g);
				}
				catch(NullPointerException e) {
					for(int row = 0; row < tiles.length; row++) {
						for(int col = 0; col < COLS; col++) {
							if(tiles[row][col] == null)
								System.out.print("null ");
							else
								System.out.print(tiles[row][col] + " ");
						}
						System.out.println();
					}
					System.out.println("Next Chunk: ");
					
					for(int row = 0; row < nextChunk.length; row++) {
						for(int col = 0; col < COLS; col++) {
							if(nextChunk[row][col] == null)
								System.out.print("null ");
							else
								System.out.print(nextChunk[row][col] + " ");
						}
						System.out.println();
					}
				}
			}
			//g.setColor(Color.BLACK);
			
			//Y in this.tiles[row][0] == null at fast speeds
			//g.drawString(Integer.toString(i), 20, (int) ((int)getRowTopYCoordinate(i) + 20 - PlayState.cameraY1));
		}
	}

}
