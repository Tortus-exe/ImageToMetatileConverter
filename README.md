# ImageToMetatileConverter

So, this is a converter which converts from an input image, palette file, and screen reorder file into some data which can be read by the NES. 

**Input files:  (these files are placed in the /resources folder)**
- palettes.perScreen: text file containing one line per screen, 16 hex colours per line corresponding to the proper colours on the image. 
- screens.reorder: text file containing one line only, and the order of screens to place (in the example, the first 4 numbers in the file are 0, 1, 4, 5; so the first four screens in order are the first, second, fifth, and sixth screens scrolling from left to right, top to bottom)
- Level.png: literally a png of the level. THIS MUST HAVE A WIDTH THAT IS A MULTIPLE OF 256, AND A HEIGHT THAT IS A MULTIPLE OF 224!!

**Output:**
- tiles.chr: A character rom file containing all the tiles in the entire level
- palettes.png: A png-format visualization of the palettes contained in the palettes file (palettes.perScreen)
- paletteExtrapolated.png: a png-format extrapolation of each palette in each screen (each row is one screen)
- missingColors.txt: a text file displaying errors and missing colours
- tiles.asm: an assembly file dictating the layout of each screen as an array of tiles
- metatiles.asm: an assembly file dictating the layout of each screen as an array of metatiles
- metatileLookups.asm: an assembly file dictating how tiles are arranged to form metatiles (arranged into 4 arrays: top left, top right, bottom left, bottom right)
- metametatiles.asm: an assembly file dictating the layout of each screen as an array of metametatiles
- metametatileLookups.asm: an assembly file dictating how metatiles are arranged to form metametatiles (arranged into 4 arrays: top left, top right, bottom left, bottom right)

There is only one file to run: Main.java
```bash
javac Main.java
java Main
```
this is all that you need to run in the terminal to initialize this app. 
Good luck!!!

- Tortle
