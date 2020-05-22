package splash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Splash400Response {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("info")
    @Expose
    private Info info;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info.toString();
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}

class Info {

    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("line_number")
    @Expose
    private Integer lineNumber;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Info{" +
                "source='" + source + '\'' +
                ", lineNumber=" + lineNumber +
                ", error='" + error + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
