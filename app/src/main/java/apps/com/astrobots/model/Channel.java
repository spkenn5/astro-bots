package apps.com.astrobots.model;

/**
 * Created by kenji on 1/17/17.
 */

public class Channel {
    private String channelId;
    private String channelTitle;
    private String channelStbNumber;

    public Channel(String channelId, String channelTitle, String channelStbNumber) {
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.channelStbNumber = channelStbNumber;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getChannelStbNumber() {
        return channelStbNumber;
    }

    public void setChannelStbNumber(String channelStbNumber) {
        this.channelStbNumber = channelStbNumber;
    }
}
