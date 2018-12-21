package com.blazemeter.jmeter.citrix.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumHelper {

	private EnumHelper() {
	}

	public static <T extends Enum<T>> List<EnumWrapper<T>> getWrappers(Class<T> clazz, Set<T> filter,
			boolean includeNone) {
		Stream<T> stream = Arrays.stream(clazz.getEnumConstants());
		if (filter != null) {
			stream = stream.filter(filter::contains);
		}
		List<EnumWrapper<T>> result = stream.map(v -> new EnumWrapper<T>(clazz, v)).collect(Collectors.toList());
		if (includeNone) {
			result.add(0, new EnumWrapper<T>(clazz, null));
		}
		return result;
	}

	public static <T extends Enum<T>> List<EnumWrapper<T>> getWrappers(Class<T> clazz, boolean includeNone) {
		return getWrappers(clazz, null, includeNone);
	}

	public static <T extends Enum<T>> EnumWrapper<T>[] getWrappersAsArray(Class<T> clazz, Set<T> filter,
			boolean includeNone) {
		List<EnumWrapper<T>> wrappers = getWrappers(clazz, filter, includeNone);
		@SuppressWarnings("unchecked")
		EnumWrapper<T>[] array = new EnumWrapper[wrappers.size()];
		return wrappers.toArray(array);
	}

	public static <T extends Enum<T>> EnumWrapper<T>[] getWrappersAsArray(Class<T> clazz, boolean includeNone) {
		return getWrappersAsArray(clazz, null, includeNone);
	}

	public static <T extends Enum<T>> EnumWrapper<T> lookup(EnumWrapper<T>[] array, T value) {
		EnumWrapper<T> result = null;
		int index = 0;
		while (index < array.length && result == null) {
			EnumWrapper<T> current = array[index];
			if (current.getEnumValue() == value) {
				result = current;
			} else {
				index++;
			}
		}
		return result;
	}

	public static class EnumWrapper<T extends Enum<T>> {
		private final Class<T> clazz;
		private final T enumValue;

		public T getEnumValue() {
			return enumValue;
		}

		public String getLabel() {
			String suffix = enumValue != null ? enumValue.name().toLowerCase() : "none";
			return CitrixUtils.getResString("enum_helper_" + clazz.getSimpleName().toLowerCase() + "_" + suffix, false);
		}

		public EnumWrapper(Class<T> clazz, T value) {
			this.clazz = clazz;
			this.enumValue = value;
		}

		@Override
		public String toString() {
			return getLabel();
		}

	}
}
