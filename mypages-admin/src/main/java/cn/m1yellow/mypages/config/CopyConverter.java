package cn.m1yellow.mypages.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.core.Converter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转换器（当源对象属性类型与目标对象属性类型不一致时，使用转换器）
 */
public class CopyConverter implements Converter {

    @Override
    public Object convert(Object value, Class target, Object context) {
        {
            String s = value.toString();
            if (target.equals(int.class) || target.equals(Integer.class)) {
                return Integer.parseInt(s);
            }
            if (target.equals(long.class) || target.equals(Long.class)) {
                return Long.parseLong(s);
            }
            if (target.equals(float.class) || target.equals(Float.class)) {
                return Float.parseFloat(s);
            }
            if (target.equals(double.class) || target.equals(Double.class)) {
                return Double.parseDouble(s);
            }
            if (target.equals(Date.class)) {
                return new Date(s);
            }
            if (target.equals(BigDecimal.class)) {
                if (!StringUtils.isBlank(s) && !s.equals("NaN")) {
                    return new BigDecimal(s);
                }
            }

            return value;
        }
    }
}
