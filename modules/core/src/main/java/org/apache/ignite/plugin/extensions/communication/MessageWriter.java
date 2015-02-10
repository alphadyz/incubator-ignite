/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.plugin.extensions.communication;

import org.apache.ignite.lang.*;

import java.nio.*;
import java.util.*;

/**
 * Communication message writer.
 * <p>
 * Allows to customize the binary format of communication messages.
 */
public interface MessageWriter {
    /**
     * Sets but buffer to write to.
     *
     * @param buf Byte buffer.
     */
    public void setBuffer(ByteBuffer buf);

    /**
     * Writes {@code byte} value.
     *
     * @param name Field name.
     * @param val {@code byte} value.
     * @return Whether value was fully written.
     */
    public boolean writeByte(String name, byte val);

    /**
     * Writes {@code short} value.
     *
     * @param name Field name.
     * @param val {@code short} value.
     * @return Whether value was fully written.
     */
    public boolean writeShort(String name, short val);

    /**
     * Writes {@code int} value.
     *
     * @param name Field name.
     * @param val {@code int} value.
     * @return Whether value was fully written.
     */
    public boolean writeInt(String name, int val);

    /**
     * Writes {@code long} value.
     *
     * @param name Field name.
     * @param val {@code long} value.
     * @return Whether value was fully written.
     */
    public boolean writeLong(String name, long val);

    /**
     * Writes {@code float} value.
     *
     * @param name Field name.
     * @param val {@code float} value.
     * @return Whether value was fully written.
     */
    public boolean writeFloat(String name, float val);

    /**
     * Writes {@code double} value.
     *
     * @param name Field name.
     * @param val {@code double} value.
     * @return Whether value was fully written.
     */
    public boolean writeDouble(String name, double val);

    /**
     * Writes {@code char} value.
     *
     * @param name Field name.
     * @param val {@code char} value.
     * @return Whether value was fully written.
     */
    public boolean writeChar(String name, char val);

    /**
     * Writes {@code boolean} value.
     *
     * @param name Field name.
     * @param val {@code boolean} value.
     * @return Whether value was fully written.
     */
    public boolean writeBoolean(String name, boolean val);

    /**
     * Writes {@code byte} array.
     *
     * @param name Field name.
     * @param val {@code byte} array.
     * @return Whether array was fully written.
     */
    public boolean writeByteArray(String name, byte[] val);

    /**
     * Writes {@code short} array.
     *
     * @param name Field name.
     * @param val {@code short} array.
     * @return Whether array was fully written.
     */
    public boolean writeShortArray(String name, short[] val);

    /**
     * Writes {@code int} array.
     *
     * @param name Field name.
     * @param val {@code int} array.
     * @return Whether array was fully written.
     */
    public boolean writeIntArray(String name, int[] val);

    /**
     * Writes {@code long} array.
     *
     * @param name Field name.
     * @param val {@code long} array.
     * @return Whether array was fully written.
     */
    public boolean writeLongArray(String name, long[] val);

    /**
     * Writes {@code float} array.
     *
     * @param name Field name.
     * @param val {@code float} array.
     * @return Whether array was fully written.
     */
    public boolean writeFloatArray(String name, float[] val);

    /**
     * Writes {@code double} array.
     *
     * @param name Field name.
     * @param val {@code double} array.
     * @return Whether array was fully written.
     */
    public boolean writeDoubleArray(String name, double[] val);

    /**
     * Writes {@code char} array.
     *
     * @param name Field name.
     * @param val {@code char} array.
     * @return Whether array was fully written.
     */
    public boolean writeCharArray(String name, char[] val);

    /**
     * Writes {@code boolean} array.
     *
     * @param name Field name.
     * @param val {@code boolean} array.
     * @return Whether array was fully written.
     */
    public boolean writeBooleanArray(String name, boolean[] val);

    /**
     * Writes {@link String}.
     *
     * @param name Field name.
     * @param val {@link String}.
     * @return Whether value was fully written.
     */
    public boolean writeString(String name, String val);

    /**
     * Writes {@link BitSet}.
     *
     * @param name Field name.
     * @param val {@link BitSet}.
     * @return Whether value was fully written.
     */
    public boolean writeBitSet(String name, BitSet val);

    /**
     * Writes {@link UUID}.
     *
     * @param name Field name.
     * @param val {@link UUID}.
     * @return Whether value was fully written.
     */
    public boolean writeUuid(String name, UUID val);

    /**
     * Writes {@link IgniteUuid}.
     *
     * @param name Field name.
     * @param val {@link IgniteUuid}.
     * @return Whether value was fully written.
     */
    public boolean writeIgniteUuid(String name, IgniteUuid val);

    /**
     * Writes {@code enum} value.
     *
     * @param name Field name.
     * @param val {@code enum} value.
     * @return Whether value was fully written.
     */
    public boolean writeEnum(String name, Enum<?> val);

    /**
     * Writes nested message.
     *
     * @param name Field name.
     * @param val Message.
     * @return Whether value was fully written.
     */
    public boolean writeMessage(String name, MessageAdapter val);

    /**
     * Writes array of objects.
     *
     * @param name Field name.
     * @param arr Array of objects.
     * @param itemCls Array component type.
     * @return Whether array was fully written.
     */
    public <T> boolean writeObjectArray(String name, T[] arr, Class<T> itemCls);

    /**
     * Writes collection.
     *
     * @param name Field name.
     * @param col Collection.
     * @param itemCls Collection item type.
     * @return Whether value was fully written.
     */
    public <T> boolean writeCollection(String name, Collection<T> col, Class<T> itemCls);

    /**
     * Writes map.
     *
     * @param name Field name.
     * @param map Map.
     * @param keyCls Map key type.
     * @param valCls Map value type.
     * @return Whether value was fully written.
     */
    public <K, V> boolean writeMap(String name, Map<K, V> map, Class<K> keyCls, Class<V> valCls);
}