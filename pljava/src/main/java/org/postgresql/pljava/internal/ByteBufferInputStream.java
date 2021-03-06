/*
 * Copyright (c) 2018 Tada AB and other contributors, as listed below.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the The BSD 3-Clause License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Contributors:
 *   Chapman Flack
 */
package org.postgresql.pljava.internal;

import java.io.InputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

/**
 * Wrap a readable {@link ByteBuffer} as an {@link InputStream}.
 *<p>
 * An implementing class must provide a {@link #buffer} method that returns the
 * {@code ByteBuffer}, and the method is responsible for knowing when the memory
 * region windowed by the {@code ByteBuffer} is no longer to be accessed, and
 * throwing an exception in that case.
 *<p>
 * The implementing class must supply an object that the {@code InputStream}
 * operations will be {@code synchronized} on, and that must be the same object
 * on which any operations that affect the byte buffer's accessibility will
 * synchronize.
 */
public abstract class ByteBufferInputStream extends InputStream
{
	/**
	 * The object on which the {@code InputStream} operations will synchronize.
	 *<p>
	 * Must be the same object, if any, that operations affecting the byte
	 * buffer's accessibility synchronize on, and may be of any type useful to
	 * the implementing class.
	 * <strong>Every implementing subclass must assign something to
	 * {@code m_state} when created, and then leave it alone as if it were
	 * {@code final}.</strong>
	 */
	protected Object m_state;

	/**
	 * Whether this stream is open; initially true.
	 */
	protected boolean m_open;

	/**
	 * Construct an instance, given an object on which to synchronize.
	 *<p>
	 * Does not require a parameter to initialize {@link m_state} (because if an
	 * implementing subclass needs to create a state object with a reference to
	 * this, Java's restriction on referencing this prior to calling a
	 * superclass constructor could be triggered).
	 *<p>
	 * <strong>Every implementing subclass must assign something to
	 * {@code m_state} when created, and then leave it alone as if it were
	 * {@code final}.</strong>
	 */
	protected ByteBufferInputStream()
	{
		m_open = true;
	}

	/**
	 * Return the {@link ByteBuffer} being wrapped, or throw an exception if the
	 * memory windowed by the buffer should no longer be accessed.
	 *<p>
	 * The monitor on {@link #m_state} is held when this method is called.
	 *<p>
	 * This method also should throw an exception if {@link #m_open} is false.
	 * It is called everywhere that should happen, so it is the perfect place
	 * for the test, and allows the implementing class to use a customized
	 * message in the exception.
	 */
	protected abstract ByteBuffer buffer() throws IOException;

	@Override
	public int read() throws IOException
	{
		synchronized ( m_state )
		{
			ByteBuffer src = buffer();
			if ( 0 < src.remaining() )
				return src.get();
			return -1;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		synchronized ( m_state )
		{
			ByteBuffer src = buffer();
			int has = src.remaining();
			if ( len > has )
			{
				if ( 0 == has )
					return -1;
				len = has;
			}
			src.get(b, off, len);
			return len;
		}
	}

	@Override
	public long skip(long n) throws IOException
	{
		synchronized ( m_state )
		{
			ByteBuffer src = buffer();
			int has = src.remaining();
			if ( n > has )
				n = has;
			src.position(src.position() + (int)n);
			return n;
		}
	}

	@Override
	public int available() throws IOException
	{
		synchronized ( m_state )
		{
			return buffer().remaining();
		}
	}

	@Override
	public void close() throws IOException
	{
		synchronized ( m_state )
		{
			if ( ! m_open )
				return;
			m_open = false;
		}
	}

	@Override
	public void mark(int readlimit)
	{
		synchronized ( m_state )
		{
			if ( ! m_open )
				return;
			try
			{
				buffer().mark();
			}
			catch ( IOException e )
			{
				/*
				 * The contract is for mark to throw no checked exception.
				 * An exception caught here probably means the state's no longer
				 * live, which will be signaled to the caller if another,
				 * throwing, method is then called. If not, no harm no foul.
				 */
			}
		}
	}

	@Override
	public void reset() throws IOException
	{
		synchronized ( m_state )
		{
			if ( ! m_open )
				return;
			try
			{
				buffer().reset();
			}
			catch ( InvalidMarkException e )
			{
				throw new IOException("reset attempted when mark not set");
			}
		}
	}

	/**
	 * Return {@code true}; this class does support {@code mark} and
	 * {@code reset}.
	 */
	@Override
	public boolean markSupported()
	{
		return true;
	}
}
