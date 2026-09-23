package raccoonman.reterraforged.data.worldgen.preset.settings;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

/**
 * 1.20.1 backport helper. DFU 6's optionalFieldOf is lenient: a present-but-invalid value silently becomes
 * "absent" (so the default is used). FTF targets DFU 8, where optionalFieldOf is strict and reports the error.
 * This restores FTF's strict semantics for fields whose invalid values should be rejected.
 */
final class StrictOptionalField {

	private StrictOptionalField() {
	}

	static <A> MapCodec<Optional<A>> of(String name, Codec<A> codec) {
		return new MapCodec<Optional<A>>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.of(ops.createString(name));
			}

			@Override
			public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
				T value = input.get(name);
				if (value == null) {
					return DataResult.success(Optional.empty());
				}
				return codec.parse(ops, value).map(Optional::of);
			}

			@Override
			public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return input.isPresent() ? prefix.add(name, codec.encodeStart(ops, input.get())) : prefix;
			}

			@Override
			public String toString() {
				return "StrictOptionalField[" + name + ": " + codec + "]";
			}
		};
	}

	static <A> MapCodec<A> of(String name, Codec<A> codec, A defaultValue) {
		return of(name, codec).xmap(
			(value) -> value.orElse(defaultValue),
			(value) -> Objects.equals(value, defaultValue) ? Optional.empty() : Optional.of(value)
		);
	}
}
