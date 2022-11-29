module Main where

import System.Environment
import Codec.Picture

import Data.Array.Dynamic
import qualified Data.Vector.Storable as V

process :: Array a -> Array a
process = transpose [0,2,1,3,4]

-- (⊂⍤1)¨(⊂⍤3)3 4{1 3 2 4 5⍉((3,⍨⍺,⍨⍺÷⍨2↑⍴⍵))⍴⍵}3 1 2⍉3 12 12⍴'rgb'∘.,⍳12×12

convertImage :: DynamicImage -> IO ()
convertImage x = do
    img <- return $ convertRGB16 x
    b <- (return.imageData) img
    arr <- return $ process $ fromList [
        (imageWidth img)`div`256, 
        (imageHeight img)`div`224, 
        256,224,3] $ V.toList b
    print $ shapeL arr

checkForBadSize :: DynamicImage -> IO ()
checkForBadSize x = do
    img <- return $ convertRGB16 x
    if ((imageWidth img) `mod` 256 /= 0) || ((imageHeight img) `mod` 224 /= 0) then putStrLn "bad image size!"
        else convertImage x

main :: IO ()
main = do
    args <- head <$> getArgs
    attemptedImage <- readPng args
    either putStrLn checkForBadSize attemptedImage
