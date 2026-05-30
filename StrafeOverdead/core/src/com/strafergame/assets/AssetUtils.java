package com.strafergame.assets;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * @author mihaisto
 */
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

        for (FileHandle file : dirHandle.list()) {
            if (file.isDirectory()) {
                res.addAll(listFilesInAssets(file.path(), extension));
            } else {
                if (file.extension().equalsIgnoreCase(extension)) {
                    res.add(file.path());
                }
            }
        }

        return res;
    }

}
