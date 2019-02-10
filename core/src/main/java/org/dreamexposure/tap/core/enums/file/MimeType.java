package org.dreamexposure.tap.core.enums.file;

/**
 * @author NovaFox161
 * Date Created: 12/15/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public enum MimeType {
    IMAGE("image", "img"), MEDIA("media", "media"), AUDIO("audio", "audio"), VIDEO("video", "video"), JSON("application/json", "text"), TEXT("text/plain", "text");

    private String formalName;
    private String folder;

    MimeType(String _formalName, String _folder) {
        formalName = _formalName;
        folder = _folder;
    }

    public String getFormalName() {
        return formalName;
    }

    public String getFolder() {
        return folder;
    }

    public static String getExtension(String fullMime) {
        switch (fullMime.toLowerCase()) {
            case "text/plain":
                return ".txt";
            case "application/json":
                return ".json";
            case "image/gif":
                return ".gif";
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/tiff":
                return ".tiff";
            case "image/vnd.wap.wbmp":
                return ".wbmp";
            case "image/x-icon":
                return ".ico";
            case "image/x-jng":
                return "jng";
            case "image/x-ms-bmp":
                return ".bmp";
            case "image/svg+xml":
                return ".svg";
            case "image/webp":
                return ".webp";
            case "audio/midi":
                return ".midi";
            case "audio/mpeg":
                return ".mp3";
            case "audio/ogg":
                return ".ogg";
            case "audio/x-m4a":
                return ".m4a";
            case "audio/x-realaudio":
                return ".ra";
            case "video/3gpp":
                return ".3gp";
            case "video/mp2t":
                return ".ts";
            case "video/mp4":
                return ".mp4";
            case "video/mpeg":
                return ".mpg";
            case "video/quicktime":
                return ".mov";
            case "video/webm":
                return ".webm";
            case "video/x-flv":
                return ".flv";
            case "video/x-m4v":
                return ".m4v";
            case "video/x-mng":
                return ".mng";
            case "video/x-ms-asf":
                return ".asf";
            case "video/x-ms-wmv":
                return ".wmv";
            case "video/x-msvideo":
                return ".avi";
            default:
                return ".tmp";
        }
    }
}
