/*
 * Copyright (c) 2004, 2005 TADA AB - Taby Sweden
 * Distributed under the terms shown in the file COPYRIGHT
 * found in the root folder of this project or at
 * http://eng.tada.se/osprojects/COPYRIGHT.html
 */
package org.postgresql.pljava.jdbc;

import java.sql.SQLException;

/**
 * A ResultSet base provides methods that are common both for
 * a SyntheticResultSet (which is not associated with a
 * statement) and SPIResultSet.
 *
 * @author Filip Hrbek
 */
abstract class ResultSetBase extends ReadOnlyResultSet
{
	private int m_fetchSize;
	private int m_row;

	ResultSetBase(int fetchSize)
	{
		m_fetchSize = fetchSize;
		m_row = 0;	// First row is 1 so 0 is on undefined position.
	}

	public int getFetchDirection()
	throws SQLException
	{
		return FETCH_FORWARD;
	}

	public final int getFetchSize()
	throws SQLException
	{
		return m_fetchSize;
	}

	public final int getRow()
	throws SQLException
	{
		return m_row;
	}

	public int getType()
	throws SQLException
	{
		return TYPE_FORWARD_ONLY;
	}

	/**
	 * Cursor positoning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public void afterLast()
	throws SQLException
	{
		throw new UnsupportedFeatureException("Cursor positioning");
	}

	/**
	 * Cursor positoning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public void beforeFirst()
	throws SQLException
	{
		throw new UnsupportedFeatureException("Cursor positioning");
	}

	public void close()
	throws SQLException
	{
		m_row = -1;
	}

	/**
	 * Cursor positioning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean first() throws SQLException
	{
		throw new UnsupportedFeatureException("Cursor positioning");
	}

	public boolean isAfterLast() throws SQLException
	{
		return m_row < 0;
	}

	public boolean isBeforeFirst() throws SQLException
	{
		return m_row == 0;
	}

	public boolean isFirst() throws SQLException
	{
		return m_row == 1;
	}

	/**
	 * Cursor positioning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean last()
	throws SQLException
	{
		throw new UnsupportedFeatureException("Cursor positioning");
	}

	/**
	 * Reverse positioning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean previous()
	throws SQLException
	{
		throw new UnsupportedFeatureException("Reverse positioning");
	}

	/**
	 * Cursor positioning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean absolute(int row)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Cursor positioning");
	}

	/**
	 * Cursor positioning is not implemented yet.
	 * @throws SQLException indicating that this feature is not supported.
	 */
	public boolean relative(int rows)
	throws SQLException
	{
		throw new UnsupportedFeatureException("Cursor positioning");
	}

	/**
	 * Only {@link java.sql.ResultSet#FETCH_FORWARD} is supported.
	 * @throws SQLException indicating that this feature is not supported
	 * for other values on <code>direction</code>.
	 */
	public void setFetchDirection(int direction)
	throws SQLException
	{
		if(direction != FETCH_FORWARD)
			throw new UnsupportedFeatureException("Non forward fetch direction");
	}

	public void setFetchSize(int fetchSize)
	throws SQLException
	{
		if(fetchSize <= 0)
			throw new IllegalArgumentException("Illegal fetch size for ResultSet");
		m_fetchSize = fetchSize;
	}

	final void setRow(int row)
	{
		m_row = row;
	}
}