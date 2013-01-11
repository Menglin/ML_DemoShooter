package com.menglin.example;


import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.content.Context;

public class Character{// implements Runnable{
	
	public Character()
	{
		m_health = 100;
		m_speed = 10;
	}
	
	public void fn_loadRes(TextureManager tm, Context c, String res)
	{
		Character.m_BitmapTextureAtlas = new BitmapTextureAtlas(tm, 32, 32, TextureOptions.BILINEAR);
		Character.m_TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(Character.m_BitmapTextureAtlas, c, res, 0, 0);
		Character.m_BitmapTextureAtlas.load();
	}
	
	public void fn_initSprite(float x, float y, Scene sc, VertexBufferObjectManager vbom)
	{
		m_Sprite = new Sprite(x, y, Character.m_TextureRegion, vbom);
		sc.attachChild(m_Sprite);
	}
	
	public void fn_initPhysicsBody(PhysicsWorld pw)
	{
		m_PhysicsWorld = pw;
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		m_body = PhysicsFactory.createCircleBody(m_PhysicsWorld, m_Sprite, BodyType.KinematicBody, fixtureDef);
		m_body.setUserData(new UserData(m_Sprite, "Character"));
		m_body.setActive(true);
		m_PhysicsWorld.registerPhysicsConnector(new PhysicsConnector(m_Sprite, m_body, true, true));
	}
	
	/*
	public void fn_initThread()
	{
		m_thread = new Thread(this, "Character");
		m_thread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	*/
	
	protected int m_health;
	protected int m_speed;
	
	static protected BitmapTextureAtlas m_BitmapTextureAtlas;
	static protected ITextureRegion m_TextureRegion;
	protected Sprite m_Sprite;
	protected Body m_body;
	
	protected PhysicsWorld m_PhysicsWorld;
	
	//protected Thread m_thread;
}
