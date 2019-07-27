package com.bytecamp.util.bean;

import java.lang.reflect.Field;

public abstract class BeanBase {
	protected int limitStart = -1;
	protected int limitEnd = -1;
	protected int recordCount = 0;
	protected String orderByClause;

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public int getLimitStart() {
		return limitStart;
	}

	public void setLimitStart(int limitStart) {
		this.limitStart = limitStart;
	}

	public int getLimitEnd() {
		return limitEnd;
	}

	public void setLimitEnd(int limitEnd) {
		this.limitEnd = limitEnd;
	}

	private Field[] fs = null;

	public void setProperty(String key, String value) {
		if (fs == null) {
			fs = this.getClass().getDeclaredFields();
		}
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			f.setAccessible(true);
			String type = f.getType().toString();
			if (f.getName().equals(key) && type.endsWith("String")) {
				try {
					f.set(this, value);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 给属性设值
			} else if (f.getName().equals(key) && (type.endsWith("int") || type.endsWith("Integer"))) {
				try {
					f.set(this, Integer.valueOf(value));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 给属性设值
			} else {
				//System.out.println(f.getType() + "\t");
			}

		}
	}
}
