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
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.content.Context;

public class Bullets {
	
	public void fn_loadRes(TextureManager tm, Context c, String res)
	{
		Bullets.m_BitmapTextureAtlas = new BitmapTextureAtlas(tm, 8, 16, TextureOptions.BILINEAR);
		Bullets.m_TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(Bullets.m_BitmapTextureAtlas, c, res, 0, 0);
		Bullets.m_BitmapTextureAtlas.load();
	}

	public void fn_addBullet(float x, float y, float dx, float dy, Scene sc, VertexBufferObjectManager vbom, PhysicsWorld pw)
	{
		Sprite spr = new Sprite(x, y, Bullets.m_TextureRegion, vbom);
		spr.setRotation(MathUtils.radToDeg((float)Math.atan2(dx, -dy)));
		Body body = PhysicsFactory.createBoxBody(pw, spr, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f));
		body.setBullet(true);
		body.setUserData(new UserData(spr, "Bullet"));
		//body.setLinearVelocity(50*dx/(float)Math.sqrt(dx*dx + dy*dy), 50*dy/(float)Math.sqrt(dx*dx + dy*dy));
		body.setLinearVelocity(50*(float)Math.cos((float)Math.atan2(dy, dx)), 50*(float)Math.sin((float)Math.atan2(dy, dx)));
		sc.attachChild(spr);
		pw.registerPhysicsConnector(new PhysicsConnector(spr, body, true, true));
	}
	
	protected static BitmapTextureAtlas m_BitmapTextureAtlas;
	protected static ITextureRegion m_TextureRegion;
}
