package factorio.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.fasterxml.jackson.core.JsonFactory;
import com.intellij.execution.filters.Filter;

public class FactorioDAPJsonFilter implements Filter {
    private final @NotNull  JsonFactory myJsonFactory;

    public FactorioDAPJsonFilter() {
        this.myJsonFactory = new JsonFactory();
    }

    @Override
    public @Nullable Result applyFilter(@NotNull final String line, final int entireLength) {
        /*int jsonStart = line.indexOf('{');
        if (jsonStart == -1 || line.indexOf('}') == -1) return null;

        String jsonStr = line.substring(jsonStart);

        try {
            JsonParser p = myJsonFactory.createParser(jsonStr);

            String dapName;
            boolean isEvent = false;

            while (true) {
                JsonToken t = p.nextToken();
                if (JsonToken.FIELD_NAME == t) {
                    String fieldName = p.currentName();
                    JsonToken val = p.nextToken();
                    String valueStr = p.getText();

                    switch (fieldName) {
                        case "command":
                            isEvent = false;
                            dapName = valueStr;
                        case "event":
                            isEvent = true;
                            dapName = valueStr;
                    }
                }
            }

            return new Result();

        } catch (IOException e) {
            return null;
        }*/
        return null;
    }
}
