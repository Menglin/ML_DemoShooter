package com.menglin.example;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;

public class ResourcesManager {

	// for option menu
	public static Font m_Font;
	
	// splash texture
	public static BitmapTextureAtlas m_SplashTextureAtlas;
	public static ITextureRegion m_SplashTextureRegion;
	
	// startmenu texture
	public static BitmapTextureAtlas m_StartMenuTextureAtlas;
	public static ITextureRegion m_StartMenuStartTextureRegion;
	public static ITextureRegion m_StartMenuStartTextureRegion2;
	public static ITextureRegion m_StartMenuStartTextureRegion3;
	public static ITextureRegion m_StartMenuQuitTextureRegion;
	public static ITextureRegion m_StartMenuQuitTextureRegion2;
	public static ITextureRegion m_StartMenuQuitTextureRegion3;
	public static ITextureRegion m_StartMenuPicTextureRegion;
	
	// hero texture
	public static BuildableBitmapTextureAtlas m_HeroTextureAtlas;
	public static ITiledTextureRegion m_HeroTiledTextureRegion;
	
	// enemy texture
	public static BuildableBitmapTextureAtlas m_EnemyTextureAtlas;
	public static ITiledTextureRegion m_EnemyTiledTextureRegion;
	
	// background texture
	public static BitmapTextureAtlas m_BGTextureAtlas;
	public static ITextureRegion m_BGTextureRegion;
	
	// on screen control
	public static BitmapTextureAtlas m_OnScreenControlTexture;
	public static ITextureRegion m_OnScreenControlBaseTextureRegion;
	public static ITextureRegion m_OnScreenControlKnobTextureRegion;
	
	// bullet texture
	public static BitmapTextureAtlas m_BulletTextureAtlas;
	public static ITextureRegion m_BulletTextureRegion;
	
	public void fn_loadResources(TextureManager textureManager, FontManager fontManager, Context context)
	{
		// set font
		FontFactory.setAssetBasePath("font/");

		final ITexture fontTexture = new BitmapTextureAtlas(textureManager, 256, 256, TextureOptions.BILINEAR);
		m_Font = FontFactory.createFromAsset(fontManager, fontTexture, context.getAssets(), "Plok.ttf", 48, true, android.graphics.Color.WHITE);
		m_Font.load();
		
		// set pic folder
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		// For splash ////
		m_SplashTextureAtlas = new BitmapTextureAtlas(textureManager, 800, 480, TextureOptions.DEFAULT);
		m_SplashTextureRegion =BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_SplashTextureAtlas,context,"splash.png", 0, 0);
		m_SplashTextureAtlas.load();
		// end splash ////
		
		// for start menu ////
		m_StartMenuTextureAtlas = new BitmapTextureAtlas(textureManager, 800, 580, TextureOptions.DEFAULT);
		m_StartMenuStartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_start.png", 0, 0);
		m_StartMenuStartTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_start2.png", 170, 0);
		m_StartMenuStartTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_start3.png", 340, 0);
		m_StartMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_quit.png", 0, 50);
		m_StartMenuQuitTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_quit2.png", 170, 50);
		m_StartMenuQuitTextureRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_quit3.png", 340, 50);
		m_StartMenuPicTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_StartMenuTextureAtlas,context,"menu_pic.png", 0, 100);
		m_StartMenuTextureAtlas.load();
		// end start menu ////
		
		// for game ////
		m_HeroTextureAtlas = new BuildableBitmapTextureAtlas(textureManager, 32, 32, TextureOptions.DEFAULT);
		m_HeroTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(m_HeroTextureAtlas, context, "hero.png", 1, 1);
		m_HeroTextureAtlas.load();
		try {
			m_HeroTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			m_HeroTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		m_EnemyTextureAtlas = new BuildableBitmapTextureAtlas(textureManager, 64, 32, TextureOptions.DEFAULT);
		m_EnemyTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(m_EnemyTextureAtlas, context, "enemy.png", 2, 1);
		m_EnemyTextureAtlas.load();
		try {
			m_EnemyTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			m_EnemyTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		m_BGTextureAtlas = new BitmapTextureAtlas(textureManager, 2048, 2048, TextureOptions.DEFAULT);
		m_BGTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_BGTextureAtlas, context, "map.jpg", 0, 0);
		m_BGTextureAtlas.load();
		// end game ////
		
		// on screen control
		m_OnScreenControlTexture = new BitmapTextureAtlas(textureManager, 256, 128, TextureOptions.BILINEAR);
		m_OnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_OnScreenControlTexture, context, "onscreen_control_base.png", 0, 0);
		m_OnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_OnScreenControlTexture, context, "onscreen_control_knob.png", 128, 0);
		m_OnScreenControlTexture.load();
		
		// bullet ////
		m_BulletTextureAtlas = new BitmapTextureAtlas(textureManager, 8, 16, TextureOptions.BILINEAR);
		m_BulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(m_BulletTextureAtlas, context, "bullet.png", 0, 0);
		m_BulletTextureAtlas.load();
	}
}
