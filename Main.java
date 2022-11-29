import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class Main {
	public static void main(String[] args) {
		/*
		resources folder: 
		screens.ass: contains the screen layout as digits
		palettes.perScreen: contains the palettes used on each of the 27 or something idk lol screens
		TODO:
		√ parse each file
		√ build an internal image of the entire stage
		√ categorize each 16x16 block into palettes, then save each unique metatile into an array
		√ run through the image again, then save each unique metametatile into a metametatile array
		√ Save the metametatile array in terms of metatiles, the metatile array in terms of tiles
		√ Run through the image one last time to format the data in terms of metametatile IDs, then export all into an output folder

		OK TIME TO FIX PALETTES!

		- Only touch the metametatile section
		loop through every pixel in the metametatile and get the set of all palettes
		if the set is equal to an existing metatile with an existing equivalent palette then set that metatile to be that thing
		then write the lookups
		*/
//			[[	COLOR PALETTE ARRAY  ]]
		final ArrayList<Color> ColourPalettes = new ArrayList<>(Arrays.asList(
			new Color(Integer.valueOf("707070", 16)), 
			new Color(Integer.valueOf("1e1f85", 16)), 
			new Color(Integer.valueOf("0016a4", 16)), 
			new Color(Integer.valueOf("3f1294", 16)), 
			new Color(Integer.valueOf("87086e", 16)), 
			new Color(Integer.valueOf("a70015", 16)), 
			new Color(Integer.valueOf("9f0007", 16)), 
			new Color(Integer.valueOf("770604", 16)), 
			new Color(Integer.valueOf("402704", 16)), 
			new Color(Integer.valueOf("033f07", 16)), 
			new Color(Integer.valueOf("064f0c", 16)), 
			new Color(Integer.valueOf("023813", 16)), 
			new Color(Integer.valueOf("183957", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("b8b8b8", 16)), 
			new Color(Integer.valueOf("0074e4", 16)), 
			new Color(Integer.valueOf("1b42e3", 16)), 
			new Color(Integer.valueOf("7e23eb", 16)), 
			new Color(Integer.valueOf("b616b4", 16)), 
			new Color(Integer.valueOf("de0058", 16)), 
			new Color(Integer.valueOf("d72510", 16)), 
			new Color(Integer.valueOf("c74617", 16)), 
			new Color(Integer.valueOf("886f17", 16)), 
			new Color(Integer.valueOf("118f1e", 16)), 
			new Color(Integer.valueOf("16a724", 16)), 
			new Color(Integer.valueOf("108f3f", 16)), 
			new Color(Integer.valueOf("0a8087", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("f8f8f8", 16)), 
			new Color(Integer.valueOf("3bbaf5", 16)), 
			new Color(Integer.valueOf("5893f4", 16)), 
			new Color(Integer.valueOf("9f8bf4", 16)), 
			new Color(Integer.valueOf("ee7cf4", 16)), 
			new Color(Integer.valueOf("f771ae", 16)), 
			new Color(Integer.valueOf("f76f63", 16)), 
			new Color(Integer.valueOf("f79644", 16)), 
			new Color(Integer.valueOf("f0b648", 16)), 
			new Color(Integer.valueOf("f0b648", 16)), 
			new Color(Integer.valueOf("82ce34", 16)), 
			new Color(Integer.valueOf("4fd756", 16)), 
			new Color(Integer.valueOf("5ff79e", 16)), 
			new Color(Integer.valueOf("1ee8d8", 16)), 
			new Color(Integer.valueOf("707070", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("f8f8f8", 16)), 
			new Color(Integer.valueOf("a9e1f7", 16)), 
			new Color(Integer.valueOf("c0d1f6", 16)), 
			new Color(Integer.valueOf("d0c9f6", 16)), 
			new Color(Integer.valueOf("f7c1f6", 16)), 
			new Color(Integer.valueOf("f7c0d7", 16)), 
			new Color(Integer.valueOf("f7b8b1", 16)), 
			new Color(Integer.valueOf("f8d7ab", 16)), 
			new Color(Integer.valueOf("f8dfa4", 16)), 
			new Color(Integer.valueOf("e1f7a6", 16)), 
			new Color(Integer.valueOf("b2f7ca", 16)), 
			new Color(Integer.valueOf("9af8f0", 16)), 
			new Color(Integer.valueOf("b8b8b8", 16)), 
			new Color(Integer.valueOf("000000", 16)), 
			new Color(Integer.valueOf("000000", 16))
			));
		final ArrayList<Color> GrayScaleColourPalette = new ArrayList<>(Arrays.asList(
			new Color(Integer.valueOf("000000", 16)),
			new Color(Integer.valueOf("666666", 16)),
			new Color(Integer.valueOf("AAAAAA", 16)),
			new Color(Integer.valueOf("FFFFFF", 16))
			));
//			[[  INPUT FILES  ]]
		final String palettesFilePath = "./Tornado Man/palettes.txt";
		final String imageFileName = "./Tornado Man/TORNADO_MAN_FINALLY_DONE.png";
		final String screenReorderFileName = "./Tornado Man/screenReorder.txt";
//			[[  OUTPUT FILES  ]]
		final String outputCHRName = "tiles.chr";
		final String outputTileScreenName = "tiles.asm";
		final String outputMetatileScreenName = "metatiles.asm";
		final String outputMetatileLookupsName = "metatileLookups.asm";
		final String outputMetametatileScreenName = "metametatiles.asm";
		final String outputMetametatileLookupsName = "metametatileLookups.asm";
//			[[  DEBUG FILES  ]]
		final String paletteImageFileName = "Palettes.png";
		final String extrapolatedPaletteFileName = "paletteExtrapolated.png";
		final String missingColorsFileName = "missingColors.txt";
		final String metatilesListAsImage = "metatiles.png";
		final String tileDebugFile = "tilesAsPalettes.txt";
//			[[  CONSTANTS  ]]
		final int screenWidthPX = 256;
		final int screenHeightPX = 224;
		final int numTilesPerScreen = screenWidthPX*screenHeightPX/64;
		final int numMetatilesPerScreen = numTilesPerScreen/4;
		final int numMetametatilesPerScreen = numMetatilesPerScreen/4;
//			[[  INPUT LISTS  ]]
		int numScreensPerRow = 0;
		ArrayList<ArrayList<Color>> palettesPerScreen = new ArrayList<>();
//			[[  OUTPUT PALETTE LISTS  ]]
		ArrayList<boolean[]> usedColors = new ArrayList<>();
		ArrayList<ArrayList<Color>> usedColorsInAllScreens = new ArrayList<>();
//			[[  PIXEL LISTS  ]]
		int[][] pixelsRaw = new int[palettesPerScreen.size()][screenWidthPX * screenHeightPX];
		Color[][] pixels = new Color[palettesPerScreen.size()][screenWidthPX * screenHeightPX];
//			[[	OUTPUT TILE LISTS  ]]
		ArrayList<int[]> TileList = new ArrayList<>();
		ArrayList<byte[]> TileListHI = new ArrayList<>();
		ArrayList<byte[]> TileListLO = new ArrayList<>();
		ArrayList<Integer> PaletteList = new ArrayList<>();
		ArrayList<Integer> ScreenReordering = new ArrayList<>();
		ArrayList<int[]> ScreensAsTiles = new ArrayList<>();
//			[[  METATILE LISTS]]
		ArrayList<int[]> MetatileList = new ArrayList<>();
		ArrayList<int[]> ScreensAsMetatiles = new ArrayList<>();
		ArrayList<Integer> PalettesPerMetatile = new ArrayList<>();
//			[[  METAMETATILE LISTS]]
		ArrayList<int[]> ScreensAsMetametatiles = new ArrayList<>();
		ArrayList<ArrayList<int[]>> ListOfMetametatileLists = new ArrayList<>();
		ArrayList<Integer> SwitchList = new ArrayList<>();
//			[[	DEBUG LISTS  ]]
		ArrayList<List<Color>> missingColorList = new ArrayList<>();
		ArrayList<int[]> missingInfoList = new ArrayList<>();
		ArrayList<Color> metatileDebugPixelList = new ArrayList<>();

		//read the level file
		BufferedImage level = null;
		try {
			level = ImageIO.read(new File(imageFileName));
			System.out.println(level.getWidth() + " " + level.getHeight());
		} catch (IOException e) {e.printStackTrace();}
		boolean wrongWidth =false;
		boolean wrongHeight = false;
		if(level.getWidth() % 256 != 0) {
			System.out.println("THE SCREEN HAS NOT BEEN CROPPED CORRECTLY: WIDTH IS INCORRECT. THERE ARE " + level.getWidth()%256 + " EXTRA PIXELS IN WIDTH.");
			wrongWidth = true;
		}
		if(level.getHeight() % 224 != 0) {
			System.out.println("THE SCREEN HAS NOT BEEN CROPPED CORRECTLY: HEIGHT IS INCORRECT. THERE ARE " + level.getHeight()%224 + " EXTRA PIXELS IN HEIGHT.");
			wrongHeight = true;
		}
		if(wrongWidth || wrongHeight) {
			return;
		}

		//read the inputs
		try {
			String line;
			numScreensPerRow = level.getWidth()/screenWidthPX;
			//screen order works, now start reading the palettes
			File palettesEachScreen = new File(palettesFilePath);
			FileReader palettesFileFR = new FileReader(palettesEachScreen);
			BufferedReader palettesBR = new BufferedReader(palettesFileFR);
			while((line = palettesBR.readLine()) != null) {
				ArrayList<Color> screenArrayList = new ArrayList<>();
				for(int i = 0; i < line.length(); i+=7) {
					screenArrayList.add(new Color(
						Integer.valueOf(line.substring(i,i+2), 16), 
						Integer.valueOf(line.substring(i+2,i+4), 16), 
						Integer.valueOf(line.substring(i+4,i+6), 16)
					));
				}
				palettesPerScreen.add(screenArrayList);
			}
			palettesBR.close();
			//palettesPerScreen works, now start reading the file that will tell us how to reorder the screens

			File screenReorder = new File(screenReorderFileName);
			FileReader screenReorderFR = new FileReader(screenReorder);
			BufferedReader screenReorderBR = new BufferedReader(screenReorderFR);
			while((line = screenReorderBR.readLine()) != null) {
				for(String str : line.split(" ")) {
					ScreenReordering.add(Integer.parseInt(str));
				}
			}
			screenReorderBR.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		//output a visualization of palettes to ensure everything is in order as intended
		File paletteImageFile = new File(paletteImageFileName);
		BufferedImage paletteImage = new BufferedImage(16, palettesPerScreen.size(), BufferedImage.TYPE_INT_ARGB);
		for(int r = 0; r < 16 * palettesPerScreen.size(); r++) {
			paletteImage.setRGB(r%16, r/16, palettesPerScreen.get(r/16).get(r%16).getRGB());
		}
		try {
			ImageIO.write(paletteImage, "PNG", paletteImageFile);
		} catch(IOException e) {}

		
		pixelsRaw = new int[palettesPerScreen.size()][screenWidthPX * screenHeightPX];
		pixels = new Color[palettesPerScreen.size()][screenWidthPX * screenHeightPX];
		boolean done = false;
		int indexToPixelsRaw = 0;
		try {
			for(int i = 0; i < (level.getWidth() * level.getHeight() / (screenWidthPX * screenHeightPX)); i++) {
				//Only start if the top left tile is opaque
				if(new Color(level.getRGB((i%numScreensPerRow)*screenWidthPX, (i/numScreensPerRow)*screenHeightPX), true).getAlpha() > 0) {
					int[] currentScreen = new int[screenWidthPX * screenHeightPX * numScreensPerRow];
					int[] correctedScreen = new int[screenWidthPX * screenHeightPX];
					int x = (i%numScreensPerRow)*screenWidthPX;
					int y = (i/numScreensPerRow)*screenHeightPX;
					PixelGrabber pixelGrabber = new PixelGrabber(level, x, y, screenWidthPX, screenHeightPX, currentScreen, 0, level.getWidth());
					//pixelgrabber likes to take every row. Since there are 12 screens per row we need to have an array that is 12x256 px long.
					try {
						done = pixelGrabber.grabPixels();
					} catch (ArrayIndexOutOfBoundsException ex) {}
					do {
						System.out.println("screen number: " + i);
					} while (!done);
					//IF THIS IS INFINITE LOOPING - CHECK THE BOUNDS OF THE IMAGE FIRST!!!!
					//we want to go row by row and take the lines from the non-blank rows. 
					for(int p = 0; p < screenHeightPX; p++) {
						for(int j = 0; j < screenWidthPX; j++) {
							correctedScreen[(p*screenWidthPX)+j] = currentScreen[(p*screenWidthPX*numScreensPerRow)+j];
						//	if(currentScreen[(p*screenWidthPX*numScreensPerRow)+j] == 0) {
						//		System.out.println((p*screenWidthPX*numScreensPerRow)+j);
						//	}
						}
					}
					pixelsRaw[indexToPixelsRaw] = correctedScreen;
					indexToPixelsRaw++;
					//OUTPUT A TEST IMAGE DISPLAYING WHAT THE COMPUTER SEES OF THE SCREEN
					/*
					if(i == 7) {
						BufferedImage testImage = new BufferedImage(screenWidthPX, screenHeightPX, BufferedImage.TYPE_INT_ARGB);
						for (int r = 0; r < correctedScreen.length; r++) {
							testImage.setRGB(r%screenWidthPX, r/screenWidthPX, correctedScreen[r]);
						}
						File f = new File("output.png");
						try {
							ImageIO.write(testImage, "PNG", f);
						} catch(IOException e) {

						}
					}
					*/
				}
			}
		} catch(InterruptedException e) {}
		for(int m = 0; m < pixelsRaw.length; m++) {
			for(int i = 0; i < pixelsRaw[m].length; i++) {
				int screenReorderIndex = ScreenReordering.get(m);
				int alpha = (pixelsRaw[screenReorderIndex][i] >> 24) & 0xff;
	    		int red   = (pixelsRaw[screenReorderIndex][i] >> 16) & 0xff;
		    	int green = (pixelsRaw[screenReorderIndex][i] >>  8) & 0xff;
	      		int blue  = (pixelsRaw[screenReorderIndex][i]      ) & 0xff;
				pixels[m][i] = new Color(red, green, blue, alpha);
			}
		}

		int maxWidth = 0;
		for(int screen = 0; screen < pixels.length; screen++) {
			usedColorsInAllScreens.add(new ArrayList<>());
			for(int pixel = 0; pixel < pixels[screen].length; pixel++) {
				if(!usedColorsInAllScreens.get(screen).contains(pixels[screen][pixel])) {
					usedColorsInAllScreens.get(screen).add(pixels[screen][pixel]);
				}
			}
			if(usedColorsInAllScreens.get(screen).size() > maxWidth) {
				maxWidth = usedColorsInAllScreens.size();
			}
		}
		File paletteVisFile = new File(extrapolatedPaletteFileName);
		BufferedImage paletteVis = new BufferedImage(maxWidth, usedColorsInAllScreens.size(), BufferedImage.TYPE_INT_ARGB);
		for(int r = 0; r < usedColorsInAllScreens.size(); r++) {
			for(int imgy = 0; imgy < usedColorsInAllScreens.get(r).size(); imgy++) {
				paletteVis.setRGB(imgy, r, usedColorsInAllScreens.get(r).get(imgy).getRGB());
			}
		}
		try{ImageIO.write(paletteVis, "PNG", paletteVisFile);} catch(IOException e) {}

/*
		for(int f_sc = 0; f_sc < pixels.length; f_sc++) {
			File screenFile = new File("RMV_ME_SCREEN_" + f_sc + ".png");
			BufferedImage screenImage = new BufferedImage(screenWidthPX, screenHeightPX, BufferedImage.TYPE_INT_ARGB);
			for(int f_px = 0; f_px < pixels[f_sc].length; f_px++) {
				screenImage.setRGB(f_px%screenWidthPX, f_px/screenWidthPX, pixels[f_sc][f_px].getRGB());
			}
			try{ImageIO.write(screenImage, "PNG", screenFile);}catch(IOException e){}
		}
*/

//			[[  PIXELS > TILES AREA  ]]
		//The pixels have been prepareth!!!!!!! It is time to find every tile!
		for(int screen = 0; screen < palettesPerScreen.size(); screen++) {
			//split the screen first into sections of different colours, then figure out the tiles from there - not the other way around!!
			int[] currentScreenAsTiles = new int[numTilesPerScreen];
			boolean[] usedColorArray = new boolean[]{false, false, false, false};
			int[] currentScreenAsMetatileSizedPaletteBlocks = new int[numTilesPerScreen/4];
			ArrayList<Color> currentPalettes = palettesPerScreen.get(screen);
			for(int paletteBlock = 0; paletteBlock < numTilesPerScreen/4; paletteBlock++) {
				int x = paletteBlock % (screenWidthPX/16);
				int y = paletteBlock / (screenWidthPX/16);
				int ind = 0;
				Color[] currentBlock = new Color[256];
				//we have the X and Y of the blocks and we will loop through them. Get current metatile as pixels
				for (int row = (y*16); row < (y*16)+16; row++) {
					for(int col = x*16; col < (x*16)+16; col++) {
						currentBlock[ind] = pixels[screen][(row*screenWidthPX)+col];
						ind++;
					}
				}
				//we now have currentBlock, use this to get a list of the colours
				Color[] usedPalette = new Color[4];
				List<Color> coloursInCurrentBlock = Arrays.asList(currentBlock).stream().distinct().collect(Collectors.toList());
				List<Color> currentSet = null;
				int currentlyUsedPalette = 4;
				for(int palette = 0; palette < 4; palette++) {
					currentSet = currentPalettes.subList((4*palette), (4*palette)+4);
					boolean foundANonMatch = false;
					for(int pixelColor = 0; pixelColor < coloursInCurrentBlock.size(); pixelColor++) {
						if(!currentSet.contains(coloursInCurrentBlock.get(pixelColor))) {
							foundANonMatch = true;
						}
					}
					if(!foundANonMatch) {
						currentlyUsedPalette = palette;
						currentPalettes.subList((4*palette), (4*palette)+4).toArray(usedPalette);
						usedColorArray[palette] = true;
						break;
					}
				}
				if(currentlyUsedPalette == 4) {
					boolean exists = false;
					for(int m = 0; m < missingColorList.size(); m++) {
						if(missingColorList.get(m).equals(coloursInCurrentBlock)) {
							exists = true;
						}
					}
					if(!exists) {
						missingColorList.add(coloursInCurrentBlock);
						missingInfoList.add(new int[]{paletteBlock, x, y, screen});
					}
				}
				currentScreenAsMetatileSizedPaletteBlocks[paletteBlock] = currentlyUsedPalette;
			}
			for(int tile = 0; tile < numTilesPerScreen; tile++) {
				//loop through all tiles and add them to the list of things
				int x = tile % (screenWidthPX/8);
				int y = tile/(screenWidthPX/8);
				Color[] currentTile = new Color[64];
				int[] tileTranslatedAsPalettes = new int[64];
				int index = 0;
				for(int row = (y*8); row < (y*8)+8; row++) {
					for(int column = x*8; column < (x*8)+8; column++) {
						currentTile[index] = pixels[screen][(row*screenWidthPX)+column];
						index++;
					}
				}
				int currentlyUsedPalette = currentScreenAsMetatileSizedPaletteBlocks[(x/2)+((y/2)*screenWidthPX/16)];
				if(currentlyUsedPalette == 4) {
						currentlyUsedPalette = 3;
				}
				Color[] usedPalette = new Color[4];
				currentPalettes.subList((4*currentlyUsedPalette), (4*currentlyUsedPalette)+4).toArray(usedPalette);
				for(int pixel = 0; pixel < currentTile.length; pixel++) {
					tileTranslatedAsPalettes[pixel] = new ArrayList<>(Arrays.asList(usedPalette)).indexOf(currentTile[pixel]);
				}
				/*
				if(!printedOnce) {
					System.out.println("Error on Screen: " + screen + " tile at X: " + x + " Y: " + y);
					System.out.println(currentSet);
					printedOnce = true;
				}
				*/
				//we have the currently used palette
				//we have to translate the tile into palette form
				boolean tileAlreadyExists = false;
				for(int loopedTile = 0; loopedTile < TileList.size(); loopedTile++) {
					if(Arrays.equals(TileList.get(loopedTile), tileTranslatedAsPalettes)) {
						tileAlreadyExists = true;
					}
				}
				if(!tileAlreadyExists) {
					TileList.add(tileTranslatedAsPalettes);
					PaletteList.add(currentlyUsedPalette);
					//debugListForTileColours.add(usedPalette.toList());
				}
				currentScreenAsTiles[tile] = -1;
				for(int b = 0; b < TileList.size(); b++) {
					if(Arrays.equals(TileList.get(b),tileTranslatedAsPalettes)) {
						currentScreenAsTiles[tile] = b;
					}
				}
			}
			usedColors.add(usedColorArray);
			ScreensAsTiles.add(currentScreenAsTiles);
		}

//			[[  METATILES AREA  ]]		
		//Tiles and Palettes are dealt with! Now to do metatiles and metametatiles!
		for(int screen = 0; screen < ScreensAsTiles.size(); screen++) {
			int[] currentScreenAsMetatile = new int[numMetatilesPerScreen];
			ArrayList<Color> currentPalettes = palettesPerScreen.get(screen);
			for(int metatile = 0; metatile < numMetatilesPerScreen; metatile++) {
				int[] currentMetatile = new int[]{
					ScreensAsTiles.get(screen)[( (metatile/16)*64 + ((metatile%16) *2 ))], 
					ScreensAsTiles.get(screen)[( (metatile/16)*64 + ((metatile%16) *2 ) + 1)],
					ScreensAsTiles.get(screen)[( (metatile/16)*64 + ((metatile%16) *2 ) + 32)],
					ScreensAsTiles.get(screen)[( (metatile/16)*64 + ((metatile%16) *2 ) + 33)]
				};
				//this works
				int x = metatile % (screenWidthPX/16);
				int y = metatile/(screenWidthPX/16);
				int currentPaletteOfMetatile = 0;
				ArrayList<Color> currentPalettesInMetatile = new ArrayList<>();
				for(int row = (y*16); row < (y*16)+16; row++) {
					for(int column = x*16; column < (x*16)+16; column++) {
						boolean contained = false;
						Color currentPixel = pixels[screen][(row*screenWidthPX)+column];
						//loop through every pixel in the current metatile
						for(int i = 0; i < currentPalettesInMetatile.size(); i++) {
							if(currentPalettesInMetatile.get(i).equals(currentPixel)) {
								contained = true;
								break;
							}
						}
						if(!contained) {
							currentPalettesInMetatile.add(currentPixel);
						}
					}
				}
				if(currentPalettesInMetatile.size() > 4) {
					System.out.println("PalettesInMetatileProblem: " + screen + " " + x + " " + y);
				}
				for(int palette = 0; palette < currentPalettes.size()/4; palette++) {
					List<Color> currentSet = currentPalettes.subList((4*palette), (4*palette)+4);
					boolean foundANonMatch = false;
					for(int pixelColor = 0; pixelColor < currentPalettesInMetatile.size(); pixelColor++) {
						if(!currentSet.contains(currentPalettesInMetatile.get(pixelColor))) {
							foundANonMatch = true;
						}
					}
					if(!foundANonMatch) {
						currentPaletteOfMetatile = palette;
						break;
					}
				}

				currentScreenAsMetatile[metatile] = -1;
				for(int m = 0; m < MetatileList.size(); m++) {
					if(Arrays.equals(MetatileList.get(m), currentMetatile) && currentPaletteOfMetatile == PalettesPerMetatile.get(m)) {
						currentScreenAsMetatile[metatile] = m;
						break;
					}
				}
				if(currentScreenAsMetatile[metatile] == -1) {
					MetatileList.add(currentMetatile);
					PalettesPerMetatile.add(currentPaletteOfMetatile);
					currentScreenAsMetatile[metatile] = MetatileList.size() -1;
				}
			}
			ScreensAsMetatiles.add(currentScreenAsMetatile);
		}

		
		//METATILES ARE DONE!!!! FINAL STRETCH!!!!!
		ListOfMetametatileLists.add(new ArrayList<>());
		for(int screen = 0; screen < ScreensAsMetatiles.size(); screen++) {
			int indexIntoListOfMetametatileLists = 0;
			int[] currentScreenAsMetametatile = new int[numMetametatilesPerScreen];
			for(int metametatile = 0; metametatile < numMetametatilesPerScreen; metametatile++) {
				ArrayList<int[]> MetametatileList = ListOfMetametatileLists.get(indexIntoListOfMetametatileLists);
				int[] currentMetametatile = new int[]{
					ScreensAsMetatiles.get(screen)[( (metametatile/8)*32 + ((metametatile%8) *2 ))], 
					ScreensAsMetatiles.get(screen)[( (metametatile/8)*32 + ((metametatile%8) *2 ) + 1)],
					ScreensAsMetatiles.get(screen)[( (metametatile/8)*32 + ((metametatile%8) *2 ) + 16)],
					ScreensAsMetatiles.get(screen)[( (metametatile/8)*32 + ((metametatile%8) *2 ) + 17)]
				};
				//this is copied from the top
				currentScreenAsMetametatile[metametatile] = -1;
				for(int m = 0; m < MetametatileList.size(); m++) {
					if(Arrays.equals(MetametatileList.get(m), currentMetametatile)) {
						currentScreenAsMetametatile[metametatile] = m;
						break;
					}
				}
				if(currentScreenAsMetametatile[metametatile] == -1) {
					MetametatileList.add(currentMetametatile);
					currentScreenAsMetametatile[metametatile] = MetametatileList.size() -1;
				}
				if(MetametatileList.size() == 257) {
					//THIS WILL TRIGGER IF WE HAVE ONE TOO MANY TILES IN THE LIST
					MetametatileList.remove(256);
					ListOfMetametatileLists.set(indexIntoListOfMetametatileLists, MetametatileList);
					if(indexIntoListOfMetametatileLists+1 == ListOfMetametatileLists.size()) {
						ListOfMetametatileLists.add(new ArrayList<>());
					}
					//SwitchList.add(screen);
					metametatile = -1;
					indexIntoListOfMetametatileLists++;
				}
			}
			SwitchList.add(indexIntoListOfMetametatileLists);
			ScreensAsMetametatiles.add(currentScreenAsMetametatile);
		}
		//ListOfMetametatileLists.add(MetametatileList);




//			[[  FILE WRITING SECTION  ]]
		File missingFile = new File(missingColorsFileName);
		try {
			FileWriter mfw = new FileWriter(missingFile);
			BufferedWriter mbw = new BufferedWriter(mfw);
			mbw.write("missing colors:\n");
			for(int y = 0; y < missingColorList.size(); y++) {
				mbw.write("At X: " + missingInfoList.get(y)[1] + " Y: " + missingInfoList.get(y)[2] + " tile #:" + missingInfoList.get(y)[0] + " screen: " + (missingInfoList.get(y)[3]+1) + " cols: ");
				for(Color col : missingColorList.get(y)) {
					String b=Integer.toHexString(col.getRGB());
					if(b.length() == 6) {
						b = "xx" + b;
					} else if(b.length() == 4) {
						b = "xxxx" + b;
					} else if (b.length() == 1) {
						b = "xxxxxxx" + b;
					}
					if(b.length() < 8) {
						System.out.print(b);
					}
					mbw.write("#" + b.substring(2, 8).toUpperCase() + " ");
				}
				mbw.write("\n");
			}
			mbw.flush();
			mbw.write("unused colors: \n");

			for(int x = 0; x < usedColors.size(); x++) {
				mbw.write((x+1) + ": ");
				for(int b = 0; b < usedColors.get(x).length; b++) {
					if(!usedColors.get(x)[b]) {
						mbw.write("#" + Integer.toHexString(palettesPerScreen.get(x).get(b*4).getRGB()).substring(2, 8).toUpperCase() + " ");
						mbw.write("#" + Integer.toHexString(palettesPerScreen.get(x).get(b*4+1).getRGB()).substring(2, 8).toUpperCase() + " ");
						mbw.write("#" + Integer.toHexString(palettesPerScreen.get(x).get(b*4+2).getRGB()).substring(2, 8).toUpperCase() + " ");
						mbw.write("#" + Integer.toHexString(palettesPerScreen.get(x).get(b*4+3).getRGB()).substring(2, 8).toUpperCase() + " ");
					}
				}
				mbw.write("\n");
			}
			mbw.flush();
			mbw.close();
		} catch (IOException ex) {}
		//TileList is now complete!
		for(int tile = 0; tile < TileList.size(); tile++) {
			String[] hiBits = new String[]{"", "", "", "", "", "", "", ""};
			String[] loBits = new String[]{"", "", "", "", "", "", "", ""};
			byte[] finalHi = new byte[8];
			byte[] finalLo = new byte[8];
			for(int px = 0; px < TileList.get(tile).length; px++) {
				hiBits[px/8] += TileList.get(tile)[px]/2;
				loBits[px/8] += TileList.get(tile)[px]%2;
			}
			for(int f_byte = 0; f_byte < finalHi.length; f_byte++) {
				try {
					Integer.parseInt(hiBits[f_byte], 2);	
					Integer.parseInt(loBits[f_byte], 2);	
				} catch (Exception e) {
					break;
				}
				finalHi[f_byte] = (byte) Integer.parseInt(hiBits[f_byte], 2);
				finalLo[f_byte] = (byte) Integer.parseInt(loBits[f_byte], 2);
			}
			TileListHI.add(finalHi);
			TileListLO.add(finalLo);
		}
		//Hi Lo

		File outputCHR = new File(outputCHRName);
		try (FileOutputStream stream = new FileOutputStream(outputCHR)){
			for(int index = 0; index < TileListHI.size(); index++) {
				stream.write(TileListLO.get(index));
				stream.write(TileListHI.get(index));
			}
		} catch(IOException ex) {}

		File out = new File(outputTileScreenName);
		try {
			FileWriter fw = new FileWriter(out);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("ScreensAsTiles: \n");
			for(int x = 0; x < ScreensAsTiles.size(); x++) {
				for(int t = 0; t < ScreensAsTiles.get(x).length; t++) {
					if(t%32 == 0) {
						bw.write("HEX  ");
					}
					bw.write(String.format("%02X ", ScreensAsTiles.get(x)[t]));
					if((t+1)%32 == 0) {
						bw.write("\n");
					}
				}
				bw.write("\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException ex) {}

		File metatileOutFile = new File(outputMetatileScreenName);
		try {
			FileWriter mtfw = new FileWriter(metatileOutFile);
			BufferedWriter mtbw = new BufferedWriter(mtfw);
			mtbw.write("ScreensAsMetatiles: \n");
			for (int r = 0; r < ScreensAsMetatiles.size(); r++) {
				mtbw.write("\nscreen_" + r + ": \n");
				for(int mt = 0; mt < ScreensAsMetatiles.get(r).length; mt++) {
					if(mt%16 == 0) {
						mtbw.write("HEX  ");
					}
					mtbw.write(String.format("%02X ", ScreensAsMetatiles.get(r)[mt]));
					if((mt+1)%16 == 0) {
						mtbw.write("\n");
					}
				}
				mtbw.write("\n");
			}
			mtbw.flush();
			mtbw.close();
		} catch(IOException ex){}

		File metatileLookupsFile = new File(outputMetatileLookupsName);
		try {
			FileWriter mlfw = new FileWriter(metatileLookupsFile);
			BufferedWriter mlbw = new BufferedWriter(mlfw);
			mlbw.write("Lookups_TL: \n");
			for(int t = 0; t < MetatileList.size(); t++) {
				if((t%16) == 0) {
					mlbw.write("HEX  ");
				}
				mlbw.write(String.format("%02X ", MetatileList.get(t)[0]));
				if((t+1)%16 == 0) {
					mlbw.write("\n");
				}
			}
			mlbw.flush();
			mlbw.write("\n\nALIGN 256\nLookups_TR: \n");
			for(int t = 0; t < MetatileList.size(); t++) {
				if((t%16) == 0) {
					mlbw.write("HEX  ");
				}
				mlbw.write(String.format("%02X ", MetatileList.get(t)[1]));
				if((t+1)%16 == 0) {
					mlbw.write("\n");
				}
			}
			mlbw.flush();
			mlbw.write("\n\nALIGN 256\nLookups_BL: \n");
			for(int t = 0; t < MetatileList.size(); t++) {
				if((t%16) == 0) {
					mlbw.write("HEX  ");
				}
				mlbw.write(String.format("%02X ", MetatileList.get(t)[2]));
				if((t+1)%16 == 0) {
					mlbw.write("\n");
				}
			}
			mlbw.flush();
			mlbw.write("\n\nALIGN 256\nLookups_BR: \n");
			for(int t = 0; t < MetatileList.size(); t++) {
				if((t%16) == 0) {
					mlbw.write("HEX  ");
				}
				mlbw.write(String.format("%02X ", MetatileList.get(t)[3]));
				if((t+1)%16 == 0) {
					mlbw.write("\n");
				}
			}
			mlbw.flush();
			mlbw.write("\n\nALIGN 256\nattributes:\n;G = boss gate\n;B = block appears in front of mega man\n;S = block is spike\n;L = block is ladder\n;C = block collision\n;P = palette bits\n;    ?GBSLCPP\n");
			for(int t = 0; t < PalettesPerMetatile.size(); t++) {
				mlbw.write("DB  %000000" + String.format("%2s", Integer.toBinaryString(PalettesPerMetatile.get(t))).replace(' ', '0') + "\n");

			}
			mlbw.flush();
			mlbw.close();
		}catch(IOException ex) {}

		File metametatileOutFile = new File(outputMetametatileScreenName);
		try {
			FileWriter mttfw = new FileWriter(metametatileOutFile);
			BufferedWriter mttbw = new BufferedWriter(mttfw);
			mttbw.write("ScreensAsMetametatiles: \n");
			int w = 0;
			int lastScreenBankIndex = 0;
			for (int r = 0; r < ScreensAsMetametatiles.size(); r++) {
				if(SwitchList.get(r) != lastScreenBankIndex) {
					mttbw.write("\n;    SWITCH HERE TO BANK " + SwitchList.get(r));
					lastScreenBankIndex = SwitchList.get(r);
				}
				mttbw.write("\nscreen_" + r + ": \n");
				for(int mt = 0; mt < ScreensAsMetametatiles.get(r).length; mt++) {
					if(mt%8 == 0) {
						mttbw.write("HEX  ");
					}
					mttbw.write(String.format("%02X ", ScreensAsMetametatiles.get(r)[mt]));
					if((mt+1)%8 == 0) {
						mttbw.write("\n");
					}
				}
				mttbw.write("HEX  00 00 00 00 00 00 00 00\n");
				mttbw.write("\n");
			}
			mttbw.flush();
			mttbw.close();
		} catch(IOException ex){}

		File metametatileLookupsFile = new File(outputMetametatileLookupsName);
		try {
			FileWriter mlfw = new FileWriter(metametatileLookupsFile);
			BufferedWriter mlbw = new BufferedWriter(mlfw);
			int listNum = 0;
			for(ArrayList<int[]> mmt : ListOfMetametatileLists) {
				mlbw.write("METAMETATILE_LOOKUPS_" + listNum + ": \n\nLookups_TL_" + listNum + ": \n");
				for(int t = 0; t < mmt.size(); t++) {
					if((t%16) == 0) {
						mlbw.write("HEX  ");
					}
					mlbw.write(String.format("%02X ", mmt.get(t)[0]));
					if((t+1)%16 == 0) {
						mlbw.write("\n");
					}
				}
				mlbw.flush();
				mlbw.write("\n\nALIGN 256\nLookups_TR_" + listNum + ": \n");
				for(int t = 0; t < mmt.size(); t++) {
					if((t%16) == 0) {
						mlbw.write("HEX  ");
					}
					mlbw.write(String.format("%02X ", mmt.get(t)[1]));
					if((t+1)%16 == 0) {
						mlbw.write("\n");
					}
				}
				mlbw.flush();
				mlbw.write("\n\nALIGN 256\nLookups_BL_" + listNum + ": \n");
				for(int t = 0; t < mmt.size(); t++) {
					if((t%16) == 0) {
						mlbw.write("HEX  ");
					}
					mlbw.write(String.format("%02X ", mmt.get(t)[2]));
					if((t+1)%16 == 0) {
						mlbw.write("\n");
					}
				}
				mlbw.flush();
				mlbw.write("\n\nALIGN 256\nLookups_BR_" + listNum + ": \n");
				for(int t = 0; t < mmt.size(); t++) {
					if((t%16) == 0) {
						mlbw.write("HEX  ");
					}
					mlbw.write(String.format("%02X ", mmt.get(t)[3]));
					if((t+1)%16 == 0) {
						mlbw.write("\n");
					}
				}
				mlbw.write("\n\nALIGN 256\nPalettes_" + listNum + ": \n");
				for(int t = 0; t < mmt.size(); t++) {
					if((t%4) == 0) {
						mlbw.write("DB	");
					}
					mlbw.write("%" + 
						String.format("%2s", Integer.toBinaryString(PalettesPerMetatile.get(mmt.get(t)[3]))).replace(' ', '0') + 
						String.format("%2s", Integer.toBinaryString(PalettesPerMetatile.get(mmt.get(t)[2]))).replace(' ', '0') + 
						String.format("%2s", Integer.toBinaryString(PalettesPerMetatile.get(mmt.get(t)[1]))).replace(' ', '0') + 
						String.format("%2s", Integer.toBinaryString(PalettesPerMetatile.get(mmt.get(t)[0]))).replace(' ', '0')
						);
					if((t+1)%4 == 0) {
						mlbw.write("\n");
					} else {
						mlbw.write(", ");
					}
				}
				mlbw.flush();
				mlbw.write("\n\n\n");
				listNum++;
			}
			mlbw.close();
		}catch(IOException ex) {}

		//We now need to display an image of all the metatiles listed 
		File metatileDebugFileForCollisions = new File(metatilesListAsImage);
		int MaxY = MetatileList.size()%16==0 ? MetatileList.size()/16 : (MetatileList.size()/16)+1;
		BufferedImage metatileListImage = new BufferedImage(256, MaxY*16, BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < MaxY*16; y++) {
			for(int x = 0; x < 256; x++) {
				//128 pixels across, rounded up number of metatiles *16 pixels tall
				String binaryString = Integer.toString((y/8)%2) + Integer.toString((x/8)%2);	//binary representation
				int metatileTileIndex = Integer.parseInt(binaryString, 2);	//the index of tile in the metatile
				if ((x/16)+((y/16)*16) < MetatileList.size()) {
					int tileIndexValue = MetatileList.get((x/16)+((y/16)*16))[metatileTileIndex];
					try{
					metatileListImage.setRGB(x, y, GrayScaleColourPalette.get( TileList.get(tileIndexValue)[(x%8)+((y%8)*8)] ).getRGB());
					} catch(IndexOutOfBoundsException e) {
						break;
					}
				}
			}
		}
		try{ImageIO.write(metatileListImage, "PNG", metatileDebugFileForCollisions);} catch(IOException e) {e.printStackTrace();}
	}

	public static boolean compareArrays(Integer[] arr1, Integer[] arr2) {
    	HashSet<Integer> set1 = new HashSet<Integer>(Arrays.asList(arr1));
    	HashSet<Integer> set2 = new HashSet<Integer>(Arrays.asList(arr2));
    	return set1.equals(set2);
	}
}
