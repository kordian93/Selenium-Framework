package support.utils;

public class Utils {

    public static String extractDomain(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        String domain = url;
        domain = domain
                .replace("http://", "")
                .replace("https://", "");

        int slashIndex = domain.indexOf('/');
        if (slashIndex != -1) {
            domain = domain.substring(0, slashIndex);
        }
        return domain;
    }
}
