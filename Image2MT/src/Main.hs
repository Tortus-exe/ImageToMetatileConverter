module Main where

import System.Environment

import Codec.Picture
import Codec.Picture.Png
import Filesystem.Path.CurrentOS

main :: IO ()
main = do
    args <- (decodeString.head) <$> getArgs
    image <- convertRGB16 $ readPng args
    print image
