package com.strafergame.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationLoader
		extends AsynchronousAssetLoader<Animation<TextureRegion>, AnimationLoader.AnimationLoaderParameter> {

	public AnimationLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, AnimationLoaderParameter parameter) {
	}

	@Override
	public Animation<TextureRegion> loadSync(AssetManager manager, String fileName, FileHandle file,
			AnimationLoaderParameter parameter) {

		return null;

	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AnimationLoaderParameter parameter) {

		return null;
	}

	public static class AnimationLoaderParameter extends AssetLoaderParameters<Animation<TextureRegion>> {
		public int cycleActs;
		public int cycleCount;
		public int scalar;

		public AnimationLoaderParameter(int cycleActs, int cycleCount, int scalar) {
			this.cycleActs = cycleActs;
			this.cycleCount = cycleCount;
			this.scalar = scalar;
		}
	}

}
