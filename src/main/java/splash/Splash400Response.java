package splash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class that matches Splash 400 json response
 */
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
    private Response400Info response400Info;

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

    public Response400Info getInfo() {
        return response400Info;
    }

    public void setInfo(Response400Info info) {
        this.response400Info = info;
    }
}

