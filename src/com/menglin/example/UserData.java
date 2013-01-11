package com.menglin.example;

import org.andengine.entity.shape.IAreaShape;

public class UserData {
	public UserData(IAreaShape spr, String lab)
	{
		m_pAreaShape = spr;
		m_label = lab;
	}
	
	public IAreaShape m_pAreaShape;
	public String m_label;
}
