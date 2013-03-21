package de.shop.util;

import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Log
public class LogInterceptor implements Serializable {
	private static final long serialVersionUID = 6225006198548883927L;

	private static final String COUNT = "Anzahl = ";
	private static final int MAX_ELEM = 4; 
	private static final int CHAR_POST_AFTER_GET_SET = 3; 
	private static final int CHAR_POST_AFTER_IS = 2; 

	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {

		final Object bean = ctx.getTarget();
		final Class<?> clazz = bean.getClass();
		final String classname = clazz.getName();
		final Logger logger = Logger.getLogger(classname);

		if (!logger.isLoggable(FINER)) {
			return ctx.proceed();
		}

		String methodName = ctx.getMethod().getName();

		if ((methodName.startsWith("get") || methodName.startsWith("set"))
				&& Character.isUpperCase(methodName
						.charAt(CHAR_POST_AFTER_GET_SET))) {
			return ctx.proceed();
		}
		if ((methodName.startsWith("is"))
				&& Character.isUpperCase(methodName.charAt(CHAR_POST_AFTER_IS))) {
			return ctx.proceed();
		}
		if (("toString".equals(methodName))
				&& Character.isUpperCase(methodName.charAt(CHAR_POST_AFTER_IS))) {
			return ctx.proceed();
		}

		final Object[] params = ctx.getParameters();

		final StringBuilder sb = new StringBuilder();
		if (params != null) {
			final int anzahlParams = params.length;
			sb.append(": ");
			for (int i = 0; i < anzahlParams; i++) {
				if (params[i] == null) {
					sb.append("null");
				} 
				else {
					final String paramStr = toString(params[i]);
					sb.append(paramStr);
				}
				sb.append(", ");
			}
			final int laenge = sb.length();
			sb.delete(laenge - 2, laenge - 1);
		}
		logger.log(FINER, methodName + " BEGINN" + sb);

		Object result = null;
		result = ctx.proceed();

		if (result == null) {
			logger.log(FINER, methodName + " ENDE");
		} 
		else {
			final String resultStr = toString(result);
			logger.log(FINER, methodName + " ENDE: " + resultStr);
		}

		return result;
	}

	private static String toString(Object obj) {
		if (obj instanceof Collection<?>) {
			final Collection<?> coll = (Collection<?>) obj;
			final int anzahl = coll.size();
			if (anzahl > MAX_ELEM) {
				return COUNT + coll.size();
			}

			return coll.toString();
		}

		if (obj.getClass().isArray()) {
			final String str = arrayToString(obj);
			return str;
		}

		return obj.toString();
	}

	private static String arrayToString(Object obj) {
		final Class<?> componentClass = obj.getClass().getComponentType();

		if (!componentClass.isPrimitive()) {
			final Object[] arr = (Object[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				if (arr[i] == null) {
					sbEnd.append("null");
				}
				else {
					sbEnd.append(arr[i]);
				}
				sbEnd.append(", ");
			}
			if (anzahl > 0) {
				final int laenge = sbEnd.length();
				sbEnd.delete(laenge - 2, laenge - 1);
			}
			sbEnd.append("]");
			return sbEnd.toString();
		}

		
		if ("short".equals(componentClass.getName())) {
			final short[] arr = (short[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			if (anzahl > 0) {
				sbEnd.delete(laenge - 2, laenge - 1);
				sbEnd.append("]");
			}
			return sbEnd.toString();
		}

		if ("int".equals(componentClass.getName())) {
			final int[] arr = (int[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			if (anzahl > 0) {
				sbEnd.delete(laenge - 2, laenge - 1);
				sbEnd.append("]");
			}
			return sbEnd.toString();
		}

		if ("long".equals(componentClass.getName())) {
			final long[] arr = (long[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			if (anzahl > 0) {
				sbEnd.delete(laenge - 2, laenge - 1);
				sbEnd.append("]");
			}
			return sbEnd.toString();
		}

		if ("byte".equals(componentClass.getName())) {
			return "<byte-array>";
		}

		if ("float".equals(componentClass.getName())) {
			final float[] arr = (float[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			if (anzahl > 0) {
				final int laenge = sbEnd.length();
				sbEnd.delete(laenge - 2, laenge - 1);
			}
			sbEnd.append("]");
			return sbEnd.toString();
		}

		if ("double".equals(componentClass.getName())) {
			final double[] arr = (double[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			if (anzahl > 0) {
				final int laenge = sbEnd.length();
				sbEnd.delete(laenge - 2, laenge - 1);
			}
			sbEnd.append("]");
			return sbEnd.toString();
		}

		if ("char".equals(componentClass.getName())) {
			final char[] arr = (char[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			if (anzahl > 0) {
				final int laenge = sbEnd.length();
				sbEnd.delete(laenge - 2, laenge - 1);
			}
			sbEnd.append("]");
			return sbEnd.toString();
		}

		if ("boolean".equals(componentClass.getName())) {
			final boolean[] arr = (boolean[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			if (anzahl > 0) {
				final int laenge = sbEnd.length();
				sbEnd.delete(laenge - 2, laenge - 1);
			}
			sbEnd.append("]");
			return sbEnd.toString();
		}

		return "<<UNKNOWN ARRAY>>";
	}
}
