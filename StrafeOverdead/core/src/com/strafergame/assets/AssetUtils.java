package com.strafergame.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class AssetUtils {
	   /**
     * Retrieves a list of file paths within the specified directory and its subdirectories
     * that have the given extension.
     *
     * @param directory the directory to search in
     * @param extension the file extension to match
     * @return an array of file paths
     */
	public static Array<String> listFilesInAssets(String directory, String extension) {
	    Array<String> res = new Array<>();
	    FileHandle dirHandle = Gdx.files.internal(directory);

	    if (dirHandle.isDirectory()) {
	        for (FileHandle file : dirHandle.list()) {
	            if (file.isDirectory()) {
	                // Recursively list files in subdirectories
	                Array<String> subDirectoryFiles = listFilesInAssets(file.path(), extension);
	                res.addAll(subDirectoryFiles); // Accumulate the results from subdirectories
	            } else {
	                String relativePath = file.path();
	                if (file.extension().equalsIgnoreCase(extension)) {
	                    res.add(relativePath);
	                }
	            }
	        }
	    }
	    
	    return res;
	}

}