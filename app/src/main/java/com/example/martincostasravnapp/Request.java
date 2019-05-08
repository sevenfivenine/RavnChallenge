package com.example.martincostasravnapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Request
{
	public static final int REQUEST_CODE_CLOSE  = 6999;
	public static final int REQUEST_CODE_EMPTY  = 7000;
	public static final int REQUEST_CODE_LIST   = 7001;
	public static final int REQUEST_CODE_ADD    = 7002;
	public static final int REQUEST_CODE_REMOVE = 7003;
	public static final int REQUEST_CODE_UPDATE = 7004;
	public static final int REQUEST_CODE_SORT   = 7005;

	public static final String KEY_REQUEST_CODE = "requestCode";
	public static final String KEY_RECORD       = "record";
	public static final String KEY_ID           = "id";
	public static final String KEY_ORDER        = "order";
	public static final String KEY_FIELD        = "field";

	public static final String NO_FIELD = "NO_FIELD";


	private int    requestCode;
	private Media  record;
	private UUID   id;
	private int    order;
	private String field;


	public Request()
	{
		id = null;
		order = -1;
		field = NO_FIELD;
	}


	public static Request generateListRequest()
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_LIST );
		return request;
	}


	public static Request generateAddRequest(Media record)
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_ADD );
		request.setRecord( record );
		return request;
	}


	public static Request generateRemoveRequest(UUID id)
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_REMOVE );
		request.setId( id );
		return request;
	}


	public static Request generateUpdateRequest(Media record)
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_UPDATE );
		request.setRecord( record );
		return request;
	}


	public static Request generateSortRequest(String field, int order)
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_SORT );
		request.setField( field );
		request.setOrder( order );
		return request;
	}


	/**
	 * Create an empty request to connect to the server
	 *
	 * @return
	 */
	public static Request empty()
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_EMPTY );
		return request;
	}


	public static Request close()
	{
		Request request = new Request();
		request.setRequestCode( REQUEST_CODE_CLOSE );
		return request;
	}


	public JSONObject toJSONObject() throws JSONException
	{
		JSONObject jsonObject = new JSONObject();

		jsonObject.put( KEY_REQUEST_CODE, requestCode );

		if ( record != null )
		{
			jsonObject.put( KEY_RECORD, record.toJSONObject() );
		}

		if ( id != null )
		{
			jsonObject.put( KEY_ID, id.toString() );
		}

		if ( order != -1 )
		{
			jsonObject.put( KEY_ORDER, order );
		}

		if ( !field.equals( NO_FIELD ) )
		{
			jsonObject.put( KEY_FIELD, field );
		}

		return jsonObject;
	}


	public int getRequestCode()
	{
		return requestCode;
	}


	public void setRequestCode(int requestCode)
	{
		this.requestCode = requestCode;
	}


	public Media getRecord()
	{
		return record;
	}


	public void setRecord(Media record)
	{
		this.record = record;
	}


	public UUID getId()
	{
		return id;
	}


	public void setId(UUID id)
	{
		this.id = id;
	}


	public int getOrder()
	{
		return order;
	}


	public void setOrder(int order)
	{
		this.order = order;
	}


	public String getField()
	{
		return field;
	}


	public void setField(String field)
	{
		this.field = field;
	}
}
