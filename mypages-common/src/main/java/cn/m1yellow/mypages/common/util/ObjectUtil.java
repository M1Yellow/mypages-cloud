package cn.m1yellow.mypages.common.util;

import java.lang.reflect.Field;

public class ObjectUtil {

    /**
     * 获取对象字符串
     *
     * @param obj 操作对象
     * @return 对象字符串
     */
    public static String getString(Object obj) {
        if (obj == null) return null;
        return obj.toString().trim();
    }

    /**
     * 对象中的 String 字段去两边空格
     *
     * @param obj 操作对象
     */
    public static void stringFiledTrim(Object obj) {
        if (obj == null) return;
        try {
            //Field[] fields = ReflectUtil.getFields(obj.getClass()); // 包括父类字段
            // TODO 实体类通常是单个类，不用取父类字段
            Field[] fields = getFieldsOfObj(obj);
            for (Field field : fields) {
                if (field.getType().equals(String.class)) {
                    field.setAccessible(true);
                    Object objVal = field.get(obj);
                    if (objVal != null) {
                        //String newVal = String.valueOf(objVal).trim();
                        field.set(obj, String.valueOf(objVal).trim());
                    }
                    field.setAccessible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过反射获取对象本身内部所有的字段，不包括父类继承的
     *
     * @param obj 操作对象
     * @return 字段列表
     */
    public static Field[] getFieldsOfObj(Object obj) {
        if (obj == null) return null;
        return obj.getClass().getDeclaredFields();
    }

}
