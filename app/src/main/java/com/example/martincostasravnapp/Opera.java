package com.example.martincostasravnapp;

public class Opera
{

	private String name, composer, subgenre;

	public Opera(String name, String composer, String subgenre)
	{
		this.name = name;
		this.composer = composer;
		this.subgenre = subgenre;
	}

	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getComposer()
	{
		return composer;
	}


	public void setComposer(String composer)
	{
		this.composer = composer;
	}


	public String getSubgenre()
	{
		return subgenre;
	}


	public void setSubgenre(String subgenre)
	{
		this.subgenre = subgenre;
	}
}
