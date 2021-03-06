package com.zixin.ssm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * åºåè¯å·¥å?
 */
public class ProtoStuffSerializerUtil {

	/**
	 * åºååå¯¹è±?
	 * @param obj
	 * @return
	 */
	public static <T> byte[] serialize(T obj) {
		if (obj == null) {
			throw new RuntimeException("åºååå¯¹è±?(" + obj + ")!");
		}
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
		byte[] protostuff = null;
		try {
			protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new RuntimeException("åºåå?(" + obj.getClass() + ")å¯¹è±¡(" + obj + ")åçå¼å¸¸!", e);
		} finally {
			buffer.clear();
		}
		return protostuff;
	}

	/**
	 * ååºååå¯¹è±¡
	 * @param paramArrayOfByte
	 * @param targetClass
	 * @return
	 */
	public static <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
		if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
			throw new RuntimeException("ååºååå¯¹è±¡åçå¼å¸¸,byteåºåä¸ºç©º!");
		}
		T instance = null;
		try {
			instance = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("ååºååè¿ç¨ä¸­ä¾æ®ç±»ååå»ºå¯¹è±¡å¤±è´?!", e);
		}
		Schema<T> schema = RuntimeSchema.getSchema(targetClass);
		ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
		return instance;
	}

	/**
	 * åºåååè¡?
	 * @param objList
	 * @return
	 */
	public static <T> byte[] serializeList(List<T> objList) {
		if (objList == null || objList.isEmpty()) {
			throw new RuntimeException("åºååå¯¹è±¡åè¡?(" + objList + ")åæ°å¼å¸¸!");
		}
		@SuppressWarnings("unchecked")
		Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
		LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
		byte[] protostuff = null;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
			protostuff = bos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("åºååå¯¹è±¡åè¡?(" + objList + ")åçå¼å¸¸!", e);
		} finally {
			buffer.clear();
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return protostuff;
	}

	/**
	 * ååºåååè¡¨
	 * @param paramArrayOfByte
	 * @param targetClass
	 * @return
	 */
	public static <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
		if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
			throw new RuntimeException("ååºååå¯¹è±¡åçå¼å¸¸,byteåºåä¸ºç©º!");
		}

		Schema<T> schema = RuntimeSchema.getSchema(targetClass);
		List<T> result = null;
		try {
			result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
		} catch (IOException e) {
			throw new RuntimeException("ååºååå¯¹è±¡åè¡¨åçå¼å¸¸!", e);
		}
		return result;
	}

}