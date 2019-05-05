package com.example.martincostasravnapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Media
{
	public static final String KEY_TYPE       = "type";
	public static final String KEY_TITLE      = "title";
	public static final String KEY_AUTHOR     = "author";
	public static final String KEY_DATE       = "date";
	public static final String KEY_ID         = "id";
	public static final String KEY_VIEW_COUNT = "viewCount";
	public static final String KEY_VIEW_ORDER = "viewOrder";

	private String type, title, author, date;
	private int id, viewCount, viewOrder;


	public Media()
	{

	}


	public Media(String type, String title, String author, String date, int id, int viewCount, int viewOrder)
	{
		this.type = type;
		this.title = title;
		this.author = author;
		this.date = date;
		this.id = id;
		this.viewCount = viewCount;
		this.viewOrder = viewOrder;
	}


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject jsonObject = new JSONObject();

		jsonObject.put( KEY_ID, id );
		jsonObject.put( KEY_TYPE, type );
		jsonObject.put( KEY_TITLE, title );
		jsonObject.put( KEY_AUTHOR, author );
		jsonObject.put( KEY_DATE, date );
		jsonObject.put( KEY_VIEW_COUNT, viewCount );
		jsonObject.put( KEY_VIEW_ORDER, viewOrder );

		return jsonObject;
	}


	public static Media JSONtoMedia(JSONObject jsonObject) throws JSONException, ClassCastException
	{
		Media media = new Media();

		media.setId( (Integer) jsonObject.get( KEY_ID ) );
		media.setType( (String) jsonObject.get( KEY_TYPE ) );
		media.setTitle( (String) jsonObject.get( KEY_TITLE ) );
		media.setAuthor( (String) jsonObject.get( KEY_AUTHOR ) );
		media.setDate( (String) jsonObject.get( KEY_DATE ) );
		media.setViewCount( (Integer) jsonObject.get( KEY_VIEW_COUNT ) );
		media.setViewOrder( (Integer) jsonObject.get( KEY_VIEW_ORDER ) );

		return media;
	}


	public String getType()
	{
		return type;
	}


	public void setType(String type)
	{
		this.type = type;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getAuthor()
	{
		return author;
	}


	public void setAuthor(String author)
	{
		this.author = author;
	}


	public String getDate()
	{
		return date;
	}


	public void setDate(String date)
	{
		this.date = date;
	}


	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	public int getViewCount()
	{
		return viewCount;
	}


	public void setViewCount(int viewCount)
	{
		this.viewCount = viewCount;
	}


	public int getViewOrder()
	{
		return viewOrder;
	}


	public void setViewOrder(int viewOrder)
	{
		this.viewOrder = viewOrder;
	}
}

