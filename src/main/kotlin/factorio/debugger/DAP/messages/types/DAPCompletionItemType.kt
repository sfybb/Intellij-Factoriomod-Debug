package factorio.debugger.DAP.messages.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DAPCompletionItemType {
    @JsonProperty("method") METHOD,
    @JsonProperty("function") FUNCTION,
    @JsonProperty("constructor") CONSTRUCTOR,
    @JsonProperty("field") FIELD,
    @JsonProperty("variable") VARIABLE,
    @JsonProperty("class") CLASS,
    @JsonProperty("interface") INTERFACE,
    @JsonProperty("module") MODULE,
    @JsonProperty("property") PROPERTY,
    @JsonProperty("unit") UNIT,
    @JsonProperty("value") VALUE,
    @JsonProperty("enum") ENUM,
    @JsonProperty("keyword") KEYWORD,
    @JsonProperty("snippet") SNIPPET,
    @JsonProperty("text") TEXT,
    @JsonProperty("color") COLOR,
    @JsonProperty("file") FILE,
    @JsonProperty("reference") REFERENCE,
    @JsonProperty("customcolor") CUSTOM_COLOR;
}
