package com.menglin.example;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Enemy extends Character{
	public Enemy ()
	{
		this.m_health = 80;
		this.m_speed = 2;
	}
	
	public void fn_initSprite(float x, float y, ITextureRegion tr, Scene sc, VertexBufferObjectManager vbom)
	{
		m_Sprite = new Sprite(x, y, tr, vbom);
		sc.attachChild(m_Sprite);
	}
	
	public void fn_initPhysicsBody(PhysicsWorld pw)
	{
		m_PhysicsWorld = pw;
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		m_body = PhysicsFactory.createCircleBody(m_PhysicsWorld, m_Sprite, BodyType.KinematicBody, fixtureDef);
		m_body.setUserData(new UserData(m_Sprite, "Enemy"));
		m_body.setActive(true);
		m_PhysicsWorld.registerPhysicsConnector(new PhysicsConnector(m_Sprite, m_body, true, true));
	}
	
	public void fn_chasing(float x, float y)
	{
		final float dx = x - m_body.getPosition().x;
		final float dy = y - m_body.getPosition().y;
		final float px = m_speed*(float)Math.cos((float)Math.atan2(dy, dx));
		final float py = m_speed*(float)Math.sin((float)Math.atan2(dy, dx));
		m_body.setLinearVelocity(px + (float)(Math.random()*5 - 3), py + (float)(Math.random()*5 - 3));
	}
	
	public void fn_patrolling()
	{
		m_body.setLinearVelocity((float)(Math.random()*10 - 5), (float)(Math.random()*10 - 5));
	}

	/*
	public void fn_initThread(Character Chr)
	{
		m_thread = new Thread(this, "Character");
		m_thread.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		fn_patrolling();
	}
	*/
}
