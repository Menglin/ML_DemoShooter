package com.menglin.example;

import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;

import android.content.Context;

public class Weapon_Ranged {
	public Weapon_Ranged()
	{
		m_speed = 50;
		m_fireRate = 80;
		m_spread = 0.3f;
	}
	
	public void fn_loadRes(TextureManager tm, Context c, String res)
	{
		Weapon_Ranged.m_BulletTextureAtlas = new BitmapTextureAtlas(tm, 8, 16, TextureOptions.BILINEAR);
		Weapon_Ranged.m_BulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(Weapon_Ranged.m_BulletTextureAtlas, c, res, 0, 0);
		Weapon_Ranged.m_BulletTextureAtlas.load();
	}
	
	public void fn_fire(float x, float y, float dx, float dy, Scene sc, VertexBufferObjectManager vbom, PhysicsWorld pw)
	{
		Bullet bullet = new Bullet(x, y, Weapon_Ranged.m_BulletTextureRegion, vbom);
		bullet.setRotation(MathUtils.radToDeg((float)Math.atan2(dx, -dy)));
		bullet.fn_initPhysicsBody(pw, sc, "Bullet");
		bullet.fn_setLinearVelocity(m_speed*(float)Math.cos((float)Math.atan2(dy+Math.random()*m_spread-m_spread/2, dx+Math.random()*m_spread-m_spread/2)), m_speed*(float)Math.sin((float)Math.atan2(dy+Math.random()*m_spread-m_spread/2, dx+Math.random()*m_spread-m_spread/2)));
		sc.attachChild(bullet);
		//m_bullets.add(bullet);
		Thread t = new Thread(bullet);
		t.start();
	}
	
	protected long m_speed;
	protected long m_fireRate;
	protected float m_spread;
	
	protected static BitmapTextureAtlas m_BulletTextureAtlas;
	protected static ITextureRegion m_BulletTextureRegion;
}
