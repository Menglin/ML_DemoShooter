package com.menglin.example;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class Enemy extends Character{
	
	public Enemy(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		m_speed = 5;
	}
	
	public void fn_chasing(float x, float y)
	{
		final float dx = x - m_body.getPosition().x;
		final float dy = y - m_body.getPosition().y;
		final float px = m_speed*(float)Math.cos((float)Math.atan2(dy, dx));
		final float py = m_speed*(float)Math.sin((float)Math.atan2(dy, dx));
		m_body.setLinearVelocity(px + (float)(Math.random()*4 - 4), py + (float)(Math.random()*4 - 3));
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
