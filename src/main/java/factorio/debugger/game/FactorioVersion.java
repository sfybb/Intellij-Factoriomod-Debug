package factorio.debugger.game;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactorioVersion implements Comparable<FactorioVersion> {
    public final int major;
    public final int minor;
    public final int patch;

    private FactorioVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;

    }

    @Override
    public int compareTo(@NotNull final FactorioVersion o) {
        int diff = major - o.major;
        if (diff != 0) return diff;
        diff = minor - o.minor;
        if (diff != 0) return diff;
        return patch - o.patch;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final FactorioVersion other)) return false;
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return formatVersion();
    }

    private String formatVersion() {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append(major);
        strBuilder.append('.');
        strBuilder.append(minor);

        if(patch > 0) {
            strBuilder.append('.');
            strBuilder.append(patch);
        }
        return strBuilder.toString();
    }

    public static @NotNull FactorioVersion parse(@NotNull String versionString) throws IllegalArgumentException {
        String str = versionString.trim();
        final String[] numbers = str.split("\\.");

        int major = 0, minor = 0, patch = 0;

        for (int i = 0; i < numbers.length; i++) {
            int num;
            try {
                num = Integer.parseInt(numbers[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(versionString, e);
            }

            if(i == 0) major = num;
            else if (i == 1) minor = num;
            else if (i == 2) patch = num;
            else throw new IllegalArgumentException(versionString);
        }
        if ((major+minor+patch) == 0) throw new IllegalArgumentException(versionString);

        return new FactorioVersion(major, minor, patch);
    }

    public static @Nullable FactorioVersion tryParse(@Nullable String versionString) {
        if (versionString == null)
            return null;

        try {
            return parse(versionString);
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }
}
