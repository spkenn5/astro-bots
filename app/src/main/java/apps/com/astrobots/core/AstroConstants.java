package apps.com.astrobots.core;

/**
 * Created by kenji on 1/22/17.
 */

public class AstroConstants {
    public static final String TV_GUIDE_API = "http://ams-api.astro.com.my/ams/v3/getEvents?channelId=%s&periodStart=%s&periodEnd=%s";
    public static final String CHANNEL_LIST_API = "http://ams-api.astro.com.my/ams/v3/getChannelList";

    public static final String LOCAL_API_ADD = "http://localhost:3000/api/astro-services/add?user_id=%s&user_name=321&user_email=%s&user_favorite_channel=%s&user_favorite_program=%s";
    public static final String LOCAL_API_GET = "";
    public static final String LOCAL_API_DELETE = "";

    public static final int SORT_NAME = 1;
    public static final int SORT_ID = 2;
    public static final int SORT_STB = 3;
}
