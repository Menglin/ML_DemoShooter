package com.menglin.example;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Character extends Sprite implements Runnable{

	public Character(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		m_health = 100;
		m_speed = 2;
		
		m_sprite = this;
	}
	
	public void fn_initPhysicsBody(String userdata)
	{
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(100, 0.5f, 0.5f);
		m_body = PhysicsFactory.createCircleBody(MainActivity.m_physicsWorld, this, BodyType.DynamicBody, fixtureDef);
		m_body.setUserData(new UserData(this, userdata));
		m_body.setActive(true);
		m_body.setAngularDamping(1000);
		MainActivity.m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, m_body, true, true));
	}
	
	public void fn_startThread()
	{
		Thread t = new Thread(this, "Character");
		t.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public Vector2 m_WorldPos;
	
	protected int m_health;
	protected int m_speed;
	
	protected Sprite m_sprite;
	protected Body m_body;
}
